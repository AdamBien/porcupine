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

import com.airhacks.porcupine.execution.entity.Pipeline;
import com.airhacks.porcupine.execution.entity.Statistics;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author airhacks.com
 */
@ApplicationScoped
public class PipelineStore {

    ConcurrentHashMap<String, Pipeline> pipelines;

    @PostConstruct
    public void init() {
        this.pipelines = new ConcurrentHashMap<>();
    }

    public Pipeline get(String name) {
        return this.pipelines.get(name);
    }

    public void put(String pipelineName, Pipeline pipeline) {
        this.pipelines.put(pipelineName, pipeline);
    }

    public Collection<Pipeline> pipelines() {
        return this.pipelines.values();

    }

    public Statistics getStatistics(String name) {
        Pipeline pipeline = this.pipelines.get(name);
        if (pipeline != null) {
            return pipeline.getStatistics();
        } else {
            return new Statistics();
        }
    }

    public int getNumberOfPipelines() {
        return this.pipelines.size();
    }

    public List<Statistics> getAllStatistics() {
        return this.pipelines.values().stream().
                map(p -> p.getStatistics()).
                collect(Collectors.toList());
    }

    public void clear() {
        this.pipelines.clear();
    }

    @PreDestroy
    public void shutdown() {
        this.pipelines.values().parallelStream().forEach(p -> p.shutdown());
        this.pipelines.clear();
    }

}
