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
public class HttpClientBuilderConfiguration {

    private Boolean simpleClient;
    private Integer connectTimeout;
    private Integer connectionRequestTimeout;
    private Integer socketTimeout;
    private Integer chunkSize;
    private Integer maxConnectionPerRoute;
    private Integer maxConnectionTotal;
    private Boolean proxy;
    private Boolean streaming;
    private Boolean sslCertificateVerification;
    private HttpClientTracesConfiguration traces;

    protected Boolean getSimpleClient() {
        return simpleClient;
    }
    /**
     * @param simpleClient
     * @return
     */
    public HttpClientBuilderConfiguration setSimpleClient(Boolean simpleClient) {
        this.simpleClient = simpleClient;
        return this;
    }
    protected Integer getConnectTimeout() {
        return connectTimeout;
    }
    /**
     * @param connectTimeout
     * @return
     */
    public HttpClientBuilderConfiguration setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }
    protected Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }
    /**
     * @param connectionRequestTimeout
     * @return
     */
    public HttpClientBuilderConfiguration setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }
    protected Integer getSocketTimeout() {
        return socketTimeout;
    }
    /**
     * @param socketTimeout
     * @return
     */
    public HttpClientBuilderConfiguration setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }
    protected Integer getChunkSize() {
        return chunkSize;
    }
    /**
     * @param chunkSize
     * @return
     */
    public HttpClientBuilderConfiguration setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
        return this;
    }
    protected Integer getMaxConnectionPerRoute() {
        return maxConnectionPerRoute;
    }
    /**
     * @param maxConnectionPerRoute
     * @return
     */
    public HttpClientBuilderConfiguration setMaxConnectionPerRoute(Integer maxConnectionPerRoute) {
        this.maxConnectionPerRoute = maxConnectionPerRoute;
        return this;
    }
    protected Integer getMaxConnectionTotal() {
        return maxConnectionTotal;
    }
    /**
     * @param maxConnectionTotal
     * @return
     */
    public HttpClientBuilderConfiguration setMaxConnectionTotal(Integer maxConnectionTotal) {
        this.maxConnectionTotal = maxConnectionTotal;
        return this;
    }
    /**
     * @return
     */
    public Boolean getProxy() {
        return proxy;
    }
    /**
     * @param proxy
     * @return
     */
    public HttpClientBuilderConfiguration setProxy(Boolean proxy) {
        this.proxy = proxy;
        return this;
    }
    protected Boolean getStreaming() {
        return streaming;
    }
    /**
     * @param streaming
     * @return
     */
    public HttpClientBuilderConfiguration setStreaming(Boolean streaming) {
        this.streaming = streaming;
        return this;
    }
    protected Boolean getSslCertificateVerification() {
        return sslCertificateVerification;
    }
    /**
     * @param sslCertificateVerification
     * @return
     */
    public HttpClientBuilderConfiguration setSslCertificateVerification(Boolean sslCertificateVerification) {
        this.sslCertificateVerification = sslCertificateVerification;
        return this;
    }
    protected HttpClientTracesConfiguration getTraces() {
        return traces;
    }
    /**
     * @param traces
     * @return
     */
    public HttpClientBuilderConfiguration setTraces(HttpClientTracesConfiguration traces) {
        this.traces = traces;
        return this;
    }
    protected boolean isTracesActive() {
        return this.traces != null && (this.traces.isRequestActive() || this.traces.isResponseActive());
    }
}
