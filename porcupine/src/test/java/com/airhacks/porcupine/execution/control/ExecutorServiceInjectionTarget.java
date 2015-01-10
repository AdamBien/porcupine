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

import com.airhacks.porcupine.execution.entity.Statistics;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author airhacks.com
 */
public class ExecutorServiceInjectionTarget {

    public final static String FIRST = "first";
    public final static String SECOND = "second";
    @Inject
    @Managed
    private ExecutorService first;

    @Inject
    @Managed
    private ExecutorService second;

    @Inject
    @Dedicated(FIRST)
    private Instance<Statistics> firstStatistics;

    @Inject
    @Dedicated(SECOND)
    private Instance<Statistics> secondStatistics;

    public Executor getFirst() {
        return first;
    }

    public Executor getSecond() {
        return second;
    }

    public Statistics getFirstStatistics() {
        return this.firstStatistics.get();
    }

    public Statistics getSecondStatistics() {
        return this.secondStatistics.get();
    }

}
