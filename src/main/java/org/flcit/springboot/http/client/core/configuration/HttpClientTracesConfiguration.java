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

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class HttpClientTracesConfiguration {

    private Boolean active;
    private HttpClientTraceMessageConfiguration request;
    private HttpClientTraceMessageConfiguration response;

    /**
     * 
     */
    public HttpClientTracesConfiguration() {}

    /**
     * @param configuration
     */
    public HttpClientTracesConfiguration(HttpClientTracesConfiguration configuration) {
        this.active = configuration.active;
        this.request = configuration.request == null ? null : new HttpClientTraceMessageConfiguration(configuration.request);
        this.response = configuration.response == null ? null : new HttpClientTraceMessageConfiguration(configuration.response);
    }

    protected boolean isRequestActive() {
        return !Boolean.FALSE.equals(active) && request != null && request.isActive();
    }
    protected boolean isResponseActive() {
        return !Boolean.FALSE.equals(active) && response != null && response.isActive();
    }
    /**
     * @param active
     * @return
     */
    public HttpClientTracesConfiguration setActive(Boolean active) {
        this.active = active;
        return this;
    }
    protected HttpClientTraceMessageConfiguration getRequest() {
        return request;
    }
    /**
     * @param request
     * @return
     */
    public HttpClientTracesConfiguration setRequest(HttpClientTraceMessageConfiguration request) {
        this.request = request;
        return this;
    }
    protected HttpClientTraceMessageConfiguration getResponse() {
        return response;
    }
    /**
     * @param response
     * @return
     */
    public HttpClientTracesConfiguration setResponse(HttpClientTraceMessageConfiguration response) {
        this.response = response;
        return this;
    }

}
