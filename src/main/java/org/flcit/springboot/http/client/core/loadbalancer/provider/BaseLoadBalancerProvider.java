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

package org.flcit.springboot.http.client.core.loadbalancer.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class BaseLoadBalancerProvider implements LoadBalancerProvider {

    private final String[] urls;

    /**
     * @param urls
     */
    public BaseLoadBalancerProvider(String[] urls) {
        this.urls = urls;
    }

    /**
     * @param urls
     * @param weights
     */
    public BaseLoadBalancerProvider(String[] urls, int[] weights) {
        this(getUrls(urls, weights));
    }

    @Override
    public String[] getUrls() {
        return this.urls;
    }

    @SuppressWarnings("java:S1168")
    private static final String[] getUrls(String[] urls, int[] weights) {
        if (ObjectUtils.isEmpty(urls)) {
            return null;
        }
        final List<String> values = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            int weight = weights != null && i < weights.length ? weights[i] : 1;
            for (int n = 0; n < weight; n++) {
                values.add(urls[i]);
            }
        }
        return values.toArray(new String[0]);
    }

}
