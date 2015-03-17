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
package com.airhacks.porcupine.execution.entity;

import com.airhacks.porcupine.execution.control.InstrumentedThreadPoolExecutor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * An internal class -- don't use it in your application.
 *
 * @author airhacks.com
 */
public class Pipeline {

    private final String pipelineName;
    private final InstrumentedThreadPoolExecutor tpe;
    private final AtomicLong rejectedTasks;

    public Pipeline(String pipelineName, InstrumentedThreadPoolExecutor tpe) {
        this.pipelineName = pipelineName;
        this.tpe = tpe;
        this.rejectedTasks = new AtomicLong();
    }

    public Statistics getStatistics() {
        int remainingQueueCapacity = this.tpe.getQueue().remainingCapacity();
        int minQueueCapacity = this.tpe.getMinRemainingQueueCapacity();
        int corePoolSize = this.tpe.getCorePoolSize();
        long completedTaskCount = this.tpe.getCompletedTaskCount();
        int activeThreadCount = this.tpe.getActiveCount();
        int largestThreadPoolSize = this.tpe.getLargestPoolSize();
        int currentThreadPoolSize = this.tpe.getPoolSize();
        long totalNumberOfTasks = this.tpe.getTaskCount();
        int maximumPoolSize = this.tpe.getMaximumPoolSize();
        RejectedExecutionHandler handler = this.tpe.getRejectedExecutionHandler();
        String rejectedExecutionHandlerName = null;
        if (handler != null) {
            rejectedExecutionHandlerName = handler.getClass().getSimpleName();
        }
        return new Statistics(this.pipelineName, remainingQueueCapacity, minQueueCapacity, completedTaskCount, activeThreadCount, corePoolSize, largestThreadPoolSize, currentThreadPoolSize, totalNumberOfTasks, maximumPoolSize, rejectedExecutionHandlerName, this.rejectedTasks.get());

    }

    public ExecutorService getExecutor() {
        return tpe;
    }

    public void shutdown() {
        this.tpe.shutdown();
    }

    public boolean manages(ThreadPoolExecutor executor) {
        return this.tpe == executor;
    }

    public void taskRejected() {
        this.rejectedTasks.incrementAndGet();
    }

}
