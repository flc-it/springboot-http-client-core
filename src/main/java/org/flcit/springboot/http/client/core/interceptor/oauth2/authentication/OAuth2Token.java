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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class OAuth2Token {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private long expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;

    /**
     * @return
     */
    public String getAccessToken() {
        return accessToken;
    }
    /**
     * @param accessToken
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    /**
     * @return
     */
    public String getTokenType() {
        return tokenType;
    }
    /**
     * @param tokenType
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    /**
     * @return
     */
    public long getExpiresIn() {
        return expiresIn;
    }
    /**
     * @param expiresIn
     */
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    /**
     * @return
     */
    public String getRefreshToken() {
        return refreshToken;
    }
    /**
     * @param refreshToken
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    /**
     * @return
     */
    public String getScope() {
        return scope;
    }
    /**
     * @param scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

}
