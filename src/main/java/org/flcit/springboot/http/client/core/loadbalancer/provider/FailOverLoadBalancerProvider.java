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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import org.flcit.commons.core.util.ArrayUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class FailOverLoadBalancerProvider extends BaseLoadBalancerProvider implements FailOverLoadBalancer {

    String[] currentUrls;
    String[] retryFailedUrls;
    Map<String, Long> failedUrls;

    /**
     * @param urls
     */
    public FailOverLoadBalancerProvider(String[] urls) {
        super(urls);
        this.currentUrls = super.getUrls();
    }

    /**
     * @param urls
     * @param weights
     */
    public FailOverLoadBalancerProvider(String[] urls, int[] weights) {
        super(urls, weights);
        this.currentUrls = super.getUrls();
    }

    @Override
    public String[] getUrls() {
        if (failedUrls == null
                && retryFailedUrls == null) {
            return super.getUrls();
        }
        synchronized (this) {
            return this.currentUrls == null || (this.retryFailedUrls == null && this.failedUrls == null) ? super.getUrls() : StringUtils.concatenateStringArrays(this.currentUrls, getFailedExpiredUrls());
        }
    }

    @Override
    public final String fail(final String callUrl, long timeout) {
        final String url = getInstance(callUrl);
        synchronized (this) {
            if (this.failedUrls == null) {
                this.failedUrls = new HashMap<>(1);
            }
            this.failedUrls.put(url, System.currentTimeMillis() + timeout);
            if (this.retryFailedUrls != null) {
                this.retryFailedUrls = ArrayUtils.remove(this.retryFailedUrls, url);
            }
            this.currentUrls = ArrayUtils.remove(this.currentUrls, url);
            if (ObjectUtils.isEmpty(this.currentUrls)) {
                reset();
            }
        }
        return url;
    }

    @Override
    public void ok(String callUrl) {
        if (this.retryFailedUrls == null) {
            return;
        }
        final String url = getInstance(callUrl);
        if (url == null || !ObjectUtils.containsElement(this.retryFailedUrls, url)) {
            return;
        }
        synchronized (this) {
            this.retryFailedUrls = ArrayUtils.remove(this.retryFailedUrls, url);
            this.failedUrls.remove(url);
            this.currentUrls = ObjectUtils.addObjectToArray(this.currentUrls, url);
            checkNoFailedUrls();
        }
    }

    private String getInstance(String callUrl) {
        if (callUrl == null) {
            return null;
        }
        String res = null;
        for (String url : super.getUrls()) {
            if (callUrl.startsWith(url)
                    && (res == null || res.length() < url.length())) {
                res = url;
            }
        }
        return res;
    }

    private final boolean checkNoFailedUrls() {
        if (CollectionUtils.isEmpty(failedUrls)
                && ObjectUtils.isEmpty(retryFailedUrls)) {
            reset();
            return true;
        }
        return false;
    }

    private final String[] getFailedExpiredUrls() {
        List<String> expired = null;
        for (Entry<String, Long> entry : this.failedUrls.entrySet()) {
            if (expired(entry.getValue())) {
                if (expired == null) {
                    expired = new ArrayList<>(1);
                }
                expired.add(entry.getKey());
            }
        }
        return org.flcit.commons.core.util.CollectionUtils.toArray(expired);
    }

    private final void reset() {
        this.retryFailedUrls = null;
        this.failedUrls = null;
        this.currentUrls = super.getUrls();
    }

    private static final boolean expired(Long timeMillis) {
        return timeMillis == null || timeMillis < System.currentTimeMillis();
    }

    @Override
    public void take(String url) {
        if (failedUrls == null
                || !failedUrls.containsKey(url)) {
            return;
        }
        synchronized (this) {
            if (this.failedUrls.remove(url) != null) {
                this.retryFailedUrls = ObjectUtils.addObjectToArray(this.retryFailedUrls, url);
            }
        }
    }

}
