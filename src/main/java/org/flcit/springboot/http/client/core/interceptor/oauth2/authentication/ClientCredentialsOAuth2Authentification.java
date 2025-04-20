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

package org.flcit.springboot.http.client.core.interceptor.oauth2.authentication;

import org.springframework.util.MultiValueMap;

import org.flcit.springboot.http.client.core.interceptor.oauth2.OAuth2GrantType;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class ClientCredentialsOAuth2Authentification extends BaseOAuth2Authentication {

    private String clientId;
    private String clientSecret;

    /**
     * 
     */
    public ClientCredentialsOAuth2Authentification() {
        setGrantType(OAuth2GrantType.client_credentials);
    }

    /**
     * @return
     */
    public String getClientId() {
        return clientId;
    }
    /**
     * @param clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    /**
     * @return
     */
    public String getClientSecret() {
        return clientSecret;
    }
    /**
     * @param clientSecret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    void fillRequest(final MultiValueMap<String, String> request) {
        request.add("client_id", this.clientId);
        request.add("client_secret", this.clientSecret);
    }

}
