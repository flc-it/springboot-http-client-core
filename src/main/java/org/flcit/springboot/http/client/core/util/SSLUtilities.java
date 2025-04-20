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

package org.flcit.springboot.http.client.core.util;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class SSLUtilities {

    private SSLUtilities() { }

    private static final TrustManager[] _trustManagers = trustAllHttpsCertificates();

    private static final SSLContext _SSLContext = trustAllHttpsCertificatesSSLContext();

    /**
     * @return
     */
    public static HttpClient getHttpClient() {
        return getHttpClientBuilder().build();
    }

    /**
     * @return
     */
    public static HttpClientBuilder getHttpClientBuilder() {
        return disableSSLSecurity(HttpClients.custom());
    }

    /**
     * @param httpClientBuilder
     * @return
     */
    public static HttpClientBuilder disableSSLSecurity(HttpClientBuilder httpClientBuilder) {
        return httpClientBuilder
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSSLContext(_SSLContext);
    }

    /**
     * @param connection
     */
    public static void disableSSLSecurity(HttpsURLConnection connection) {
        connection.setHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        connection.setSSLSocketFactory(_SSLContext.getSocketFactory());
    }

    /**
     * @return
     */
    public static TrustManager[] trustAllHttpsCertificates() {
        return new TrustManager[] { new FakeX509TrustManager() };
    }

    /**
     * @return
     */
    public static SSLContext trustAllHttpsCertificatesSSLContext() {
        try {
            final SSLContext sSLContext = SSLContext.getInstance("TLSv1.2");
            sSLContext.init(null, _trustManagers, null);
            return sSLContext;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static final class FakeX509TrustManager implements X509TrustManager {

        private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};

        /**
         * Always trust for client SSL chain peer certificate chain with any
         * authType authentication types.
         *
         * @param chain the peer certificate chain.
         * @param authType the authentication type based on the client
         * certificate.
         * @throws CertificateException 
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            TrustAllStrategy.INSTANCE.isTrusted(chain, authType);
        }

        /**
         * Always trust for server SSL chain peer certificate chain with any
         * authType exchange algorithm types.
         *
         * @param chain the peer certificate chain.
         * @param authType the key exchange algorithm used.
         * @throws CertificateException 
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            TrustAllStrategy.INSTANCE.isTrusted(chain, authType);
        }

        /**
         * Return an empty array of certificate authority certificates which are
         * trusted for authenticating peers.
         *
         * @return a empty array of issuer certificates.
         */
        public X509Certificate[] getAcceptedIssuers() {
            return _AcceptedIssuers;
        }
    }

}