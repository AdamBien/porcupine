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

import com.airhacks.porcupine.configuration.control.ExecutorConfigurator;
import com.airhacks.porcupine.execution.control.ManagedThreadFactoryExposerMock;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class DefaulThreadPoolExecutorTest {

    ExecutorServiceExposer cut;

    @Before
    public void init() {
        this.cut = new ExecutorServiceExposer();
        this.cut.threadFactory = new ManagedThreadFactoryExposerMock().expose();
    }

    @Test
    public void executeRunnable() throws InterruptedException, ExecutionException {
        ExecutorConfigurator configurator = new ExecutorConfigurator();
        ThreadPoolExecutor executor = this.cut.createThreadPoolExecutor(configurator.defaultConfigurator(), (Runnable r, ThreadPoolExecutor e) -> {
            fail("Task was rejected");
        });
        String result = executor.submit(this::getMessage).get();
        assertTrue(result.startsWith("+"));
    }

    public String getMessage() {
        return "+ " + System.currentTimeMillis();
    }

}
