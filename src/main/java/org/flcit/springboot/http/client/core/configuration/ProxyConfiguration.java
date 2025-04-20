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

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Collections;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.StringUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class ProxyConfiguration {

    private String hostname;
    private Integer port;
    private String domain;
    private String username;
    private String password;

    /**
     * @return
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return
     */
    public HttpHost getHost() {
        return port != null ? new HttpHost(hostname, port) : new HttpHost(hostname);
    }

    private final Authenticator authenticator = new Authenticator() {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(ProxyConfiguration.this.domain + "\\" + ProxyConfiguration.this.username, ProxyConfiguration.this.password.toCharArray());
        }
    };

    /**
     * @return
     */
    public Proxy getProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
    }

    private boolean isWithCredentials() {
        return StringUtils.hasLength(username) && StringUtils.hasLength(password); 
    }

    /**
     * @return
     */
    public HttpClient getHttpClient() {
        return getHttpClientBuilder().build();
    }

    /**
     * @return
     */
    public HttpClientBuilder getHttpClientBuilder() {
        return getHttpClientBuilder(RequestConfig.custom());
    }

    /**
     * @param requestConfigBuilder
     * @return
     */
    public HttpClientBuilder getHttpClientBuilder(RequestConfig.Builder requestConfigBuilder) {
        return add(HttpClients.custom(), requestConfigBuilder);
    }

    /**
     * @return
     */
    public Proxy build() {
        Authenticator.setDefault(authenticator);
        return getProxy();
    }

    /**
     * @param httpClientBuilder
     * @param requestConfigBuilder
     * @return
     */
    public HttpClientBuilder add(HttpClientBuilder httpClientBuilder, RequestConfig.Builder requestConfigBuilder) {
        CredentialsProvider credsProvider = null;
        if (isWithCredentials()) {
            credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(getHost()), new UsernamePasswordCredentials(username, password));
            credsProvider.setCredentials(new AuthScope(getHost(), AuthScope.ANY_REALM, AuthSchemes.NTLM), new NTCredentials(username, password, null, domain));
        }
        return httpClientBuilder
                .setDefaultRequestConfig(requestConfigBuilder
                        .setProxy(getHost())
                        .setProxyPreferredAuthSchemes(Collections.singletonList(AuthSchemes.NTLM))
                        .build())
                .setDefaultCredentialsProvider(credsProvider);
    }

}
