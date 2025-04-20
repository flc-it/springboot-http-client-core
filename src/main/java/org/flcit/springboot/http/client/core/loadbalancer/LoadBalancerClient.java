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

import java.net.URI;

import org.springframework.web.client.RestClientException;

import org.flcit.commons.core.util.ObjectUtils;
import org.flcit.springboot.http.client.core.loadbalancer.domain.FailOverConfiguration;
import org.flcit.springboot.http.client.core.loadbalancer.domain.LoadBalancerAlgorithm;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class LoadBalancerClient {

    private String[] urls;
    private int[] weights;
    private boolean active;
    private FailOverConfiguration failOver;
    private LoadBalancerAlgorithm algorithm;
    private LoadBalancer instance;

    /**
     * @param urls
     */
    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    /**
     * @param weights
     */
    public void setWeights(int[] weights) {
        this.weights = weights;
    }

    /**
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean isFailOver() {
        return active && failOver != null && failOver.isActive();
    }

    /**
     * @return
     */
    public boolean hasFailOver() {
        return active && failOver != null && failOver.isActive() && instance != null && instance.hasFailOver();
    }

    /**
     * @param e
     * @return
     */
    public boolean isFail(RestClientException e) {
        return failOver != null && failOver.isFail(e);
    }
    /**
     * @param e
     * @return
     */
    public boolean isRetry(RestClientException e) {
        return failOver != null && failOver.isRetry(e);
    }
    /**
     * @return
     */
    public FailOverConfiguration getFailOver() {
        return this.failOver;
    }

    /**
     * @param failOver
     */
    public void setFailOver(FailOverConfiguration failOver) {
        this.failOver = failOver;
    }

    /**
     * @param algorithm
     */
    public void setAlgorithm(LoadBalancerAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 
     */
    public void init() {
        if (!active || urls == null || urls.length < 1) {
            this.instance = null;
        } else {
            this.instance = LoadBalancer.getLoadBalancer(algorithm, urls, weights, isFailOver());
        }
    }

    /**
     * @param url
     * @param e
     * @return
     */
    public String fail(URI url, RestClientException e) {
        return fail(url.toString(), e);
    }

    /**
     * @param url
     */
    public void ok(URI url) {
        ok(url.toString());
    }

    /**
     * @param url
     * @param e
     * @return
     */
    public String fail(String url, RestClientException e) {
        if (!hasFailOver()) {
            throw e;
        }
        return instance.fail(url, failOver.getTimeout());
    }

    /**
     * @param url
     */
    public void ok(String url) {
        if (hasFailOver()) {
            instance.ok(url);
        }
    }

    /**
     * @param defaultUrl
     * @return
     */
    public String getUrl(String defaultUrl) {
        if (!active) {
            return defaultUrl;
        }
        if (instance == null) {
            instance = LoadBalancer.getLoadBalancer(algorithm, urls, weights, isFailOver());
        }
        return instance != null ? ObjectUtils.getOrDefault(instance.getUrl(), defaultUrl) : defaultUrl;
    }

}
