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

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import org.flcit.commons.core.util.DateUtils;
import org.flcit.springboot.http.client.core.domain.QueryParamsListMode;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class QueryParamsUtils {

    private QueryParamsUtils() {}

    /**
     * @param queryParams
     * @return
     */
    public static String build(Map<String, ?> queryParams) {
        return build(queryParams, QueryParamsListMode.ONE_VALUE);
    }

    /**
     * @param queryParams
     * @param mode
     * @return
     */
    public static String build(Map<String, ?> queryParams, QueryParamsListMode mode) {
        if (CollectionUtils.isEmpty(queryParams)) {
            return org.flcit.commons.core.util.StringUtils.EMPTY;
        }
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
        for (Entry<String, ?> entry : queryParams.entrySet()) {
            if (!StringUtils.hasLength(entry.getKey())) {
                continue;
            }
            if (mode == QueryParamsListMode.JOIN_VALUES) {
                addQueryParam(uriBuilder, entry.getKey(), entry.getValue());
            } else {
                addQueryParamOneValue(uriBuilder, entry.getKey(), entry.getValue());
            }
        }
        return uriBuilder.build(true).toUriString();
    }

    private static void addQueryParamOneValue(UriComponentsBuilder uriBuilder, String key, Object value) {
        if (value != null && Iterable.class.isAssignableFrom(value.getClass())) {
            for (Object elem: (Iterable<?>) value) {
                addQueryParamOneValue(uriBuilder, key, elem);
            }
        } else if (value != null && value.getClass().isArray()) {
            for (Object elem: (Object[]) value) {
                addQueryParamOneValue(uriBuilder, key, elem);
            }
        } else {
            addQueryParam(uriBuilder, key, value);
        }
    }

    private static void addQueryParam(UriComponentsBuilder uriBuilder, String key, Object value) {
        if (StringUtils.hasLength(key)) {
            uriBuilder.queryParam(encode(key), encode(value));
        }
    }

    private static String encode(Object value) {
        return value == null ? null : UriUtils.encode(transform(value), StandardCharsets.UTF_8);
    }

    private static String transform(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return transform((Date) value);
        }
        if (value instanceof Optional) {
            return transform(((Optional<?>) value).orElse(null));
        }
        if (Iterable.class.isAssignableFrom(value.getClass())) {
            return transform((Iterable<?>) value);
        }
        if (value.getClass().isArray()) {
            return transform((Object[]) value);
        }
        return value.toString();
    }

    private static String transform(Date date) {
        return date == null ? null : DateUtils.formatDateISO8601(date);
    }

    private static String transform(Iterable<?> iterable) {
        return iterable == null ? null : org.flcit.commons.core.util.StringUtils.join(",", iterable);
    }

    private static String transform(Object[] array) {
        return array == null ? null : StringUtils.arrayToCommaDelimitedString(array);
    }

}
