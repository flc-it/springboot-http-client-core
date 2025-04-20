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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

import org.flcit.commons.core.util.BooleanUtils;
import org.flcit.springboot.http.client.core.configuration.HttpClientTraceMessageConfiguration;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class LoggingClientInterceptor extends BaseLoggingClientInterceptor implements ClientInterceptor {

    /**
     * @param name
     * @param request
     * @param response
     */
    public LoggingClientInterceptor(String name,
            HttpClientTraceMessageConfiguration request,
            HttpClientTraceMessageConfiguration response) {
        super(name, request, response);
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        if (isEnabled()) {
            final long id = ids.incrementAndGet();
            messageContext.setProperty(REQUEST_ID, id);
            if (isRequestActive()) {
                traceRequest(id, TYPE_REQUEST, messageContext.getRequest());
            }
        }
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
        if (!isEnabled() || !isResponseActive()) {
            return;
        }
         final Long id = (Long) messageContext.getProperty(REQUEST_ID);
         if (id == null) {
             return;
         }
         traceResponse(id, TYPE_RESPONSE, messageContext.getResponse());
    }

    private void traceRequest(long id, String type, WebServiceMessage message) throws WebServiceIOException {
        if (BooleanUtils.isTrue(request.getBody())) {
            traceBody(id, type, request.getMaxLength(), message);
        }
    }

    private void traceResponse(long id, String type, WebServiceMessage message) throws WebServiceIOException {
        if (BooleanUtils.isTrue(response.getBody())) {
            traceBody(id, type, response.getMaxLength(), message);
        }
    }

    private final void traceBody(long id, String type, Integer maxLength, WebServiceMessage message) throws WebServiceIOException {
        try {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            message.writeTo(os);
            body(id, type, os.toString(StandardCharsets.UTF_8.name()), maxLength);
        } catch (IOException e) {
            throw new WebServiceIOException("traceBody - " + type, e);
        }
    }

}
