/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.operations.routes;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.GetDomainResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsRequest;
import org.cloudfoundry.client.v2.routes.ListRouteApplicationsResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesResponse;
import org.cloudfoundry.client.v2.routes.RouteEntity;
import org.cloudfoundry.client.v2.routes.RouteExistsRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceRoutesResponse;
import org.cloudfoundry.operations.routes.ListRoutesRequest.Level;
import org.cloudfoundry.operations.util.Exceptions;
import org.cloudfoundry.operations.util.Validators;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple3;
import reactor.rx.Stream;

import java.util.List;

public final class DefaultRoutes implements Routes {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> organizationId;

    private final Mono<String> spaceId;

    public DefaultRoutes(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.organizationId = organizationId;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Boolean> check(CheckRouteRequest request) {
        return Validators
                .validate(request)
                .and(this.organizationId)
                .then(requestDomainIdCheckRoute(this.cloudFoundryClient))
                .then(requestCheckRoute(this.cloudFoundryClient))
                .defaultIfEmpty(false);
    }

    public Mono<Void> create(CreateRouteRequest request) {
        return Validators
                .validate(request)
                .and(this.organizationId)
                .then(requestDomainIdAndSpaceIdWithContext(this.cloudFoundryClient))
                .then(requestCreateRoute(this.cloudFoundryClient));
    }

    @Override
    public Publisher<Route> list(ListRoutesRequest request) {
        return Validators
                .validate(request)
                .flatMap(requestRoutesResources(this.cloudFoundryClient, this.organizationId, this.spaceId))
                .flatMap(requestAuxiliaryContent(this.cloudFoundryClient));
    }

    @Override
    public Mono<Void> map(MapRouteRequest request) {
        return Validators
                .validate(request)
                .and(this.spaceId)
                .then(requestRouteIdAndApplicationId(this.cloudFoundryClient, this.organizationId))
                .then(requestAssociateRouteWithApplication(this.cloudFoundryClient))
                .after();
    }

    private static Function<ApplicationResource, String> extractApplicationName() {
        return new Function<ApplicationResource, String>() {

            @Override
            public String apply(ApplicationResource resource) {
                return Resources.getEntity(resource).getName();
            }

        };
    }

    private static Function<GetDomainResponse, String> extractDomainName() {
        return new Function<GetDomainResponse, String>() {

            @Override
            public String apply(GetDomainResponse response) {
                return Resources.getEntity(response).getName();
            }

        };
    }

    private static Function<GetSpaceResponse, String> extractSpaceName() {
        return new Function<GetSpaceResponse, String>() {

            @Override
            public String apply(GetSpaceResponse response) {
                return Resources.getEntity(response).getName();
            }

        };
    }

    private static Mono<String> requestApplicationId(CloudFoundryClient cloudFoundryClient, MapRouteRequest request, String spaceId) {
        return requestSpaceApplication(cloudFoundryClient, request.getApplicationName(), spaceId)
                .map(Resources.extractId());
    }

    private static Mono<List<String>> requestApplicationNames(CloudFoundryClient cloudFoundryClient, RouteResource routeResource) {
        return Paginated
                .requestResources(requestApplicationsPage(cloudFoundryClient, routeResource))
                .map(extractApplicationName())
                .toList();
    }

    private static Function<Integer, Mono<ListRouteApplicationsResponse>> requestApplicationsPage(final CloudFoundryClient cloudFoundryClient, final RouteResource resource) {
        return new Function<Integer, Mono<ListRouteApplicationsResponse>>() {

            @Override
            public Mono<ListRouteApplicationsResponse> apply(Integer page) {
                ListRouteApplicationsRequest request = ListRouteApplicationsRequest.builder()
                        .id(Resources.getId(resource))
                        .page(page)
                        .build();

                return cloudFoundryClient.routes().listApplications(request);
            }

        };
    }

    private static Function<Tuple2<String, String>, Mono<AssociateApplicationRouteResponse>> requestAssociateRouteWithApplication(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<String, String>, Mono<AssociateApplicationRouteResponse>>() {
            @Override
            public Mono<AssociateApplicationRouteResponse> apply(Tuple2<String, String> tuple) {
                String routeId = tuple.t1;
                String applicationId = tuple.t2;

                AssociateApplicationRouteRequest request = AssociateApplicationRouteRequest.builder()
                        .id(applicationId)
                        .routeId(routeId)
                        .build();

                return cloudFoundryClient.applicationsV2().associateRoute(request);
            }
        };
    }

    private static Function<RouteResource, Mono<Route>> requestAuxiliaryContent(final CloudFoundryClient cloudFoundryClient) {
        return new Function<RouteResource, Mono<Route>>() {

            @Override
            public Mono<Route> apply(RouteResource routeResource) {
                return Mono
                        .when(requestApplicationNames(cloudFoundryClient, routeResource), requestDomainName(cloudFoundryClient, routeResource), requestSpaceName(cloudFoundryClient, routeResource))
                        .map(toRoute(routeResource));
            }

        };
    }

    private static Function<Tuple2<String, CheckRouteRequest>, Mono<Boolean>> requestCheckRoute(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<String, CheckRouteRequest>, Mono<Boolean>>() {

            @Override
            public Mono<Boolean> apply(Tuple2<String, CheckRouteRequest> tuple) {
                String domainId = tuple.t1;
                CheckRouteRequest checkRouteRequest = tuple.t2;

                RouteExistsRequest request = RouteExistsRequest.builder()
                        .domainId(domainId)
                        .host(checkRouteRequest.getHost())
                        .path(checkRouteRequest.getPath())
                        .build();

                return cloudFoundryClient.routes().exists(request);
            }

        };
    }

    private static Function<Tuple3<String, String, CreateRouteRequest>, Mono<Void>> requestCreateRoute(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple3<String, String, CreateRouteRequest>, Mono<Void>>() {

            @Override
            public Mono<Void> apply(Tuple3<String, String, CreateRouteRequest> tuple) {
                String spaceId = tuple.t1;
                String domainId = tuple.t2;
                CreateRouteRequest request = tuple.t3;

                return requestCreateRoute(cloudFoundryClient, domainId, request.getHost(), request.getPath(), spaceId)
                        .after();
            }

        };
    }

    private static Function<String, Mono<CreateRouteResponse>> requestCreateRoute(final CloudFoundryClient cloudFoundryClient, final String host, final String path, final String spaceId) {
        return new Function<String, Mono<CreateRouteResponse>>() {

            @Override
            public Mono<CreateRouteResponse> apply(String domainId) {
                return requestCreateRoute(cloudFoundryClient, domainId, host, path, spaceId);
            }

        };
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String path, String spaceId) {
        org.cloudfoundry.client.v2.routes.CreateRouteRequest request = org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                .domainId(domainId)
                .host(host)
                .path(path)
                .spaceId(spaceId)
                .build();

        return cloudFoundryClient.routes().create(request);
    }

    private static Mono<String> requestCreateRouteId(CloudFoundryClient cloudFoundryClient, MapRouteRequest request, Mono<String> organizationId, String spaceId) {
        return organizationId
                .then(requestDomainIdAssociateRoute(cloudFoundryClient, request.getDomain()))
                .then(requestCreateRoute(cloudFoundryClient, request.getHost(), request.getPath(), spaceId))
                .map(Resources.extractId());
    }

    private static Mono<String> requestDomainId(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return requestPrivateDomainsResources(cloudFoundryClient, organizationId, domain)
                .switchIfEmpty(requestSharedDomainsResources(cloudFoundryClient, domain))
                .map(Resources.extractId())
                .singleOrEmpty();
    }

    private static Function<Tuple2<CreateRouteRequest, String>, Mono<Tuple3<String, String, CreateRouteRequest>>> requestDomainIdAndSpaceIdWithContext(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<CreateRouteRequest, String>, Mono<Tuple3<String, String, CreateRouteRequest>>>() {

            @Override
            public Mono<Tuple3<String, String, CreateRouteRequest>> apply(Tuple2<CreateRouteRequest, String> tuple) {
                CreateRouteRequest request = tuple.t1;
                String organizationId = tuple.t2;

                return Mono.when(requestSpaceId(cloudFoundryClient, organizationId, request.getSpace()), requestDomainIdCreateRoute(cloudFoundryClient, organizationId, request.getDomain()),
                        Mono.just(request));
            }

        };
    }

    private static Function<String, Mono<String>> requestDomainIdAssociateRoute(final CloudFoundryClient cloudFoundryClient, final String domain) {
        return new Function<String, Mono<String>>() {

            @Override
            public Mono<String> apply(String organizationId) {
                return requestDomainId(cloudFoundryClient, organizationId, domain);
            }

        };
    }

    private static Function<Tuple2<CheckRouteRequest, String>, Mono<Tuple2<String, CheckRouteRequest>>> requestDomainIdCheckRoute(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<CheckRouteRequest, String>, Mono<Tuple2<String, CheckRouteRequest>>>() {

            @Override
            public Mono<Tuple2<String, CheckRouteRequest>> apply(Tuple2<CheckRouteRequest, String> tuple) {
                CheckRouteRequest request = tuple.t1;
                String organizationId = tuple.t2;

                return requestDomainId(cloudFoundryClient, organizationId, request.getDomain())
                        .and(Mono.just(request));
            }

        };
    }

    private static Mono<String> requestDomainIdCreateRoute(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return requestDomainId(cloudFoundryClient, organizationId, domain)
                .otherwiseIfEmpty(Mono.<String>error(new IllegalArgumentException(String.format("Domain %s does not exist", domain))));
    }

    private static Mono<String> requestDomainName(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        GetDomainRequest request = GetDomainRequest.builder()
                .id(Resources.getEntity(resource).getDomainId())
                .build();

        return cloudFoundryClient.domains().get(request)
                .map(extractDomainName());
    }

    private static Function<Integer, Mono<ListRoutesResponse>> requestOrganizationRoutesPage(final CloudFoundryClient cloudFoundryClient, final String organizationId) {
        return new Function<Integer, Mono<ListRoutesResponse>>() {

            @Override
            public Mono<ListRoutesResponse> apply(Integer page) {
                org.cloudfoundry.client.v2.routes.ListRoutesRequest request = org.cloudfoundry.client.v2.routes.ListRoutesRequest.builder()
                        .organizationId(organizationId)
                        .page(page)
                        .build();

                return cloudFoundryClient.routes().list(request);
            }

        };
    }

    private static Function<String, Stream<RouteResource>> requestOrganizationRoutesResources(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Stream<RouteResource>>() {

            @Override
            public Stream<RouteResource> apply(String organizationId) {
                return Paginated
                        .requestResources(requestOrganizationRoutesPage(cloudFoundryClient, organizationId));
            }

        };
    }

    private static Function<Integer, Mono<ListOrganizationPrivateDomainsResponse>> requestPrivateDomainsPage(final CloudFoundryClient cloudFoundryClient, final String organizationId,
                                                                                                             final String domain) {
        return new Function<Integer, Mono<ListOrganizationPrivateDomainsResponse>>() {

            @Override
            public Mono<ListOrganizationPrivateDomainsResponse> apply(Integer page) {
                ListOrganizationPrivateDomainsRequest request = ListOrganizationPrivateDomainsRequest.builder()
                        .id(organizationId)
                        .name(domain)
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().listPrivateDomains(request);
            }

        };
    }

    private static Stream<DomainResource> requestPrivateDomainsResources(CloudFoundryClient cloudFoundryClient, String organizationId, String domain) {
        return Paginated
                .requestResources(requestPrivateDomainsPage(cloudFoundryClient, organizationId, domain))
                .cast(DomainResource.class);
    }

    private static Function<Tuple2<MapRouteRequest, String>, Mono<Tuple2<String, String>>> requestRouteIdAndApplicationId(final CloudFoundryClient cloudFoundryClient, final Mono<String>
            organizationId) {
        return new Function<Tuple2<MapRouteRequest, String>, Mono<Tuple2<String, String>>>() {

            @Override
            public Mono<Tuple2<String, String>> apply(Tuple2<MapRouteRequest, String> tuple) {
                MapRouteRequest request = tuple.t1;
                String spaceId = tuple.t2;

                return Mono
                        .when(requestCreateRouteId(cloudFoundryClient, request, organizationId, spaceId), requestApplicationId(cloudFoundryClient, request, spaceId));
            }

        };
    }

    private static Function<ListRoutesRequest, Stream<RouteResource>> requestRoutesResources(final CloudFoundryClient cloudFoundryClient, final Mono<String> organizationId,
                                                                                             final Mono<String> spaceId) {
        return new Function<ListRoutesRequest, Stream<RouteResource>>() {

            @Override
            public Stream<RouteResource> apply(ListRoutesRequest request) {
                if (Level.ORGANIZATION == request.getLevel()) {
                    return Stream
                            .from(organizationId
                                    .flatMap(requestOrganizationRoutesResources(cloudFoundryClient)));
                } else {
                    return Stream
                            .from(spaceId
                                    .flatMap(requestSpaceRoutesResources(cloudFoundryClient)));
                }
            }

        };
    }

    private static Function<Integer, Mono<ListSharedDomainsResponse>> requestSharedDomainsPage(final CloudFoundryClient cloudFoundryClient, final String domain) {
        return new Function<Integer, Mono<ListSharedDomainsResponse>>() {

            @Override
            public Mono<ListSharedDomainsResponse> apply(Integer page) {
                ListSharedDomainsRequest request = ListSharedDomainsRequest.builder()
                        .name(domain)
                        .page(page)
                        .build();

                return cloudFoundryClient.sharedDomains().list(request);
            }

        };
    }

    private static Stream<DomainResource> requestSharedDomainsResources(CloudFoundryClient cloudFoundryClient, String domain) {
        return Paginated
                .requestResources(requestSharedDomainsPage(cloudFoundryClient, domain))
                .cast(DomainResource.class);
    }

    private static Mono<ApplicationResource> requestSpaceApplication(CloudFoundryClient cloudFoundryClient, String applicationName, String spaceId) {
        return Paginated
                .requestResources(requestSpaceApplicationsPage(cloudFoundryClient, spaceId, applicationName))
                .single();
    }

    private static Function<Integer, Mono<ListSpaceApplicationsResponse>> requestSpaceApplicationsPage(final CloudFoundryClient cloudFoundryClient, final String spaceId,
                                                                                                       final String applicationName) {
        return new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

            @Override
            public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                        .id(spaceId)
                        .name(applicationName)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listApplications(request);
            }

        };
    }

    private static Mono<String> requestSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return Paginated
                .requestResources(requestSpacesPage(cloudFoundryClient, organizationId, space))
                .map(Resources.extractId())
                .single()
                .otherwise(Exceptions.<String>convert(String.format("Space %s does not exist", space)));
    }

    private static Mono<String> requestSpaceName(CloudFoundryClient cloudFoundryClient, RouteResource resource) {
        GetSpaceRequest request = GetSpaceRequest.builder()
                .id(Resources.getEntity(resource).getSpaceId())
                .build();

        return cloudFoundryClient.spaces().get(request)
                .map(extractSpaceName());
    }

    private static Function<Integer, Mono<ListSpaceRoutesResponse>> requestSpaceRoutesPage(final CloudFoundryClient cloudFoundryClient, final String spaceId) {
        return new Function<Integer, Mono<ListSpaceRoutesResponse>>() {

            @Override
            public Mono<ListSpaceRoutesResponse> apply(Integer page) {
                ListSpaceRoutesRequest request = ListSpaceRoutesRequest.builder()
                        .id(spaceId)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listRoutes(request);
            }

        };
    }

    private static Function<String, Stream<RouteResource>> requestSpaceRoutesResources(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Stream<RouteResource>>() {

            @Override
            public Stream<RouteResource> apply(String spaceId) {
                return Paginated
                        .requestResources(requestSpaceRoutesPage(cloudFoundryClient, spaceId));
            }

        };
    }

    private static Function<Integer, Mono<ListOrganizationSpacesResponse>> requestSpacesPage(final CloudFoundryClient cloudFoundryClient, final String organizationId, final String
            spaceName) {
        return new Function<Integer, Mono<ListOrganizationSpacesResponse>>() {

            @Override
            public Mono<ListOrganizationSpacesResponse> apply(Integer page) {
                ListOrganizationSpacesRequest request = ListOrganizationSpacesRequest.builder()
                        .id(organizationId)
                        .name(spaceName)
                        .page(page)
                        .build();

                return cloudFoundryClient.organizations().listSpaces(request);
            }

        };
    }

    private static Function<Tuple3<List<String>, String, String>, Route> toRoute(final RouteResource resource) {
        return new Function<Tuple3<List<String>, String, String>, Route>() {

            @Override
            public Route apply(Tuple3<List<String>, String, String> tuple) {
                List<String> applications = tuple.t1;
                String domain = tuple.t2;
                String space = tuple.t3;

                RouteEntity routeEntity = Resources.getEntity(resource);

                return Route.builder()
                        .applications(applications)
                        .domain(domain)
                        .host(routeEntity.getHost())
                        .path(routeEntity.getPath())
                        .routeId(Resources.getId(resource))
                        .space(space)
                        .build();
            }
        };
    }

}