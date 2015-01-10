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
import java.io.File;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author airhacks.com
 */
@RunWith(Arquillian.class)
public class ExecutorServiceExposerIT {

    @Inject
    ExecutoServiceInjectionTarget testSupport;

    @Inject
    PipelineStore ps;

    @Deployment
    public static Archive create() {
        return ShrinkWrap.create(WebArchive.class).
                addClasses(ManagedThreadFactoryExposerMock.class,
                        ExecutoServiceInjectionTarget.class,
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
    public void statisticsAreAvailable() {
        Pipeline first = ps.get("first");
        assertNotNull(first);
        assertNotNull(first.getStatistics());
    }

}
