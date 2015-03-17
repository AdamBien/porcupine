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
package com.airhacks.porcupine.interceptors;

import com.airhacks.porcupine.execution.entity.Statistics;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author airhacks.com
 */
public class HttpHeaderStatisticInjectorTest {

    private HttpHeaderStatisticInjector cut;

    @Before
    public void init() {
        this.cut = new HttpHeaderStatisticInjector();
    }

    @Test
    public void serializeStatistics() {
        Statistics stats = new Statistics("test", 0, 1, 2, 3, 4, 5, 6, 7, "duke", 8);
        String result = this.cut.serializeStatistics(stats);
        assertNotNull(result);
    }

}
