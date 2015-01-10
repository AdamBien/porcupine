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

import static com.airhacks.porcupine.execution.control.ExecutorServiceDedicatedInjectionTarget.CUSTOM_FIRST;
import static com.airhacks.porcupine.execution.control.ExecutorServiceDedicatedInjectionTarget.CUSTOM_SECOND;
import static com.airhacks.porcupine.execution.control.ExecutorServiceInjectionTarget.FIRST;
import static com.airhacks.porcupine.execution.control.ExecutorServiceInjectionTarget.SECOND;
import com.airhacks.porcupine.execution.entity.Pipeline;
import com.airhacks.porcupine.execution.entity.Statistics;
import java.io.File;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
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
    public static Archive create() {
        return ShrinkWrap.create(WebArchive.class).
                addClasses(ManagedThreadFactoryExposerMock.class,
                        ExecutorServiceInjectionTarget.class,
                        ExecutorServiceDedicatedInjectionTarget.class,
                        ExecutorServiceExposer.class,
                        PipelineStore.class,
                        Managed.class).
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

}
