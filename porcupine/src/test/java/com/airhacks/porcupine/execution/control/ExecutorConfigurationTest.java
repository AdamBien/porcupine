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

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author airhacks.com
 */
public class ExecutorConfigurationTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void policyAndQueueSet() {
        ExecutorConfiguration cut = new ExecutorConfiguration.Builder().
                abortPolicy().
                queueCapacity(6).
                build();
        assertNotNull(cut);
    }

    @Test
    public void negativeQueueSizeTest() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage(both(containsString("queue")).and(containsString("should be > 0")));
        new ExecutorConfiguration.Builder().
                queueCapacity(-1).
                build();
    }

    @Test
    public void negativeKeepAliveTime() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage(both(containsString("keepAliveTime")).and(containsString("should be > 0")));
        new ExecutorConfiguration.Builder().
                keepAliveTime(-1).
                build();
    }

    @Test
    public void corePoolSizeLargerThanMaxPoolsize() {
        expected.expect(IllegalStateException.class);
        expected.expectMessage(both(containsString("corePoolSize")).and(containsString("maxPoolSize")));

        new ExecutorConfiguration.Builder().
                corePoolSize(2).
                maxPoolSize(1).
                build();
    }

}
