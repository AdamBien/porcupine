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
import java.io.File;
import java.util.concurrent.ThreadFactory;
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
public class CustomThreadFactoryExposerTest {

    @Inject
    ThreadFactory custom;

    @Deployment
    public static Archive create() {
        return ShrinkWrap.create(WebArchive.class).
                addClasses(CustomThreadFactoryExposer.class,
                        ThreadFactoryExposer.class).
                addAsManifestResource(new File("target/classes/META-INF/beans.xml"), "beans.xml");
    }

    @Test
    public void customThreadInjected() {
        Thread thread = custom.newThread(null);
        assertThat(thread.getName(), is("-custom-"));
    }

}
