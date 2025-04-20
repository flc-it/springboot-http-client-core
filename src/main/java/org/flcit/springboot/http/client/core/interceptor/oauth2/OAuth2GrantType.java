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

package org.flcit.springboot.http.client.core.interceptor.oauth2;

import org.springframework.util.StringUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@SuppressWarnings("java:S115")
public enum OAuth2GrantType {

    authorization_code,
    password,
    client_credentials,
    refresh_token,
    jwt_bearer("urn:ietf:params:oauth:grant-type:jwt-bearer"),
    device_code("urn:ietf:params:oauth:grant-type:device_code");

    private final String value;

    private OAuth2GrantType() {
        this(null);
    }

    private OAuth2GrantType(String value) {
        this.value = StringUtils.hasLength(value) ? value : name();
    }

    /**
     * @return
     */
    public String getValue() {
        return this.value;
    }

    /**
     *
     */
    @Override
    public String toString() {
        return getValue();
    }

}
