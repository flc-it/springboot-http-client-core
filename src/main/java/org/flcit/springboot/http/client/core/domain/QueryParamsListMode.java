/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flcit.springboot.http.client.core.domain;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public enum QueryParamsListMode {

    /**
     * ?names=v1,v2,v3
     */
    JOIN_VALUES,
    /**
     * ?names=v1&amp;names=v2&amp;names=v3
     */
    ONE_VALUE;

}
