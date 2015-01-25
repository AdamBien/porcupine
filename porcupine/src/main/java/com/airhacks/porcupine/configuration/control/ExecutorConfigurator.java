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
package com.airhacks.porcupine.configuration.control;

import com.airhacks.porcupine.execution.boundary.Dedicated;
import com.airhacks.porcupine.execution.control.ExecutorConfiguration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import javax.enterprise.inject.Specializes;

/**
 * This class is meant to be overridden and specialized {@link Specializes} to
 * change the {@link ThreadPoolExecutor} configuration.
 *
 * @author airhacks.com
 */
public class ExecutorConfigurator {

    /**
     *
     * @param name the name used within the {@link Dedicated} qualifier.
     * @return a default configuration, unless overridden
     */
    public ExecutorConfiguration forPipeline(String name) {
        return defaultConfigurator();
    }

    /**
     *
     * @return the default configuration for all injected
     * {@link ExecutorService} instances (unless overridden and specialized
     * {@link Specializes})
     */
    public ExecutorConfiguration defaultConfigurator() {
        return ExecutorConfiguration.defaultConfiguration();
    }

}
