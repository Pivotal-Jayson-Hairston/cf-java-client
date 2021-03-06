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

package org.cloudfoundry.reactor.client.v3.applications;

import org.cloudfoundry.client.v3.applications.ApplicationsV3;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletRequest;
import org.cloudfoundry.client.v3.applications.AssignApplicationDropletResponse;
import org.cloudfoundry.client.v3.applications.CancelApplicationTaskRequest;
import org.cloudfoundry.client.v3.applications.CancelApplicationTaskResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationTaskRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationTaskResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationPackagesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationProcessesResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationTasksResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.applications.ScaleApplicationRequest;
import org.cloudfoundry.client.v3.applications.ScaleApplicationResponse;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationResponse;
import org.cloudfoundry.client.v3.applications.StopApplicationRequest;
import org.cloudfoundry.client.v3.applications.StopApplicationResponse;
import org.cloudfoundry.client.v3.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationResponse;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.v3.AbstractClientV3Operations;
import reactor.core.publisher.Mono;

/**
 * The Reactor-based implementation of {@link ApplicationsV3}
 */
public final class ReactorApplicationsV3 extends AbstractClientV3Operations implements ApplicationsV3 {

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorApplicationsV3(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<AssignApplicationDropletResponse> assignDroplet(AssignApplicationDropletRequest request) {
        return put(request, AssignApplicationDropletResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "current_droplet"));
    }

    @Override
    public Mono<CancelApplicationTaskResponse> cancelTask(CancelApplicationTaskRequest request) {
        return put(request, CancelApplicationTaskResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "tasks", request.getTaskId(), "cancel"));
    }

    @Override
    public Mono<CreateApplicationResponse> create(CreateApplicationRequest request) {
        return post(request, CreateApplicationResponse.class, builder -> builder.pathSegment("v3", "apps"));
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId()));
    }

    @Override
    public Mono<GetApplicationResponse> get(GetApplicationRequest request) {
        return get(request, GetApplicationResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId()));
    }

    @Override
    public Mono<GetApplicationEnvironmentResponse> getEnvironment(GetApplicationEnvironmentRequest request) {
        return get(request, GetApplicationEnvironmentResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "env"));
    }

    @Override
    public Mono<GetApplicationProcessResponse> getProcess(GetApplicationProcessRequest request) {
        return get(request, GetApplicationProcessResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "processes", request.getType()));
    }

    @Override
    public Mono<GetApplicationProcessStatisticsResponse> getProcessStatistics(GetApplicationProcessStatisticsRequest request) {
        return get(request, GetApplicationProcessStatisticsResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "processes", request.getType(), "stats"));
    }

    @Override
    public Mono<GetApplicationTaskResponse> getTask(GetApplicationTaskRequest request) {
        return get(request, GetApplicationTaskResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "tasks", request.getTaskId()));
    }

    @Override
    public Mono<ListApplicationsResponse> list(ListApplicationsRequest request) {
        return get(request, ListApplicationsResponse.class, builder -> builder.pathSegment("v3", "apps"));
    }

    @Override
    public Mono<ListApplicationDropletsResponse> listDroplets(ListApplicationDropletsRequest request) {
        return get(request, ListApplicationDropletsResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "droplets"));
    }

    @Override
    public Mono<ListApplicationPackagesResponse> listPackages(ListApplicationPackagesRequest request) {
        return get(request, ListApplicationPackagesResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "packages"));
    }

    @Override
    public Mono<ListApplicationProcessesResponse> listProcesses(ListApplicationProcessesRequest request) {
        return get(request, ListApplicationProcessesResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "processes"));
    }

    @Override
    public Mono<ListApplicationTasksResponse> listTasks(ListApplicationTasksRequest request) {
        return get(request, ListApplicationTasksResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "tasks"));
    }

    @Override
    public Mono<ScaleApplicationResponse> scale(ScaleApplicationRequest request) {
        return put(request, ScaleApplicationResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "processes", request.getType(), "scale"));
    }

    @Override
    public Mono<StartApplicationResponse> start(StartApplicationRequest request) {
        return put(request, StartApplicationResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "start"));
    }

    @Override
    public Mono<StopApplicationResponse> stop(StopApplicationRequest request) {
        return put(request, StopApplicationResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "stop"));
    }

    @Override
    public Mono<Void> terminateInstance(TerminateApplicationInstanceRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId(), "processes", request.getType(), "instances", request.getIndex()));
    }

    @Override
    public Mono<UpdateApplicationResponse> update(UpdateApplicationRequest request) {
        return patch(request, UpdateApplicationResponse.class, builder -> builder.pathSegment("v3", "apps", request.getApplicationId()));
    }

}
