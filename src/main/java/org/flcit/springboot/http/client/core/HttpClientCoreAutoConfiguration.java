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

package org.flcit.springboot.http.client.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import org.flcit.springboot.http.client.core.configuration.HttpClientBuilder;
import org.flcit.springboot.http.client.core.configuration.ProxyConfiguration;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@AutoConfiguration
public class HttpClientCoreAutoConfiguration {

    /**
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("proxy")
    @ConditionalOnProperty("proxy.hostname")
    public ProxyConfiguration getProxyConfiguration() {
        return new ProxyConfiguration();
    }

    /**
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("http.client.core.connection.default")
    public HttpClientBuilder getHttpClientCoreBuilder() {
        return new HttpClientBuilder(getProxyConfiguration());
    }

}
