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

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import org.flcit.springboot.http.client.core.interceptor.oauth2.OAuth2GrantType;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public abstract class BaseOAuth2Authentication {

    private OAuth2GrantType grantType;
    private String scope;

    /**
     * @return
     */
    public OAuth2GrantType getGrantType() {
        return grantType;
    }
    /**
     * @param grantType
     */
    public void setGrantType(OAuth2GrantType grantType) {
        this.grantType = grantType;
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

    abstract void fillRequest(final MultiValueMap<String, String> request);

    /**
     * @return
     */
    public final MultiValueMap<String, String> generateRequest() {
        final MultiValueMap<String, String> request = new LinkedMultiValueMap<>(3);
        request.add("grant_type", this.grantType.getValue());
        if (StringUtils.hasLength(this.scope)) {
            request.add("scope", this.scope);
        }
        fillRequest(request);
        return request;
    }

}
