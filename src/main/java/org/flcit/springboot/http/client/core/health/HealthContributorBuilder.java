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

package org.flcit.springboot.http.client.core.health;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.util.Assert;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class HealthContributorBuilder {

    private HealthContributorBuilder() { }

    /**
     * @param <I>
     * @param <B>
     * @param indicatorClass
     * @param beans
     * @return
     */
    public static final <I extends org.springframework.boot.actuate.health.HealthIndicator, B> HealthContributor createContributor(Class<I> indicatorClass, Map<String, B> beans) {
        Assert.notEmpty(beans, "Beans must not be empty");
        if (beans.size() == 1) {
            return createIndicator(indicatorClass, beans.values().iterator().next());
        }
        return createComposite(indicatorClass, beans);
    }

    private static final <I extends org.springframework.boot.actuate.health.HealthIndicator, B> HealthContributor createComposite(Class<I> indicatorClass, Map<String, B> beans) {
        return CompositeHealthContributor.fromMap(beans, bean -> createIndicator(indicatorClass, bean));
    }

    private static final <I extends org.springframework.boot.actuate.health.HealthIndicator, B> I createIndicator(Class<I> indicatorClass, B bean) {
        try {
            Constructor<I> constructor = indicatorClass.getDeclaredConstructor(bean.getClass());
            return BeanUtils.instantiateClass(constructor, bean);
        }
        catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to create health indicator " + indicatorClass + " for bean type " + bean.getClass(), ex);
        }
    }

}
