/*
 * Copyright 2015 Adam Bien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airhacks.porcupine.execution.boundary;

import com.airhacks.porcupine.execution.entity.Statistics;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * <p>
 * A qualifier used to injection of configured and managed ExecutorService
 * instances. The <code>value()</code> method defines the name of the
 * ExecutorService which can be injected into repeatedly into different
 * components.
 * <p>
 * The same name can be used to retrieve the corresponding {@link Statistics}.
 *
 * @author airhacks.com
 */
@Qualifier
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dedicated {

    public final static String DEFAULT = "-";

    /**
     * Defines the name for the pipeline (Executor with statistics). If the name
     * is unset, the name of the field is going to be used instead.
     *
     * @return the name of the pipeline. Either a new pipeline is going to be
     * created, or an existing returned.
     */
    @Nonbinding
    String value() default DEFAULT;
}
