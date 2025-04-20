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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Lookup;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import org.flcit.springboot.commons.test.util.ReflectionTestUtils;

class HttpClientBuilderTest {

    private static final int CHUNK_SIZE = 8192;
    private static final int CONNECTION_REQUEST_TIMEOUT = 30;
    private static final int CONNECTION_TIMEOUT = 60;
    private static final int MAX_CONNECTION_PER_ROUTE = 10;
    private static final int MAX_CONNECTION_TOTAL = 30;
    private static final int SOCKET_TIMEOUT = 45;

    @Test
    void testBuildSimpleNull() {
        final HttpClientBuilder builder = new HttpClientBuilder(null);
        builder.setSimpleClient(true);
        final ClientHttpRequestFactory httpBuilder = builder.build(new HttpClientBuilderConfiguration());
        assertInstanceOf(SimpleClientHttpRequestFactory.class, httpBuilder);
        assertNull(ReflectionTestUtils.getFieldValue(httpBuilder, "proxy"));
        assertTrue((boolean) ReflectionTestUtils.getFieldValue(httpBuilder, "bufferRequestBody"));
        assertEquals(org.springframework.test.util.ReflectionTestUtils.getField(null, SimpleClientHttpRequestFactory.class, "DEFAULT_CHUNK_SIZE"), ReflectionTestUtils.getFieldValue(httpBuilder, "chunkSize"));
        assertEquals(-1, ReflectionTestUtils.getFieldValue(httpBuilder, "connectTimeout"));
        assertEquals(-1, ReflectionTestUtils.getFieldValue(httpBuilder, "readTimeout"));
    }

    @Test
    void testBuildSimpleDefault() {
        final HttpClientBuilder builder = new HttpClientBuilder(null);
        builder.setSimpleClient(true)
            .setStreaming(true)
            .setSslCertificateVerification(false)
            .setChunkSize(CHUNK_SIZE)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setMaxConnectionPerRoute(MAX_CONNECTION_PER_ROUTE)
            .setMaxConnectionTotal(MAX_CONNECTION_TOTAL)
            .setSocketTimeout(SOCKET_TIMEOUT);
        final ClientHttpRequestFactory httpBuilder = builder.build(new HttpClientBuilderConfiguration());
        assertInstanceOf(SimpleClientHttpRequestFactory.class, httpBuilder);
        assertNull(ReflectionTestUtils.getFieldValue(httpBuilder, "proxy"));
        assertFalse((boolean) ReflectionTestUtils.getFieldValue(httpBuilder, "bufferRequestBody"));
        assertEquals(CHUNK_SIZE, ReflectionTestUtils.getFieldValue(httpBuilder, "chunkSize"));
        assertEquals(CONNECTION_TIMEOUT, ReflectionTestUtils.getFieldValue(httpBuilder, "connectTimeout"));
        assertEquals(SOCKET_TIMEOUT, ReflectionTestUtils.getFieldValue(httpBuilder, "readTimeout"));
    }

    @Test
    void testBuildSimpleCustomValues() {
        final ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHostname("localhost");
        proxyConfiguration.setDomain("FLC");
        proxyConfiguration.setPort(8080);
        proxyConfiguration.setUsername("username");
        proxyConfiguration.setPassword("password");
        final HttpClientBuilderConfiguration builderConfiguration = new HttpClientBuilderConfiguration();
        builderConfiguration.setSimpleClient(true)
            .setStreaming(true)
            .setProxy(true)
            .setSslCertificateVerification(false)
            .setChunkSize(CHUNK_SIZE)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setMaxConnectionPerRoute(MAX_CONNECTION_PER_ROUTE)
            .setMaxConnectionTotal(MAX_CONNECTION_TOTAL)
            .setSocketTimeout(SOCKET_TIMEOUT);
        final HttpClientBuilder builder = new HttpClientBuilder(proxyConfiguration);
        final ClientHttpRequestFactory httpBuilder = builder.build(builderConfiguration);
        assertInstanceOf(SkipSslVerificationHttpRequestFactory.class, httpBuilder);
        assertNotNull(ReflectionTestUtils.getFieldValue(httpBuilder, "proxy"));
        assertFalse((boolean) ReflectionTestUtils.getFieldValue(httpBuilder, "bufferRequestBody"));
        assertEquals(CHUNK_SIZE, ReflectionTestUtils.getFieldValue(httpBuilder, "chunkSize"));
        assertEquals(CONNECTION_TIMEOUT, ReflectionTestUtils.getFieldValue(httpBuilder, "connectTimeout"));
        assertEquals(SOCKET_TIMEOUT, ReflectionTestUtils.getFieldValue(httpBuilder, "readTimeout"));
    }

    @Test
    void testBuildNull() {
        final ClientHttpRequestFactory httpBuilder = new HttpClientBuilder(null).build(new HttpClientBuilderConfiguration());
        assertInstanceOf(HttpComponentsClientHttpRequestFactory.class, httpBuilder);
        assertInstanceOf(CloseableHttpClient.class, ((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient());
        final RequestConfig defaultConfig = (RequestConfig) ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "defaultConfig");
        final Map<?, ?> credentialsMap = (Map<?, ?>) ReflectionTestUtils.getFieldValue(ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "credentialsProvider"), "credMap");
        assertTrue(credentialsMap.isEmpty());
        assertNull(defaultConfig.getProxy());
        assertNull(defaultConfig.getProxyPreferredAuthSchemes());
        assertTrue((boolean) ReflectionTestUtils.getFieldValue(httpBuilder, "bufferRequestBody"));
        assertEquals(-1, defaultConfig.getConnectTimeout());
        assertEquals(-1, defaultConfig.getConnectionRequestTimeout());
        assertEquals(-1, defaultConfig.getSocketTimeout());
        final PoolingHttpClientConnectionManager pool = (PoolingHttpClientConnectionManager) ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "connManager");
        assertEquals(15, pool.getDefaultMaxPerRoute());
        assertEquals(60, pool.getMaxTotal());
    }

    @Test
    void testBuildDefault() {
        final HttpClientBuilder builder = new HttpClientBuilder(null);
        builder.setStreaming(true)
            .setSslCertificateVerification(false)
            .setChunkSize(CHUNK_SIZE)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setMaxConnectionPerRoute(MAX_CONNECTION_PER_ROUTE)
            .setMaxConnectionTotal(MAX_CONNECTION_TOTAL)
            .setSocketTimeout(SOCKET_TIMEOUT);
        final ClientHttpRequestFactory httpBuilder = builder.build(new HttpClientBuilderConfiguration());
        assertInstanceOf(HttpComponentsClientHttpRequestFactory.class, httpBuilder);
        assertInstanceOf(CloseableHttpClient.class, ((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient());
        final RequestConfig defaultConfig = (RequestConfig) ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "defaultConfig");
        final Map<?, ?> credentialsMap = (Map<?, ?>) ReflectionTestUtils.getFieldValue(ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "credentialsProvider"), "credMap");
        assertTrue(credentialsMap.isEmpty());
        assertNull(defaultConfig.getProxy());
        assertNull(defaultConfig.getProxyPreferredAuthSchemes());
        assertFalse((boolean) ReflectionTestUtils.getFieldValue(httpBuilder, "bufferRequestBody"));
        assertEquals(CONNECTION_TIMEOUT, defaultConfig.getConnectTimeout());
        assertEquals(CONNECTION_REQUEST_TIMEOUT, defaultConfig.getConnectionRequestTimeout());
        assertEquals(SOCKET_TIMEOUT, defaultConfig.getSocketTimeout());
        final PoolingHttpClientConnectionManager pool = (PoolingHttpClientConnectionManager) ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "connManager");
        assertEquals(MAX_CONNECTION_PER_ROUTE, pool.getDefaultMaxPerRoute());
        assertEquals(MAX_CONNECTION_TOTAL, pool.getMaxTotal());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testBuildCustomValues() {
        final ProxyConfiguration proxyConfiguration = new ProxyConfiguration();
        proxyConfiguration.setHostname("localhost");
        proxyConfiguration.setDomain("FLC");
        proxyConfiguration.setPort(8080);
        proxyConfiguration.setUsername("username");
        proxyConfiguration.setPassword("password");
        final HttpClientBuilderConfiguration builderConfiguration = new HttpClientBuilderConfiguration();
        builderConfiguration.setStreaming(true)
            .setProxy(true)
            .setSslCertificateVerification(false)
            .setChunkSize(CHUNK_SIZE)
            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
            .setConnectTimeout(CONNECTION_TIMEOUT)
            .setMaxConnectionPerRoute(MAX_CONNECTION_PER_ROUTE)
            .setMaxConnectionTotal(MAX_CONNECTION_TOTAL)
            .setSocketTimeout(SOCKET_TIMEOUT);
        final HttpClientBuilder builder = new HttpClientBuilder(proxyConfiguration);
        final ClientHttpRequestFactory httpBuilder = builder.build(builderConfiguration);
        assertInstanceOf(HttpComponentsClientHttpRequestFactory.class, httpBuilder);
        assertInstanceOf(CloseableHttpClient.class, ((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient());
        final RequestConfig defaultConfig = (RequestConfig) ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "defaultConfig");
        final Map<?, ?> credentialsMap = (Map<?, ?>) ReflectionTestUtils.getFieldValue(ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "credentialsProvider"), "credMap");
        assertFalse(credentialsMap.isEmpty());
        assertNotNull(defaultConfig.getProxy());
        assertNotNull(defaultConfig.getProxyPreferredAuthSchemes());
        final PoolingHttpClientConnectionManager pool = (PoolingHttpClientConnectionManager) ReflectionTestUtils.getFieldValue(((HttpComponentsClientHttpRequestFactory) httpBuilder).getHttpClient(), "connManager");
        assertEquals(NoopHostnameVerifier.INSTANCE, ReflectionTestUtils.getFieldValue(((Lookup<ConnectionSocketFactory>) ReflectionTestUtils.getFieldValue(ReflectionTestUtils.getFieldValue(pool, "connectionOperator"), "socketFactoryRegistry")).lookup("https"), "hostnameVerifier"));
        assertEquals(MAX_CONNECTION_PER_ROUTE, pool.getDefaultMaxPerRoute());
        assertEquals(MAX_CONNECTION_TOTAL, pool.getMaxTotal());
        assertFalse((boolean) ReflectionTestUtils.getFieldValue(httpBuilder, "bufferRequestBody"));
        assertEquals(CONNECTION_TIMEOUT, defaultConfig.getConnectTimeout());
        assertEquals(CONNECTION_REQUEST_TIMEOUT, defaultConfig.getConnectionRequestTimeout());
        assertEquals(SOCKET_TIMEOUT, defaultConfig.getSocketTimeout());
    }

}
