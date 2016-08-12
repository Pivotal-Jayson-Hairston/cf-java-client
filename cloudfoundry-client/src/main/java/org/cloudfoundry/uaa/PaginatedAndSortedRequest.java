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

package org.cloudfoundry.uaa;

import org.cloudfoundry.QueryParameter;

import java.util.Optional;

/**
 * Base class for requests that are paginated
 */
public abstract class PaginatedAndSortedRequest {

    /**
     * The number of results per page
     */
    @QueryParameter("count")
    public abstract Optional<Integer> getCount();

    /**
     * The filter
     */
    @QueryParameter("filter")
    public abstract Optional<String> getFilter();

    /**
     * The sort order
     */
    @QueryParameter("sortOrder")
    public abstract Optional<SortOrder> getSortOrder();

    /**
     * The start index
     */
    @QueryParameter("startIndex")
    public abstract Optional<Integer> getStartIndex();

}
