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

package org.flcit.springboot.http.client.core.loadbalancer.domain;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import org.flcit.commons.core.util.ArrayUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class FailOverConfiguration {

    private boolean active;
    private int failMaxAttempt = 3;
    private int retryMaxAttempt = 2;
    private long timeout = 60000;
    private int[] failStatus;
    private int[] retryStatus;

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
    public int getFailMaxAttempt() {
        return failMaxAttempt;
    }
    /**
     * @param failMaxAttempt
     */
    public void setFailMaxAttempt(int failMaxAttempt) {
        this.failMaxAttempt = failMaxAttempt;
    }
    /**
     * @return
     */
    public int getRetryMaxAttempt() {
        return retryMaxAttempt;
    }
    /**
     * @param retryMaxAttempt
     */
    public void setRetryMaxAttempt(int retryMaxAttempt) {
        this.retryMaxAttempt = retryMaxAttempt;
    }
    /**
     * @return
     */
    public long getTimeout() {
        return timeout;
    }
    /**
     * @param timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    /**
     * @return
     */
    public int[] getFailStatus() {
        return failStatus;
    }
    /**
     * @param failStatus
     */
    public void setFailStatus(int[] failStatus) {
        this.failStatus = failStatus;
    }
    /**
     * @return
     */
    public int[] getRetryStatus() {
        return retryStatus;
    }
    /**
     * @param retryStatus
     */
    public void setRetryStatus(int[] retryStatus) {
        this.retryStatus = retryStatus;
    }
    /**
     * @param e
     * @return
     */
    public boolean isFail(RestClientException e) {
        return isStatusCodes(e, 503)
                || (e.getCause() instanceof IOException && isFailException((IOException) e.getCause()))
                || isStatusCodes(e, failStatus);
    }
    /**
     * @param e
     * @return
     */
    public static final boolean isFailException(IOException e) {
        return isException(e, ConnectException.class)
                || isException(e, UnknownHostException.class);
    }
    private static final <T extends IOException> boolean isException(IOException e, Class<T> clazz) {
        return e.getClass().equals(clazz)
                || (e.getCause() instanceof IOException
                        && (e.getCause().getClass().equals(clazz)
                                || (e.getCause().getCause() instanceof IOException && e.getCause().getCause().getClass().equals(clazz))));
    }
    /**
     * @param e
     * @return
     */
    public boolean isRetry(RestClientException e) {
        return RestClientResponseException.class.isAssignableFrom(e.getClass())
                && ArrayUtils.contains(retryStatus, ((RestClientResponseException) e).getRawStatusCode());
    }
    private static final boolean isStatusCodes(RestClientException e, int... statusCodes) {
        return RestClientResponseException.class.isAssignableFrom(e.getClass())
                && ArrayUtils.contains(statusCodes, ((RestClientResponseException) e).getRawStatusCode());
    }
}
