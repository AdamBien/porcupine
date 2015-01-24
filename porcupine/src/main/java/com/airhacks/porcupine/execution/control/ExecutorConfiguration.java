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
package com.airhacks.porcupine.execution.control;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author airhacks.com
 */
public class ExecutorConfiguration {

    private int corePoolSize;
    private int keepAliveTime;
    private int maxPoolSize;
    private int queueCapacity;
    private RejectedExecutionHandler rejectedExecutionHandler;

    private ExecutorConfiguration() {
    }

    public final static class Builder {

        ExecutorConfiguration configuration = new ExecutorConfiguration();

        public Builder corePoolSize(int corePoolSize) {
            configuration.corePoolSize = corePoolSize;
            return this;
        }

        public Builder keepAliveTime(int keepAliveTime) {
            configuration.keepAliveTime = keepAliveTime;
            return this;
        }

        public Builder maxPoolSize(int maxPoolSize) {
            configuration.maxPoolSize = maxPoolSize;
            return this;
        }

        public Builder queueCapacity(int queueCapacity) {
            configuration.queueCapacity = queueCapacity;
            return this;
        }

        public Builder abortPolicy() {
            configuration.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
            return this;
        }

        public Builder callerRunsPolicy() {
            configuration.rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
            return this;
        }

        public Builder discardPolicy() {
            configuration.rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
            return this;
        }

        public Builder discardOldestPolicy() {
            configuration.rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
            return this;
        }

        public Builder customRejectedExecutionHandler(RejectedExecutionHandler reh) {
            configuration.rejectedExecutionHandler = reh;
            return this;
        }

        public ExecutorConfiguration build() {
            return configuration;

        }
    }

    public static final ExecutorConfiguration defaultConfiguration() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int corePoolSize = availableProcessors;
        int maxPoolSize = availableProcessors * 2;
        int keepAliveTime = 1;
        int queueCapacity = 100;
        return new ExecutorConfiguration.Builder().
                corePoolSize(corePoolSize).
                keepAliveTime(keepAliveTime).
                maxPoolSize(maxPoolSize).
                queueCapacity(queueCapacity).
                build();
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

}
