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

package org.cloudfoundry.uaa.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.uaa.Versioned;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

/**
 * The request payload for the update user operation
 */
@Value.Immutable
abstract class _UpdateUserRequest implements Versioned {

    /**
     * The version
     */
    @Override
    public abstract Optional<String> getVersion();

    @Value.Check
    void check() {
        getVersion()
            .orElseThrow(() -> new IllegalStateException("Cannot build UpdateUserRequest, some of required attributes are not set [version]"));

        if (!getName().getFamilyName().isPresent() || getName().getFamilyName().get().isEmpty()) {
            throw new IllegalStateException("Cannot build CreateUserRequest, required attribute familyName is not set, or is empty");
        }

        if (!getName().getGivenName().isPresent() || getName().getGivenName().get().isEmpty()) {
            throw new IllegalStateException("Cannot build CreateUserRequest, required attribute givenName is not set, or is empty");
        }
    }

    /**
     * Whether the user is active
     */
    @JsonProperty("active")
    abstract Optional<Boolean> getActive();

    /**
     * The emails for the user
     */
    @JsonProperty("emails")
    abstract List<Email> getEmails();

    /**
     * The external id
     */
    @JsonProperty("externalId")
    abstract Optional<String> getExternalId();

    /**
     * The id
     */
    @JsonProperty("id")
    @JsonIgnore
    abstract String getId();

    /**
     * The user's name
     */
    @JsonProperty("name")
    abstract Name getName();

    /**
     * The identity provider that authenticated this user
     */
    @JsonProperty("origin")
    abstract Optional<String> getOrigin();

    /**
     * The user name
     */
    @JsonProperty("userName")
    abstract String getUserName();

    /**
     * Whether the user's email is verified
     */
    @JsonProperty("verified")
    abstract Optional<Boolean> getVerified();

}
