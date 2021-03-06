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

package org.cloudfoundry.reactor.uaa.authorizations;

import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.ResponseType;
import org.cloudfoundry.uaa.authorizations.Authorizations;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByImplicitGrantBrowserRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithAuthorizationCodeGrantRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithIdTokenRequest;
import org.cloudfoundry.uaa.authorizations.AuthorizeByOpenIdWithImplicitGrantRequest;
import org.cloudfoundry.util.ExceptionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * The Reactor-based implementation of {@link Authorizations}
 */
public final class ReactorAuthorizations extends AbstractUaaOperations implements Authorizations {

    private static final AsciiString LOCATION = new AsciiString("Location");

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     */
    public ReactorAuthorizations(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    @Override
    public Mono<String> authorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.CODE))
            .map(inbound -> inbound.responseHeaders().get(LOCATION))
            .then(location -> uriParameterValue(location, "code"));
    }

    @Override
    public Mono<String> authorizationCodeGrantBrowser(AuthorizeByAuthorizationCodeGrantBrowserRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.CODE),
            outbound -> {
                outbound.headers().remove(AUTHORIZATION);
                return outbound;
            })
            .map(inbound -> inbound.responseHeaders().get(LOCATION));
    }

    @Override
    public Mono<String> implicitGrantBrowser(AuthorizeByImplicitGrantBrowserRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.TOKEN),
            outbound -> {
                outbound.headers().remove(AUTHORIZATION);
                return outbound;
            })
            .map(inbound -> inbound.responseHeaders().get(LOCATION));
    }

    @Override
    public Mono<String> openIdWithAuthorizationCodeGrant(AuthorizeByOpenIdWithAuthorizationCodeGrantRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.ID_TOKEN_CODE),
            outbound -> {
                outbound.headers().remove(AUTHORIZATION);
                return outbound;
            })
            .map(inbound -> inbound.responseHeaders().get(LOCATION));
    }

    @Override
    public Mono<String> openIdWithIdToken(AuthorizeByOpenIdWithIdTokenRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.ID_TOKEN))
            .map(inbound -> inbound.responseHeaders().get(LOCATION));
    }

    @Override
    public Mono<String> openIdWithImplicitGrant(AuthorizeByOpenIdWithImplicitGrantRequest request) {
        return get(request, builder -> builder.pathSegment("oauth", "authorize").queryParam("response_type", ResponseType.ID_TOKEN_TOKEN))
            .map(inbound -> inbound.responseHeaders().get(LOCATION));
    }

    private static Mono<String> uriParameterValue(String uriString, String parameter) {
        return Optional.ofNullable(UriComponentsBuilder.fromUriString(uriString).build().getQueryParams().getFirst(parameter))
            .map(Mono::just)
            .orElse(ExceptionUtils.illegalState(String.format("Parameter %s not in URI %s", parameter, uriString)));
    }

}
