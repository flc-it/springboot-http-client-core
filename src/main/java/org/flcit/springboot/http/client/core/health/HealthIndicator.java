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

package org.flcit.springboot.http.client.core.health;

import java.util.Map;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class HealthIndicator {

    private boolean active;
    private String url;
    private String service;
    private Map<String, String> responseFields;
    private String responseText;

    /**
     * @return
     */
    public boolean isActive() {
        return active;
    }
    /**
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * @return
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * @return
     */
    public String getService() {
        return service;
    }
    /**
     * @param service
     */
    public void setService(String service) {
        this.service = service;
    }
    /**
     * @return
     */
    public Map<String, String> getResponseFields() {
        return responseFields;
    }
    /**
     * @param responseFields
     */
    public void setResponseFields(Map<String, String> responseFields) {
        this.responseFields = responseFields;
    }
    /**
     * @return
     */
    public String getResponseText() {
        return responseText;
    }
    /**
     * @param responseText
     */
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

}
