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
import com.airhacks.porcupine.execution.control.PipelineStore;
import com.airhacks.porcupine.execution.entity.Statistics;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author airhacks.com
 */
@RunWith(Arquillian.class)
public class ExecutorServiceStatisticsTest {

    @Inject
    @Dedicated
    private ExecutorService overloaded;

    @Inject
    PipelineStore ps;

    @Deployment
    public static Archive create() {
        return ShrinkWrap.create(WebArchive.class).
                addClasses(ManagedThreadFactoryExposerMock.class,
                        ExecutorServiceExposer.class,
                        ExecutorConfigurator.class,
                        OverloadedConfiguration.class,
                        PipelineStore.class).
                addAsManifestResource(new File("target/classes/META-INF/beans.xml"), "beans.xml");
    }

    @Test
    public void rejectionsAreCountedOnOverload() throws InterruptedException, ExecutionException, TimeoutException {
        this.overloaded.submit(this::block);
        Statistics statistics = ps.getStatistics("overloaded");
        assertThat(statistics.getRejectedTasks(), is(0l));

        this.overloaded.submit(this::block);
        statistics = ps.getStatistics("overloaded");
        assertThat(statistics.getRejectedTasks(), is(1l));

        this.overloaded.submit(this::block);
        statistics = ps.getStatistics("overloaded");
        assertThat(statistics.getRejectedTasks(), is(2l));

    }

    void block() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
    }

}
