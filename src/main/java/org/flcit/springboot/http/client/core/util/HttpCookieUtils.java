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

package org.flcit.springboot.http.client.core.util;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class HttpCookieUtils {

    private HttpCookieUtils() { }

    /**
     * @param cookies
     * @param cookieName
     * @return
     */
    public static HttpCookie getValidCookie(final List<String> cookies, final String cookieName) {
        if (CollectionUtils.isEmpty(cookies)
                || !StringUtils.hasLength(cookieName)) {
            return null;
        }
        for (String cookie : cookies) {
            final List<HttpCookie> httpCookies = HttpCookie.parse(cookie);
            if (CollectionUtils.isEmpty(httpCookies)) {
                continue;
            }
            for (HttpCookie httpCookie : httpCookies) {
                if (httpCookie.getName().equals(cookieName)
                        && !httpCookie.hasExpired()) {
                    return httpCookie;
                }
            }
        }
        return null;
    }

    /**
     * @param cookies
     * @param cookiesName
     * @return
     */
    public static List<HttpCookie> getValidCookies(final List<String> cookies, final String[] cookiesName) {
        if (CollectionUtils.isEmpty(cookies)
                || ObjectUtils.isEmpty(cookiesName)) {
            return Collections.emptyList();
        }
        final List<HttpCookie> cookiesResult = new ArrayList<>(cookiesName.length);
        for (String cookie : cookies) {
            final List<HttpCookie> httpCookies = HttpCookie.parse(cookie);
            if (CollectionUtils.isEmpty(httpCookies)) {
                continue;
            }
            for (HttpCookie httpCookie : httpCookies) {
                if (!httpCookie.hasExpired()
                        && ObjectUtils.containsElement(cookiesName, httpCookie.getName())) {
                    cookiesResult.add(httpCookie);
                }
            }
        }
        return cookiesResult;
    }

    /**
     * @param cookie
     */
    public static void invalidCookie(final HttpCookie cookie) {
        if (cookie != null) {
            cookie.setMaxAge(0);
        }
    }

    /**
     * @param cookies
     */
    public static void invalidCookies(final List<HttpCookie> cookies) {
        for (HttpCookie cookie: cookies) {
            invalidCookie(cookie);
        }
    }

}
