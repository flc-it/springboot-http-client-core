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

package org.flcit.springboot.http.client.core.loadbalancer;

import org.flcit.springboot.http.client.core.loadbalancer.domain.LoadBalancerAlgorithm;
import org.flcit.springboot.http.client.core.loadbalancer.provider.BaseLoadBalancerProvider;
import org.flcit.springboot.http.client.core.loadbalancer.provider.FailOverLoadBalancerProvider;
import org.flcit.springboot.http.client.core.loadbalancer.provider.LoadBalancerProvider;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public interface LoadBalancer {

    /**
     * @return
     */
    public String getUrl();
    /**
     * @param clientIp
     * @return
     */
    public String getUrl(String clientIp);
    /**
     * @param callUrl
     * @param timeout
     * @return
     */
    public String fail(String callUrl, long timeout);
    /**
     * @param callUrl
     */
    public void ok(String callUrl);
    /**
     * @return
     */
    public boolean hasFailOver();

    /**
     * @param urls
     * @param failOver
     * @return
     */
    static LoadBalancerProvider getLoadBalancerProvider(String[] urls, boolean failOver) {
        return urls == null || urls.length < 2 || !failOver ? new BaseLoadBalancerProvider(urls)
                : new FailOverLoadBalancerProvider(urls);
    }

    /**
     * @param urls
     * @param weights
     * @param failOver
     * @return
     */
    static LoadBalancerProvider getLoadBalancerProvider(String[] urls, int[] weights, boolean failOver) {
        return urls == null || urls.length < 2 || !failOver ? new BaseLoadBalancerProvider(urls, weights)
                : new FailOverLoadBalancerProvider(urls, weights);
    }

    /**
     * @param url
     * @param failOver
     * @return
     */
    public static LoadBalancer getLoadBalancer(String url, boolean failOver) {
        return new NoLoadBalancer(getLoadBalancerProvider(new String[] { url }, failOver));
    }

    /**
     * @param urls
     * @return
     */
    public static LoadBalancer getLoadBalancer(String[] urls) {
        return getLoadBalancer(null, urls, null, false);
    }

    /**
     * @param algorithm
     * @param urls
     * @param weights
     * @param failOver
     * @return
     */
    public static LoadBalancer getLoadBalancer(LoadBalancerAlgorithm algorithm, String[] urls, int[] weights, boolean failOver) {
        if (urls == null || urls.length < 2) {
            return new NoLoadBalancer(getLoadBalancerProvider(urls, failOver));
        }
        if (algorithm == null) {
            return new RoundRobinLoadBalancer(getLoadBalancerProvider(urls, failOver));
        }
        switch (algorithm) {
        case roundRobin:
            return new RoundRobinLoadBalancer(getLoadBalancerProvider(urls, failOver));
        case random:
            return new RandomLoadBalancer(getLoadBalancerProvider(urls, failOver));
        case ipHash:
            return new IpHashLoadBalancer(getLoadBalancerProvider(urls, failOver));
        case weightRandom:
            return new WeightRandomLoadBalancer(getLoadBalancerProvider(urls, weights, failOver));
        case weightRoundRobin:
            return new WeightRoundRobinLoadBalancer(getLoadBalancerProvider(urls, weights, failOver));
        default:
            return new RoundRobinLoadBalancer(getLoadBalancerProvider(urls, failOver));
        }
    }

}
