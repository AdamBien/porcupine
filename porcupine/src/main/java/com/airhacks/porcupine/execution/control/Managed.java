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
 *
 *
 * See
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html">ThreadPoolExecutor</a>
 *
 */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface Managed {

    public static final String UNSET = "-";

    @Nonbinding
    int corePoolSize() default 4;

    @Nonbinding
    int maxPoolSize() default 10;

    /**
     *
     * @return the keep alive time in seconds.
     *
     */
    @Nonbinding
    int keepAliveTime() default 1;

    @Nonbinding
    int queueCapacity() default 10;
}
