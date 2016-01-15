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
import com.airhacks.porcupine.execution.control.ExecutorServiceDedicatedInjectionTarget;
import static com.airhacks.porcupine.execution.control.ExecutorServiceDedicatedInjectionTarget.CUSTOM_FIRST;
import static com.airhacks.porcupine.execution.control.ExecutorServiceDedicatedInjectionTarget.CUSTOM_SECOND;
import com.airhacks.porcupine.execution.control.ExecutorServiceInjectionTarget;
import static com.airhacks.porcupine.execution.control.ExecutorServiceInjectionTarget.FIRST;
import static com.airhacks.porcupine.execution.control.ExecutorServiceInjectionTarget.SECOND;
import com.airhacks.porcupine.execution.control.ManagedThreadFactoryExposerMock;
import com.airhacks.porcupine.execution.control.PipelineStore;
import com.airhacks.porcupine.execution.entity.Pipeline;
import com.airhacks.porcupine.execution.entity.Statistics;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author airhacks.com
 */
@RunWith(Arquillian.class)
public class ExecutorServiceExposerTest {

    @Inject
    ExecutorServiceInjectionTarget testSupport;

    @Inject
    ExecutorServiceDedicatedInjectionTarget dedicatedInjectionTarget;

    @Inject
    PipelineStore ps;

    @Deployment
    public static WebArchive create() {
        return ShrinkWrap.create(WebArchive.class).
                addClasses(ManagedThreadFactoryExposerMock.class,
                        ExecutorServiceInjectionTarget.class,
                        ExecutorServiceDedicatedInjectionTarget.class,
                        ExecutorServiceExposer.class,
                        ExecutorConfigurator.class,
                        PipelineStore.class).
                addAsManifestResource(new File("target/classes/META-INF/beans.xml"), "beans.xml");
    }

    @Test
    public void multipleExecutorsAreDistinct() {
        Executor first = this.testSupport.getFirst();
        Executor second = this.testSupport.getSecond();
        assertNotNull(first);
        assertNotSame(first, second);
    }

    @Test
    public void numberOfPipelines() {
        int numberOfPipelines = this.ps.getNumberOfPipelines();
        //2 default pipelines in ExecutorServiceInjectionTarget and 2
        // custom pipelines in ExecutorServiceDedicatedInjectionTarget
        assertThat(numberOfPipelines, is(4));
    }

    @Test
    public void numberOfStatistics() {
        List<Statistics> allStatistics = this.ps.getAllStatistics();
        //2 default pipelines in ExecutorServiceInjectionTarget and 2
        // custom pipelines in ExecutorServiceDedicatedInjectionTarget
        assertNotNull(allStatistics);
        assertThat(allStatistics.size(), is(4));
    }

    @Test
    public void statisticsForDefaultPipelineAreAvailable() {
        Statistics firstStatistics = this.testSupport.getFirstStatistics();
        assertNotNull(firstStatistics);
        assertThat(firstStatistics.getPipelineName(), is(FIRST));

        Statistics secondStatistics = this.testSupport.getSecondStatistics();
        assertNotNull(secondStatistics);
        assertThat(secondStatistics.getPipelineName(), is(SECOND));
    }

    @Test
    public void statisticsAreAvailable() {
        Pipeline first = ps.get("first");
        assertNotNull(first);
        assertNotNull(first.getStatistics());
    }

    @Test
    public void injectionOfDedicatedPipelines() {
        Executor first = this.dedicatedInjectionTarget.getFirst();
        assertNotNull(first);
        Statistics firstStatistics = this.dedicatedInjectionTarget.getFirstStatistics();
        assertNotNull(firstStatistics);
        String pipelineName = firstStatistics.getPipelineName();
        assertNotNull(pipelineName);
        assertThat(pipelineName, is(CUSTOM_FIRST));

        Executor second = this.dedicatedInjectionTarget.getSecond();
        assertNotNull(second);
        Statistics secondStatistics = this.dedicatedInjectionTarget.getSecondStatistics();
        assertNotNull(secondStatistics);
        pipelineName = secondStatistics.getPipelineName();
        assertNotNull(pipelineName);
        assertThat(pipelineName, is(CUSTOM_SECOND));
    }

    @Test
    public void eachPipelineContainsDistinctExecutor() {
        long numberOfExecutors = this.ps.pipelines().stream().map(s -> s.getExecutor()).distinct().count();
        long numberOfStatistics = this.ps.getAllStatistics().size();
        assertThat(numberOfExecutors, is(numberOfStatistics));
    }

    @Test
    public void allStatistics() {
        Instance<List<Statistics>> allStatisticsInstance = this.testSupport.getAllStatistics();
        List<Statistics> allStatistics = allStatisticsInstance.get();
        assertThat(allStatistics.size(), is(4));
        assertTrue(allStatistics.stream().filter(s -> s.getPipelineName().equals("first")).count() == 1);
        assertTrue(allStatistics.stream().filter(s -> s.getPipelineName().equals("second")).count() == 1);
        assertTrue(allStatistics.stream().filter(s -> s.getPipelineName().equals("customFirst")).count() == 1);
        assertTrue(allStatistics.stream().filter(s -> s.getPipelineName().equals("customSecond")).count() == 1);
    }

    @Test
    public void execution() throws InterruptedException, ExecutionException, TimeoutException {
        Statistics statistics = this.testSupport.getFirstStatistics();
        long before = statistics.getTotalNumberOfTasks();
        String result = this.testSupport.executeInFirst();
        assertTrue(result.startsWith("+"));
        statistics = this.testSupport.getFirstStatistics();
        long after = statistics.getTotalNumberOfTasks();
        assertThat(after, is(before + 1));
    }

}
