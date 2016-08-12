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

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

/**
 * The payload for the ldap identity provider configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _LdapConfiguration extends AbstractExternalIdentityProviderConfiguration {

    /**
     * The auto add group flag
     */
    @JsonProperty("autoAddGroups")
    abstract Optional<Boolean> getAutoAddGroups();

    /**
     * The URL to the ldap server, must start with ldap:// or ldaps://
     */
    @JsonProperty("baseUrl")
    abstract String getBaseUrl();

    /**
     * If you specified BindUserDN, then specify the corresponding password to be used for binding here.
     */
    @JsonProperty("bindPassword")
    abstract Optional<String> getBindPassword();

    /**
     * The distinguished name the gatekeeper uses to bind to the LDAP server.
     */
    @JsonProperty("bindUserDn")
    abstract Optional<String> getBindUserDistinguishedName();

    /**
     * The group role attribute
     */
    @JsonProperty("groupRoleAttribute")
    abstract Optional<String> getGroupRoleAttribute();

    /**
     * The group search base
     */
    @JsonProperty("groupSearchBase")
    abstract Optional<String> getGroupSearchBase();

    /**
     * The maximum group search depth limit
     */
    @JsonProperty("maxGroupSearchDepth")
    abstract Optional<Integer> getGroupSearchDepthLimit();

    /**
     * The group search filter
     */
    @JsonProperty("groupSearchFilter")
    abstract Optional<String> getGroupSearchFilter();

    /**
     * The group search subtree
     */
    @JsonProperty("groupSearchSubTree")
    abstract Optional<Boolean> getGroupSearchSubTree();

    /**
     * The group ignore partial search result flag
     */
    @JsonProperty("groupsIgnorePartialResults")
    abstract Optional<Boolean> getGroupsIgnorePartialResults();

    /**
     * The file to be used for group integration.
     */
    @JsonProperty("ldapGroupFile")
    abstract LdapGroupFile getLdapGroupFile();

    /**
     * The file to be used for configuring the LDAP authentication.
     */
    @JsonProperty("ldapProfileFile")
    abstract LdapProfileFile getLdapProfileFile();

    /**
     *
     */
    @JsonProperty("localPasswordCompare")
    abstract Optional<Boolean> getLocalPasswordCompare();

    /**
     * The name of the LDAP attribute that contains the user’s email address
     */
    @JsonProperty("mailAttributeName")
    abstract Optional<String> getMailAttributeName();

    /**
     * Defines an email pattern containing a {0} to generate an email address for an LDAP user during authentication
     */
    @JsonProperty("mailSubstitute")
    abstract Optional<String> getMailSubstitute();

    /**
     * Set to true if you wish to override an LDAP user email address with a generated one
     */
    @JsonProperty("mailSubstituteOverridesLdap")
    abstract Optional<Boolean> getMailSubstituteOverridesLdap();

    /**
     * The password attribute name
     */
    @JsonProperty("passwordAttributeName")
    abstract Optional<String> getPasswordAttributeName();

    /**
     * The password encoder
     */
    @JsonProperty("passwordEncoder")
    abstract Optional<String> getPasswordEncoder();

    /**
     * Configures the UAA LDAP referral behavior. The following values are possible: - follow → Referrals are followed - ignore → Referrals are ignored and the partial result is returned - throw → An
     * error is thrown and the authentication is aborted
     */
    @JsonProperty("referral")
    abstract Optional<String> getReferral();

    /**
     * Skips validation of the LDAP cert if set to true.
     */
    @JsonProperty("skipSSLVerification")
    abstract Optional<Boolean> getSkipSSLVerification();

    /**
     * The user distinguished name pattern
     */
    @JsonProperty("userDNPattern")
    abstract Optional<String> getUserDistinguishedNamePattern();

    /**
     * The user distinguished name pattern delimiter
     */
    @JsonProperty("userDNPatternDelimiter")
    abstract Optional<String> getUserDistinguishedNamePatternDelimiter();

    /**
     * The user search base
     */
    @JsonProperty("userSearchBase")
    abstract Optional<String> getUserSearchBase();

    /**
     * The user search filter
     */
    @JsonProperty("userSearchFilter")
    abstract Optional<String> getUserSearchFilter();

}
