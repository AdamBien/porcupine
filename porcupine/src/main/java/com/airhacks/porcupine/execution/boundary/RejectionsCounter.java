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

import com.airhacks.porcupine.execution.control.PipelineStore;
import com.airhacks.porcupine.execution.entity.Pipeline;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author airhacks.com
 */
public class RejectionsCounter implements RejectedExecutionHandler {

    private RejectedExecutionHandler handler;
    private PipelineStore ps;

    public RejectionsCounter(RejectedExecutionHandler handler, PipelineStore pipeline) {
        this.handler = handler;
        this.ps = pipeline;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Pipeline findPipeline = this.ps.findPipeline(executor);
        if (findPipeline != null) {
            findPipeline.taskRejected();
        }
        this.handler.rejectedExecution(r, executor);
    }

}
