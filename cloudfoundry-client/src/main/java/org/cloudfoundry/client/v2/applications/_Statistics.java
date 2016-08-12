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

package org.cloudfoundry.client.v2.applications;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@JsonDeserialize
@Value.Immutable
abstract class _Statistics {

    /**
     * The application disk quota
     */
    @JsonProperty("disk_quota")
    abstract Optional<Long> getDiskQuota();

    /**
     * The application file descriptor quota
     */
    @JsonProperty("fds_quota")
    abstract Optional<Integer> getFdsQuota();

    /**
     * The application host
     */
    @JsonProperty("host")
    abstract Optional<String> getHost();

    /**
     * The application memory quota
     */
    @JsonProperty("mem_quota")
    abstract Optional<Long> getMemoryQuota();

    /**
     * The application name
     */
    @JsonProperty("name")
    abstract Optional<String> getName();

    /**
     * The application port
     */
    @JsonProperty("port")
    abstract Optional<Integer> getPort();

    /**
     * The application uptime
     */
    @JsonProperty("uptime")
    abstract Optional<Long> getUptime();

    /**
     * The application uris
     */
    @JsonProperty("uris")
    @Nullable
    abstract List<String> getUris();

    /**
     * The application usage
     */
    @JsonProperty("usage")
    abstract Optional<Usage> getUsage();

}
