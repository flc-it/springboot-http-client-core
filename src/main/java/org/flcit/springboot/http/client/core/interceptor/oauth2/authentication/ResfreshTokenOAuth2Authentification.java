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

import com.fasterxml.jackson.annotation.JsonProperty;

import org.flcit.springboot.http.client.core.interceptor.oauth2.OAuth2GrantType;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class ResfreshTokenOAuth2Authentification extends BaseOAuth2Authentication {

    @JsonProperty("refresh_token")
    private String refreshToken;

    /**
     * 
     */
    public ResfreshTokenOAuth2Authentification() {
        setGrantType(OAuth2GrantType.refresh_token);
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

    @Override
    void fillRequest(final MultiValueMap<String, String> request) {
        request.add("refresh_token", refreshToken);
    }

}
