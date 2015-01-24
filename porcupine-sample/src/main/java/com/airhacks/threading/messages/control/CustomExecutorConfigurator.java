package com.airhacks.threading.messages.control;

import com.airhacks.porcupine.configuration.control.ExecutorConfigurator;
import com.airhacks.porcupine.execution.control.ExecutorConfiguration;
import javax.enterprise.inject.Specializes;

/**
 *
 * @author airhacks.com
 */
@Specializes
public class CustomExecutorConfigurator extends ExecutorConfigurator {

    @Override
    public ExecutorConfiguration defaultConfigurator() {
        return super.defaultConfigurator();
    }

    @Override
    public ExecutorConfiguration forPipeline(String name) {
        if ("heavy".equals(name)) {
            return new ExecutorConfiguration(2, 1, 100, 1000);
        }
        return super.forPipeline(name);
    }

}
