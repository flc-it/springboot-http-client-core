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
import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.config.Http1Config;
import org.flcit.commons.core.util.BooleanUtils;
import org.flcit.commons.core.util.FunctionUtils;
import org.flcit.commons.core.util.ObjectUtils;
import org.flcit.springboot.http.client.core.configuration.HttpClientBuilderConfiguration.PoolConnectionManagerConfiguration.ConnectionConfiguration;
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
        super.setRequest(
                new RequestConfiguration()
                .setRedirects(Boolean.FALSE)
                .setContentCompression(Boolean.TRUE)
        )
        .setPoolConnectionManager(
                new PoolConnectionManagerConfiguration()
                .setMaxConnectionPerRoute(20)
                .setMaxConnectionTotal(50)
        );
    }

    private final boolean isTraces(final HttpClientBuilderConfiguration builderConfiguration) {
        return builderConfiguration.isTracesActive();
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
        if (ObjectUtils.hasOrDefault(builderConfiguration.getChunkSize(), this.getChunkSize())) {
            factory.setChunkSize(ObjectUtils.getOrDefault(builderConfiguration.getChunkSize(), this.getChunkSize()));
        }
        return factory;
    }

    private final ClientHttpRequestFactory buildApache(final HttpClientBuilderConfiguration builderConfiguration, final HttpRequestInterceptor firstHttpRequestInterceptor) {
        return new HttpComponentsClientHttpRequestFactory(create(builderConfiguration, firstHttpRequestInterceptor).build());
    }

    private final org.apache.hc.client5.http.impl.classic.HttpClientBuilder create(final HttpClientBuilderConfiguration builderConfiguration, final HttpRequestInterceptor firstHttpRequestInterceptor) {
        final RequestConfig.Builder requestConfigBuilder = requestConfig(builderConfiguration.getRequest());
        final PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = connectionManagerConfig(builderConfiguration);
        final org.apache.hc.client5.http.impl.classic.HttpClientBuilder httpClientBuilder = HttpClients.custom();

        if (isProxy(builderConfiguration) && proxyConfiguration != null) {
            proxyConfiguration.add(httpClientBuilder, requestConfigBuilder);
        }
        if (Boolean.FALSE.equals(builderConfiguration.getSslCertificateVerification())) {
            SSLUtilities.disableSSLSecurity(connectionManagerBuilder);
        }
        httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
        httpClientBuilder.setConnectionManager(connectionManagerBuilder.build());
        if (firstHttpRequestInterceptor != null) {
            httpClientBuilder.addRequestInterceptorFirst(firstHttpRequestInterceptor);
        }
        return httpClientBuilder;
    }

    private final RequestConfig.Builder requestConfig(final RequestConfiguration requestConfiguration) {
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        final RequestConfiguration defaultRequestConfiguration = this.getRequest();
        FunctionUtils.consumeFirstNotNull(
                requestConfigBuilder::setRedirectsEnabled,
                requestConfiguration != null ? requestConfiguration.getRedirects() : null,
                defaultRequestConfiguration != null ? defaultRequestConfiguration.getRedirects() : null
        );
        FunctionUtils.consumeFirstNotNull(
                requestConfigBuilder::setMaxRedirects,
                requestConfiguration != null ? requestConfiguration.getMaxRedirects() : null,
                defaultRequestConfiguration != null ? defaultRequestConfiguration.getMaxRedirects() : null
        );
        FunctionUtils.consumeFirstNotNull(
                requestConfigBuilder::setContentCompressionEnabled,
                requestConfiguration != null ? requestConfiguration.getContentCompression() : null,
                defaultRequestConfiguration != null ? defaultRequestConfiguration.getContentCompression() : null
        );
        FunctionUtils.consumeFirstNotNull(
                v -> requestConfigBuilder.setDefaultKeepAlive(v, TimeUnit.MILLISECONDS),
                requestConfiguration != null ? requestConfiguration.getDefaultKeepAlive() : null,
                defaultRequestConfiguration != null ? defaultRequestConfiguration.getDefaultKeepAlive() : null
        );
        FunctionUtils.consumeFirstNotNull(
                v -> requestConfigBuilder.setConnectionRequestTimeout(v, TimeUnit.MILLISECONDS),
                requestConfiguration != null ? requestConfiguration.getConnectionRequestTimeout() : null,
                defaultRequestConfiguration != null ? defaultRequestConfiguration.getConnectionRequestTimeout() : null
        );
        FunctionUtils.consumeFirstNotNull(
                v -> requestConfigBuilder.setResponseTimeout(v, TimeUnit.MILLISECONDS),
                requestConfiguration != null ? requestConfiguration.getResponseTimeout() : null,
                defaultRequestConfiguration != null ? defaultRequestConfiguration.getResponseTimeout() : null
        );
        return requestConfigBuilder;
    }

    private final PoolingHttpClientConnectionManagerBuilder connectionManagerConfig(final HttpClientBuilderConfiguration builderConfiguration) {
        final PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create();
        final PoolConnectionManagerConfiguration poolConnectionManagerConfiguration = builderConfiguration.getPoolConnectionManager();
        final PoolConnectionManagerConfiguration defaultPoolConnectionManagerConfiguration = this.getPoolConnectionManager();
        FunctionUtils.consumeFirstNotNull(
                connectionManagerBuilder::setConnPoolPolicy,
                poolConnectionManagerConfiguration != null ? poolConnectionManagerConfiguration.getReusePolicy() : null,
                defaultPoolConnectionManagerConfiguration != null ? defaultPoolConnectionManagerConfiguration.getReusePolicy() : null
        );
        FunctionUtils.consumeFirstNotNull(
                connectionManagerBuilder::setPoolConcurrencyPolicy,
                poolConnectionManagerConfiguration != null ? poolConnectionManagerConfiguration.getConcurrencyPolicy() : null,
                defaultPoolConnectionManagerConfiguration != null ? defaultPoolConnectionManagerConfiguration.getConcurrencyPolicy() : null
        );
        FunctionUtils.consumeFirstNotNull(
                connectionManagerBuilder::setMaxConnPerRoute,
                poolConnectionManagerConfiguration != null ? poolConnectionManagerConfiguration.getMaxConnectionPerRoute() : null,
                defaultPoolConnectionManagerConfiguration != null ? defaultPoolConnectionManagerConfiguration.getMaxConnectionPerRoute() : null
        );
        FunctionUtils.consumeFirstNotNull(
                connectionManagerBuilder::setMaxConnTotal,
                poolConnectionManagerConfiguration != null ? poolConnectionManagerConfiguration.getMaxConnectionTotal() : null,
                defaultPoolConnectionManagerConfiguration != null ? defaultPoolConnectionManagerConfiguration.getMaxConnectionTotal() : null
        );
        if (ObjectUtils.hasOrDefault(builderConfiguration.getChunkSize(), this.getChunkSize())) {
            connectionManagerBuilder.setConnectionFactory(
                    ManagedHttpClientConnectionFactory.builder()
                    .http1Config(
                            Http1Config.custom().setBufferSize(
                                    ObjectUtils.getOrDefault(builderConfiguration.getChunkSize(), this.getChunkSize())
                                    )
                            .build())
                    .build()
            );
        }
        connectionManagerBuilder.setDefaultConnectionConfig(connectionConfig(builderConfiguration).build());
        return connectionManagerBuilder;
    }

    private final ConnectionConfig.Builder connectionConfig(final HttpClientBuilderConfiguration builderConfiguration) {
        final ConnectionConfig.Builder connectionConfigBuilder = ConnectionConfig.custom();
        final ConnectionConfiguration connectionConfiguration = builderConfiguration.getPoolConnectionManager() != null ? builderConfiguration.getPoolConnectionManager().getConnection() : null;
        final ConnectionConfiguration defaultConnectionConfiguration = this.getPoolConnectionManager() != null ? this.getPoolConnectionManager().getConnection() : null;
        if (ObjectUtils.hasOrDefault(builderConfiguration.getConnectTimeout(), this.getConnectTimeout())) {
            connectionConfigBuilder.setConnectTimeout(ObjectUtils.getOrDefault(builderConfiguration.getConnectTimeout(), this.getConnectTimeout()), TimeUnit.MILLISECONDS);
        }
        if (ObjectUtils.hasOrDefault(builderConfiguration.getSocketTimeout(), this.getSocketTimeout())) {
            connectionConfigBuilder.setSocketTimeout(ObjectUtils.getOrDefault(builderConfiguration.getSocketTimeout(), this.getSocketTimeout()), TimeUnit.MILLISECONDS);
        }
        FunctionUtils.consumeFirstNotNull(
                v -> connectionConfigBuilder.setTimeToLive(v, TimeUnit.MILLISECONDS),
                connectionConfiguration != null ? connectionConfiguration.getTimeToLive() : null,
                defaultConnectionConfiguration != null ? defaultConnectionConfiguration.getTimeToLive() : null
        );
        FunctionUtils.consumeFirstNotNull(
                v -> connectionConfigBuilder.setValidateAfterInactivity(v, TimeUnit.MILLISECONDS),
                connectionConfiguration != null ? connectionConfiguration.getValidateAfterInactivity() : null,
                defaultConnectionConfiguration != null ? defaultConnectionConfiguration.getValidateAfterInactivity() : null
        );
        return connectionConfigBuilder;
    }  

}
