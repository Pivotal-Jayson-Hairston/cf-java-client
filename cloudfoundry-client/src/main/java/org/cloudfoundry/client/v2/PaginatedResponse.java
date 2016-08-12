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

package org.cloudfoundry.client.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Base class for requests that are paginated
 *
 * @param <T> the resource type
 */
public abstract class PaginatedResponse<T extends Resource<?>> {

    /**
     * The next url
     */
    @JsonProperty("next_url")
    public abstract Optional<String> getNextUrl();

    /**
     * The previous url
     */
    @JsonProperty("prev_url")
    public abstract Optional<String> getPreviousUrl();

    /**
     * The resources
     */
    @JsonProperty("resources")
    @Nullable
    public abstract List<T> getResources();

    /**
     * The total pages
     */
    @JsonProperty("total_pages")
    public abstract Optional<Integer> getTotalPages();

    /**
     * The total results
     */
    @JsonProperty("total_results")
    public abstract Optional<Integer> getTotalResults();

}
