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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class InstrumentedThreadPoolExecutorTest {
    private static final int QUEUE_CAPACITY = 5;
    
    InstrumentedThreadPoolExecutor cut;

    @Before
    public void init() {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        ThreadFactory factory = (r) -> new Thread(r);
        this.cut = new InstrumentedThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, queue, factory, new InstrumentedThreadPoolExecutor.CallerRunsPolicy());
    }

    @Test
    public void minRemainingQueueCapacity() throws InterruptedException, ExecutionException {
        int minRemainingQueueCapacity = this.cut.getMinRemainingQueueCapacity();
        int remainingCapacity = this.cut.getQueue().remainingCapacity();
        assertThat(minRemainingQueueCapacity, is(remainingCapacity));
        Future<?> result = null;
        for (int i = 0; i < QUEUE_CAPACITY * 2; i++) {
            result = this.cut.submit(blockingFor(100));
        }
        result.get();
        assertThat(this.cut.getMinRemainingQueueCapacity(), is(0));
    }

    Runnable blockingFor(long ms) {
        return () -> {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ex) {
            }
        };
    }

}
