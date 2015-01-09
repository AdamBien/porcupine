package com.airhacks.porcupine.execution.control;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 *
 * @author airhacks.com
 */
@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface Managed {
}
