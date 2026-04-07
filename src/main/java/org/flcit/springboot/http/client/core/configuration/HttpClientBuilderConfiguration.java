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

import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;

/**
 * 
 * @since 1.0.0
 * @author Florian Lestic
 */
public class HttpClientBuilderConfiguration {

    private Boolean simpleClient;
    private Integer connectTimeout;
    private Integer socketTimeout;
    private Integer chunkSize;
    private Boolean proxy;
    private Boolean sslCertificateVerification;
    private HttpClientTracesConfiguration traces;

    /*
     * Additionnals properties for the Apache Client, not usable for Simple Client
     */
    private RequestConfiguration request;
    private PoolConnectionManagerConfiguration poolConnectionManager;

    class RequestConfiguration {
        private Boolean redirects;
        private Integer maxRedirects;
        private Boolean contentCompression;
        private Integer defaultKeepAlive;
        private Integer connectionRequestTimeout;
        private Integer responseTimeout;

        protected Boolean getRedirects() {
            return redirects;
        }
        public RequestConfiguration setRedirects(Boolean redirects) {
            this.redirects = redirects;
            return this;
        }
        protected Integer getMaxRedirects() {
            return maxRedirects;
        }
        public RequestConfiguration setMaxRedirects(Integer maxRedirects) {
            this.maxRedirects = maxRedirects;
            return this;
        }
        protected Boolean getContentCompression() {
            return contentCompression;
        }
        public RequestConfiguration setContentCompression(Boolean contentCompression) {
            this.contentCompression = contentCompression;
            return this;
        }
        protected Integer getDefaultKeepAlive() {
            return defaultKeepAlive;
        }
        public RequestConfiguration setDefaultKeepAlive(Integer defaultKeepAlive) {
            this.defaultKeepAlive = defaultKeepAlive;
            return this;
        }
        protected Integer getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }
        public RequestConfiguration setConnectionRequestTimeout(Integer connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
            return this;
        }
        protected Integer getResponseTimeout() {
            return responseTimeout;
        }
        public RequestConfiguration setResponseTimeout(Integer responseTimeout) {
            this.responseTimeout = responseTimeout;
            return this;
        }
    }

    class PoolConnectionManagerConfiguration {
        private Integer maxConnectionPerRoute;
        private Integer maxConnectionTotal;
        private PoolReusePolicy reusePolicy;
        private PoolConcurrencyPolicy concurrencyPolicy;
        private ConnectionConfiguration connection;

        class ConnectionConfiguration {
            private Integer timeToLive;
            private Integer validateAfterInactivity;

            protected Integer getTimeToLive() {
                return timeToLive;
            }
            /**
             * @param timeToLive
             * @return
             */
            public ConnectionConfiguration setTimeToLive(Integer timeToLive) {
                this.timeToLive = timeToLive;
                return this;
            }
            protected Integer getValidateAfterInactivity() {
                return validateAfterInactivity;
            }
            /**
             * @param validateAfterInactivity
             * @return
             */
            public ConnectionConfiguration setValidateAfterInactivity(Integer validateAfterInactivity) {
                this.validateAfterInactivity = validateAfterInactivity;
                return this;
            }
        }

        protected Integer getMaxConnectionPerRoute() {
            return maxConnectionPerRoute;
        }

        /**
         * @param maxConnectionPerRoute
         * @return
         */
        public PoolConnectionManagerConfiguration setMaxConnectionPerRoute(Integer maxConnectionPerRoute) {
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
        public PoolConnectionManagerConfiguration setMaxConnectionTotal(Integer maxConnectionTotal) {
            this.maxConnectionTotal = maxConnectionTotal;
            return this;
        }

        protected PoolReusePolicy getReusePolicy() {
            return reusePolicy;
        }

        /**
         * @param reusePolicy
         * @return
         */
        public PoolConnectionManagerConfiguration setReusePolicy(PoolReusePolicy reusePolicy) {
            this.reusePolicy = reusePolicy;
            return this;
        }

        protected PoolConcurrencyPolicy getConcurrencyPolicy() {
            return concurrencyPolicy;
        }

        /**
         * @param concurrencyPolicy
         * @return
         */
        public PoolConnectionManagerConfiguration setConcurrencyPolicy(PoolConcurrencyPolicy concurrencyPolicy) {
            this.concurrencyPolicy = concurrencyPolicy;
            return this;
        }

        protected ConnectionConfiguration getConnection() {
            return connection;
        }

        /**
         * @param connection
         * @return
         */
        public PoolConnectionManagerConfiguration setConnection(ConnectionConfiguration connection) {
            this.connection = connection;
            return this;
        }
    }
    

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
    protected Boolean getProxy() {
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

    protected RequestConfiguration getRequest() {
        return request;
    }
    /**
     * @param request
     * @return
     */
    public HttpClientBuilderConfiguration setRequest(RequestConfiguration request) {
        this.request = request;
        return this;
    }
    protected PoolConnectionManagerConfiguration getPoolConnectionManager() {
        return poolConnectionManager;
    }
    /**
     * @param poolConnectionManager
     * @return
     */
    public HttpClientBuilderConfiguration setPoolConnectionManager(PoolConnectionManagerConfiguration poolConnectionManager) {
        this.poolConnectionManager = poolConnectionManager;
        return this;
    }
}
