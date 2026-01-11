package com.prithvianilk.mybestofriendo.contextservice.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable wide event logging for a method.
 * When applied to a method, the WideEventContext will be logged at the end of
 * the method execution
 * and then cleared to prevent memory leaks.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithWideEventLogging {
}
