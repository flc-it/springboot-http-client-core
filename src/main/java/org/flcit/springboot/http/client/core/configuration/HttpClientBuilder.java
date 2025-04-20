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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.flcit.commons.core.util.BooleanUtils;
import org.flcit.commons.core.util.ObjectUtils;
import org.flcit.springboot.http.client.core.interceptor.logging.BaseLoggingClientInterceptor;
import org.flcit.springboot.http.client.core.interceptor.logging.LoggingClientHttpRequestInterceptor;
import org.flcit.springboot.http.client.core.interceptor.logging.LoggingClientInterceptor;
import org.flcit.springboot.http.client.core.util.SSLUtilities;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class HttpClientBuilder extends HttpClientBuilderConfiguration {

    private final ProxyConfiguration proxyConfiguration;

    /**
     * @param proxyConfiguration
     */
    public HttpClientBuilder(ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;
        setMaxConnectionPerRoute(15);
        setMaxConnectionTotal(60);
    }

    private final boolean isTraces(final HttpClientBuilderConfiguration builderConfiguration) {
        return builderConfiguration.isTracesActive();
    }

    private final boolean isStreaming(final HttpClientBuilderConfiguration builderConfiguration) {
        return BooleanUtils.isTrueOrNullAndTrue(builderConfiguration.getStreaming(), this.getStreaming());
    }

    private final boolean isProxy(final HttpClientBuilderConfiguration builderConfiguration) {
        return BooleanUtils.isTrueOrNullAndTrue(builderConfiguration.getProxy(), this.getProxy());
    }

    /**
     * @param <T>
     * @param builderConfiguration
     * @param name
     * @param interceptors
     * @return
     */
    @SuppressWarnings("unchecked")
    public final <T> T[] addClientInterceptors(final HttpClientBuilderConfiguration builderConfiguration, final String name, final T[] interceptors) {
        final List<T> interceptorsList = org.springframework.util.ObjectUtils.isEmpty(interceptors) ? new ArrayList<>(1) : new ArrayList<>(Arrays.asList(interceptors));
        addInterceptors(builderConfiguration, name, interceptorsList, LoggingClientInterceptor.class);
        return CollectionUtils.isEmpty(interceptorsList) ? null : (T[]) interceptorsList.toArray(new Object[0]);
    }

    /**
     * @param builderConfiguration
     * @param name
     * @param interceptors
     */
    public final void addClientHttpInterceptors(final HttpClientBuilderConfiguration builderConfiguration, final String name, final List<ClientHttpRequestInterceptor> interceptors) {
        addInterceptors(builderConfiguration, name, interceptors, LoggingClientHttpRequestInterceptor.class);
    }

    @SuppressWarnings("unchecked")
    private final <E, T extends BaseLoggingClientInterceptor> void addInterceptors(final HttpClientBuilderConfiguration builderConfiguration, final String name, final List<E> interceptors, final Class<T> clazzInstance) {
        if (isTraces(builderConfiguration)) {
            final HttpClientTraceMessageConfiguration request = convert(builderConfiguration.getTraces().getRequest());
            final HttpClientTraceMessageConfiguration response = convert(builderConfiguration.getTraces().getResponse());

            int index = org.flcit.commons.core.util.CollectionUtils.indexOf(interceptors, clazzInstance::isInstance);
            if (index != -1) {
                final T interceptor = (T) interceptors.get(index);
                Collections.swap(interceptors, index, interceptors.size() - 1);
                interceptor.setRequest(request).setResponse(response).setName(name);
            } else {
                try {
                    interceptors.add((E) clazzInstance.getConstructor(String.class, HttpClientTraceMessageConfiguration.class, HttpClientTraceMessageConfiguration.class).newInstance(name, request, response));
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    private static HttpClientTraceMessageConfiguration convert(HttpClientTraceMessageConfiguration configuration) {
        return configuration != null ? new HttpClientTraceMessageConfiguration(configuration) : null;
    }

    /**
     * @param builderConfiguration
     * @return
     */
    public final ClientHttpRequestFactory build(final HttpClientBuilderConfiguration builderConfiguration) {
        return build(builderConfiguration, null);
    }

    /**
     * @param builderConfiguration
     * @param firstHttpRequestInterceptor
     * @return
     */
    public final ClientHttpRequestFactory build(final HttpClientBuilderConfiguration builderConfiguration, final HttpRequestInterceptor firstHttpRequestInterceptor) {
        return buildInternal(builderConfiguration, BooleanUtils.isTrueOrNullAndTrue(builderConfiguration.getSimpleClient(), getSimpleClient()) ? buildSimple(builderConfiguration) : buildApache(builderConfiguration, firstHttpRequestInterceptor));
    }

    private final ClientHttpRequestFactory buildInternal(final HttpClientBuilderConfiguration builderConfiguration, final ClientHttpRequestFactory requestFactory) {
        return !isTraces(builderConfiguration) ? requestFactory : new BufferingClientHttpRequestFactory(requestFactory);
    }

    private final ClientHttpRequestFactory buildSimple(final HttpClientBuilderConfiguration builderConfiguration) {
        final SimpleClientHttpRequestFactory factory = Boolean.FALSE.equals(builderConfiguration.getSslCertificateVerification()) ? new SkipSslVerificationHttpRequestFactory() : new SimpleClientHttpRequestFactory();
        if (ObjectUtils.hasOrDefault(builderConfiguration.getConnectTimeout(), this.getConnectTimeout())) {
            factory.setConnectTimeout(ObjectUtils.getOrDefault(builderConfiguration.getConnectTimeout(), this.getConnectTimeout()));
        }
        if (ObjectUtils.hasOrDefault(builderConfiguration.getSocketTimeout(), this.getSocketTimeout())) {
            factory.setReadTimeout(ObjectUtils.getOrDefault(builderConfiguration.getSocketTimeout(), this.getSocketTimeout()));
        }
        if (isProxy(builderConfiguration)
                && proxyConfiguration != null) {
            factory.setProxy(proxyConfiguration.build());
        }
        if (isStreaming(builderConfiguration) && !isTracesActive()) {
            factory.setBufferRequestBody(false);
            if (ObjectUtils.hasOrDefault(builderConfiguration.getChunkSize(), this.getChunkSize())) {
                factory.setChunkSize(ObjectUtils.getOrDefault(builderConfiguration.getChunkSize(), this.getChunkSize()));
            }
        }
        return factory;
    }

    private final ClientHttpRequestFactory buildApache(final HttpClientBuilderConfiguration builderConfiguration, final HttpRequestInterceptor firstHttpRequestInterceptor) {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(create(builderConfiguration, firstHttpRequestInterceptor).build());
        if (isStreaming(builderConfiguration) && !isTracesActive()) {
            factory.setBufferRequestBody(false);
        }
        return factory;
    }

    private final org.apache.http.impl.client.HttpClientBuilder create(final HttpClientBuilderConfiguration builderConfiguration, final HttpRequestInterceptor firstHttpRequestInterceptor) {
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        if (ObjectUtils.hasOrDefault(builderConfiguration.getConnectTimeout(), this.getConnectTimeout())) {
            requestConfigBuilder.setConnectTimeout(ObjectUtils.getOrDefault(builderConfiguration.getConnectTimeout(), this.getConnectTimeout()));
        }
        if (ObjectUtils.hasOrDefault(builderConfiguration.getConnectionRequestTimeout(), this.getConnectionRequestTimeout())) {
            requestConfigBuilder.setConnectionRequestTimeout(ObjectUtils.getOrDefault(builderConfiguration.getConnectionRequestTimeout(), this.getConnectionRequestTimeout()));
        }
        if (ObjectUtils.hasOrDefault(builderConfiguration.getSocketTimeout(), this.getSocketTimeout())) {
            requestConfigBuilder.setSocketTimeout(ObjectUtils.getOrDefault(builderConfiguration.getSocketTimeout(), this.getSocketTimeout()));
        }
        org.apache.http.impl.client.HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfigBuilder.build());
        if (isProxy(builderConfiguration)
                && proxyConfiguration != null) {
            proxyConfiguration.add(httpClientBuilder, requestConfigBuilder);
        }
        if (Boolean.FALSE.equals(builderConfiguration.getSslCertificateVerification())) {
            SSLUtilities.disableSSLSecurity(httpClientBuilder);
        }
        if (firstHttpRequestInterceptor != null) {
            httpClientBuilder.addInterceptorFirst(firstHttpRequestInterceptor);
        }
        if (ObjectUtils.hasOrDefault(builderConfiguration.getMaxConnectionPerRoute(), this.getMaxConnectionPerRoute())) {
            httpClientBuilder.setMaxConnPerRoute(ObjectUtils.getOrDefault(builderConfiguration.getMaxConnectionPerRoute(), this.getMaxConnectionPerRoute()));
        }
        if (ObjectUtils.hasOrDefault(builderConfiguration.getMaxConnectionTotal(), this.getMaxConnectionTotal())) {
            httpClientBuilder.setMaxConnTotal(ObjectUtils.getOrDefault(builderConfiguration.getMaxConnectionTotal(), this.getMaxConnectionTotal()));
        }
        return httpClientBuilder;
    }

}
