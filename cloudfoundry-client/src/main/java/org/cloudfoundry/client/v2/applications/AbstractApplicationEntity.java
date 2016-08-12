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
import org.cloudfoundry.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * The core entity response payload for Application resources
 */
public abstract class AbstractApplicationEntity {

    /**
     * The buildpack
     */
    @JsonProperty("buildpack")
    public abstract Optional<String> getBuildpack();

    /**
     * The command
     */
    @JsonProperty("command")
    public abstract Optional<String> getCommand();

    /**
     * The console
     */
    @Deprecated
    @JsonProperty("console")
    public abstract Optional<Boolean> getConsole();

    /**
     * Debug
     */
    @Deprecated
    @JsonProperty("debug")
    public abstract Optional<Boolean> getDebug();

    /**
     * The detected start command
     */
    @JsonProperty("detected_start_command")
    public abstract Optional<String> getDetectedStartCommand();

    /**
     * Diego
     */
    @JsonProperty("diego")
    public abstract Optional<Boolean> getDiego();

    /**
     * The disk quota in megabytes
     */
    @JsonProperty("disk_quota")
    public abstract Optional<Integer> getDiskQuota();

    /**
     * The docker credentials JSONs
     */
    @JsonProperty("docker_credentials_json")
    @Nullable
    public abstract Map<String, Object> getDockerCredentialsJsons();

    /**
     * The docker image
     */
    @JsonProperty("docker_image")
    public abstract Optional<String> getDockerImage();

    /**
     * The environment JSONs
     */
    @JsonProperty("environment_json")
    @Nullable
    public abstract Map<String, Object> getEnvironmentJsons();

    /**
     * The health check timeout
     */
    @JsonProperty("health_check_timeout")
    public abstract Optional<Integer> getHealthCheckTimeout();

    /**
     * The health check type
     */
    @JsonProperty("health_check_type")
    public abstract Optional<String> getHealthCheckType();

    /**
     * The instances
     */
    @JsonProperty("instances")
    public abstract Optional<Integer> getInstances();

    /**
     * The memory in megabytes
     */
    @JsonProperty("memory")
    public abstract Optional<Integer> getMemory();

    /**
     * The name
     */
    @JsonProperty("name")
    public abstract Optional<String> getName();

    /**
     * Production
     */
    @Deprecated
    @JsonProperty("production")
    public abstract Optional<Boolean> getProduction();

    /**
     * The space id
     */
    @JsonProperty("space_guid")
    public abstract Optional<String> getSpaceId();

    /**
     * The stack id
     */
    @JsonProperty("stack_guid")
    public abstract Optional<String> getStackId();

    /**
     * The staging failed description
     */
    @JsonProperty("staging_failed_description")
    public abstract Optional<String> getStagingFailedDescription();

    /**
     * The staging failed reason
     */
    @JsonProperty("staging_failed_reason")
    public abstract Optional<String> getStagingFailedReason();

    /**
     * The staging task id
     */
    @JsonProperty("staging_task_id")
    public abstract Optional<String> getStagingTaskId();

    /**
     * The state
     */
    @JsonProperty("state")
    public abstract Optional<String> getState();

}

