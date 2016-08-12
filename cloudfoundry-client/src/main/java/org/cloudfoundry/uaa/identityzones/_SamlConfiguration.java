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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * The payload for the identity zone saml configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _SamlConfiguration {

    /**
     * If true, the SAML provider will sign all assertions.
     */
    @JsonProperty("assertionSigned")
    abstract Optional<Boolean> getAssertionSigned();

    /**
     * The lifetime of a SAML assertion in seconds.
     */
    @JsonProperty("assertionTimeToLiveSeconds")
    abstract Optional<Integer> getAssertionTimeToLive();

    /**
     * Exposed SAML metadata property. The certificate used to sign all communications.
     */
    @JsonProperty("certificate")
    abstract Optional<String> getCertificate();

    /**
     * Exposed SAML metadata property. The SAML provider’s private key.
     */
    @JsonProperty("privateKey")
    abstract Optional<String> getPrivateKey();

    /**
     * Exposed SAML metadata property. The SAML provider’s private key password. Reserved for future use.
     */
    @JsonProperty("privateKeyPassword")
    abstract Optional<String> getPrivateKeyPassword();

    /**
     * Exposed SAML metadata property. If true, the service provider will sign all outgoing authentication requests.
     */
    @JsonProperty("requestSigned")
    abstract Optional<Boolean> getRequestSigned();

    /**
     * Exposed SAML metadata property. If true, all assertions received by the SAML provider must be signed.
     */
    @JsonProperty("wantAssertionSigned")
    abstract Optional<Boolean> getWantAssertionSigned();

    /**
     * If true, the authentication request from the partner service provider must be signed.
     */
    @JsonProperty("wantAuthnRequestSigned")
    abstract Optional<Boolean> getWantPartnerAuthenticationRequestSigned();

}
