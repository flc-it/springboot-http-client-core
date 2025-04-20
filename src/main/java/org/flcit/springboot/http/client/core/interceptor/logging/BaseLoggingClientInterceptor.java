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

package org.flcit.springboot.http.client.core.interceptor.logging;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

import org.flcit.springboot.commons.core.util.LogUtils;
import org.flcit.springboot.http.client.core.configuration.HttpClientTraceMessageConfiguration;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class BaseLoggingClientInterceptor {

    static final Logger LOG = LoggerFactory.getLogger(BaseLoggingClientInterceptor.class);
    static final String TYPE_REQUEST = "Request";
    static final String TYPE_RESPONSE = "Response";
    static final String REQUEST_ID = "REQUEST_ID";

    final AtomicLong ids = new AtomicLong();

    String name;
    HttpClientTraceMessageConfiguration request;
    HttpClientTraceMessageConfiguration response;

    /**
     * @param name
     * @param request
     * @param response
     */
    public BaseLoggingClientInterceptor(String name, HttpClientTraceMessageConfiguration request, HttpClientTraceMessageConfiguration response) {
        this.name = name;
        this.request = request;
        this.response = response;
    }

    /**
     * @param name
     * @return
     */
    public BaseLoggingClientInterceptor setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param request
     * @return
     */
    public BaseLoggingClientInterceptor setRequest(HttpClientTraceMessageConfiguration request) {
        this.request = request;
        return this;
    }

    /**
     * @param response
     * @return
     */
    public BaseLoggingClientInterceptor setResponse(HttpClientTraceMessageConfiguration response) {
        this.response = response;
        return this;
    }

    final boolean isRequestActive() {
        return request != null && request.isActive();
    }

    final boolean isResponseActive() {
        return response != null && response.isActive();
    }

    static final boolean isEnabled() {
        return LOG.isInfoEnabled();
    }

    final void body(long id, String type, String body, Integer maxLength) {
        if (LOG.isInfoEnabled()) {
            LOG.info("[{}] {} {} - Body => {}", name, type, id, LogUtils.formatValue(body, maxLength));
        }
    }

    /**
     * 
     */
    public static final void activeLevel() {
        LoggingSystem.get(BaseLoggingClientInterceptor.class.getClassLoader()).setLogLevel(LOG.getName(), LogLevel.INFO);
    }

}
