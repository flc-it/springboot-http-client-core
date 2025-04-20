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

import org.flcit.springboot.http.client.core.loadbalancer.provider.FailOverLoadBalancer;
import org.flcit.springboot.http.client.core.loadbalancer.provider.LoadBalancerProvider;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
abstract class AbstractLoadBalancer implements LoadBalancer {

    final LoadBalancerProvider loadBalancerProvider;

    AbstractLoadBalancer(LoadBalancerProvider loadBalancerProvider) {
        this.loadBalancerProvider = loadBalancerProvider;
    }

    abstract String chooseUrl(String[] urls);

    @SuppressWarnings("java:S1172")
    String chooseUrl(String[] urls, String clientIp) {
        return chooseUrl(urls);
    }

    @Override
    public String getUrl() {
        final String url = chooseUrl(this.loadBalancerProvider.getUrls());
        takeUrl(url);
        return url;
    }

    @Override
    public String getUrl(String clientIp) {
        final String url = chooseUrl(this.loadBalancerProvider.getUrls(), clientIp);
        takeUrl(url);
        return url;
    }

    private void takeUrl(final String url) {
        if (loadBalancerProvider instanceof FailOverLoadBalancer) {
            ((FailOverLoadBalancer) loadBalancerProvider).take(url);
        }
    }

    @Override
    public boolean hasFailOver() {
        return loadBalancerProvider instanceof FailOverLoadBalancer;
    }

    @Override
    public String fail(String callUrl, long timeout) {
        if (loadBalancerProvider instanceof FailOverLoadBalancer) {
            return ((FailOverLoadBalancer) loadBalancerProvider).fail(callUrl, timeout);
        }
        throw new IllegalStateException();
    }

    @Override
    public void ok(String callUrl) {
        if (loadBalancerProvider instanceof FailOverLoadBalancer) {
            ((FailOverLoadBalancer) loadBalancerProvider).ok(callUrl);
        } else {
            throw new IllegalStateException();
        }
    }

}
