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

package org.flcit.springboot.http.client.core.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import org.flcit.commons.core.util.StringUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class HttpUrlCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(ConditionalOnHttpUrl.class.getName());
        if (attrs != null) {
            String prefix = StringUtils.suffix(
                    StringUtils.firstHasLength((String) attrs.getFirst("value"), (String) attrs.getFirst("prefix")),
                    StringUtils.DOT);
            return org.springframework.util.StringUtils.hasLength(context.getEnvironment().getProperty(prefix + "url"))
                    || !ObjectUtils.isEmpty(context.getEnvironment().getProperty(prefix + "load-balancer.urls"));
        }
        return false;
    }

}
