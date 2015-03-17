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
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

/**
 *
 * @author airhacks.com
 */
@Provider
public class HttpHeaderStatisticInjector implements WriterInterceptor {

    @Inject
    Instance<List<Statistics>> statistics;

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        List<Statistics> pipelines = statistics.get();
        MultivaluedMap<String, Object> headers = context.getHeaders();
        pipelines.forEach(s -> headers.add("x-porcupine-statistics-" + s.getPipelineName(), serializeStatistics(s)));
        context.proceed();
    }

    public String serializeStatistics(Statistics statistics) {
        JsonObjectBuilder stats = Json.createObjectBuilder();
        stats.add("pipelineName", statistics.getPipelineName());
        final String handlerName = statistics.getRejectedExecutionHandlerName();
        if (handlerName != null) {
            stats.add("rejectedExecutionHandlerName", handlerName);
        }
        stats.add("activeThreadCount", statistics.getActiveThreadCount());
        stats.add("completedTaskCount", statistics.getCompletedTaskCount());
        stats.add("corePoolSize", statistics.getCorePoolSize());
        stats.add("currentThreadPoolSize", statistics.getCurrentThreadPoolSize());
        stats.add("largestThreadPoolSize", statistics.getLargestThreadPoolSize());
        stats.add("maximumPoolSize", statistics.getMaximumPoolSize());
        stats.add("rejectedTasks", statistics.getRejectedTasks());
        stats.add("remainingQueueCapacity", statistics.getRemainingQueueCapacity());
        stats.add("minQueueCapacity", statistics.getMinQueueCapacity());
        stats.add("totalNumberOfTasks", statistics.getTotalNumberOfTasks());
        StringWriter writer = new StringWriter();
        try (JsonWriter outputWriter = Json.createWriter(writer)) {
            outputWriter.writeObject(stats.build());
        }
        return writer.toString();
    }

}
