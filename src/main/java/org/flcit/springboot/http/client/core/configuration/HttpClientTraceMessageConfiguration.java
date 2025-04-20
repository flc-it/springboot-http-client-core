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

package org.flcit.springboot.http.client.core.configuration;

import org.flcit.commons.core.util.BooleanUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class HttpClientTraceMessageConfiguration {

    private Boolean active;
    private Boolean headers;
    private Boolean body;
    private Integer maxLength;

    /**
     * 
     */
    public HttpClientTraceMessageConfiguration() { }

    /**
     * @param configuration
     */
    public HttpClientTraceMessageConfiguration(HttpClientTraceMessageConfiguration configuration) {
        this.active = configuration.active;
        this.headers = configuration.headers;
        this.body = configuration.body;
        this.maxLength = configuration.maxLength;
    }

    /**
     * @return
     */
    public boolean isActive() {
        return !Boolean.FALSE.equals(active)
                && BooleanUtils.hasOneTrue(headers, body);
    }
    /**
     * @param active
     * @return
     */
    public HttpClientTraceMessageConfiguration setActive(Boolean active) {
        this.active = active;
        return this;
    }
    /**
     * @return
     */
    public Boolean getHeaders() {
        return headers;
    }
    /**
     * @param headers
     * @return
     */
    public HttpClientTraceMessageConfiguration setHeaders(Boolean headers) {
        this.headers = headers;
        return this;
    }
    /**
     * @return
     */
    public Boolean getBody() {
        return body;
    }
    /**
     * @param body
     * @return
     */
    public HttpClientTraceMessageConfiguration setBody(Boolean body) {
        this.body = body;
        return this;
    }
    /**
     * @return
     */
    public Integer getMaxLength() {
        return maxLength;
    }
    /**
     * @param maxLength
     * @return
     */
    public HttpClientTraceMessageConfiguration setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

}
