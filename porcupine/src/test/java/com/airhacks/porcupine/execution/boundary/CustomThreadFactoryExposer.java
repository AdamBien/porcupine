/*
 * Copyright 2016 Adam Bien.
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

import com.airhacks.porcupine.execution.control.ThreadFactoryExposer;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;

/**
 *
 * @author airhacks.com
 */
@Specializes
public class CustomThreadFactoryExposer extends ThreadFactoryExposer {

    @Produces
    public ManagedThreadFactory expose() {
        return (r) -> newThread(r);
    }

    Thread newThread(Runnable r) {
        return new Thread("-custom-");
    }
}
