package com.airhacks.porcupine.execution.control;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 *
 * @author airhacks.com
 */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface Managed {

    @Nonbinding
    int corePoolSize() default 2;

    @Nonbinding
    int maxPoolSize() default 4;

    @Nonbinding
    int keepAliveTime() default 1;

    @Nonbinding
    int queueCapacity() default 10;

    @Nonbinding
    String pipelineName() default "default";

}
