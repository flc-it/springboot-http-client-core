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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import org.flcit.commons.core.util.BooleanUtils;
import org.flcit.commons.core.util.ClassUtils;
import org.flcit.springboot.http.client.core.configuration.HttpClientTraceMessageConfiguration;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class LoggingClientHttpRequestInterceptor extends BaseLoggingClientInterceptor implements ClientHttpRequestInterceptor {

    /**
     * @param name
     * @param request
     * @param response
     */
    public LoggingClientHttpRequestInterceptor(String name, 
            HttpClientTraceMessageConfiguration request,
            HttpClientTraceMessageConfiguration response) {
        super(name, request, response);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (!isEnabled()) {
            return execution.execute(httpRequest, body);
        }
        final long id = ids.incrementAndGet();
        trace(id, httpRequest, body);
        return trace(id, execution.execute(httpRequest, body));
    }

    private void trace(final long id, final HttpRequest httpRequest, final byte[] body) {
        if (!isRequestActive()) {
            return;
        }
        line(id, TYPE_REQUEST, httpRequest.getMethodValue(), httpRequest.getURI().toString());
        if (BooleanUtils.isTrue(request.getHeaders())) {
            headers(id, TYPE_REQUEST, httpRequest.getHeaders());
        }
        if (BooleanUtils.isTrue(request.getBody())
                && !org.springframework.util.ObjectUtils.isEmpty(body)) {
            body(id, TYPE_REQUEST, new String(body, StandardCharsets.UTF_8), request.getMaxLength());
        }
    }

    private ClientHttpResponse trace(final long id, final ClientHttpResponse httpResponse) throws IOException {
        if (isResponseActive()) {
            line(id, TYPE_RESPONSE, String.valueOf(httpResponse.getRawStatusCode()), httpResponse.getStatusText());
            if (BooleanUtils.isTrue(response.getHeaders())) {
                headers(id, TYPE_RESPONSE, httpResponse.getHeaders());
            }
            if (BooleanUtils.isTrue(response.getBody())) {
                body(id, TYPE_RESPONSE, canLogResponseBody(httpResponse) ? StreamUtils.copyToString(httpResponse.getBody(), StandardCharsets.UTF_8) : "Not Printeable", response.getMaxLength());
            }
        }
        return httpResponse;
    }

    private static final boolean canLogResponseBody(final ClientHttpResponse httpResponse) {
        return ClassUtils.equals(httpResponse.getClass(), "org.springframework.http.client.BufferingClientHttpResponseWrapper");
    }

    private static final void line(long id, String type, String value1, String value2) {
        LOG.info("{} {} - {} {}", type, id, value1, value2);
    }

    private static final void headers(long id, String type, HttpHeaders headers) {
        if (headers != null) {
            for (Entry<String, List<String>> header: headers.entrySet()) {
                for (String value: header.getValue()) {
                    LOG.info("{} {} - Header => {}: {}", type, id, header.getKey(), value);
                }
            }
        }
    }

}
