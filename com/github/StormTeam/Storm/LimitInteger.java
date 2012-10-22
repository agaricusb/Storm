package com.github.StormTeam.Storm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LimitInteger {
    int limit() default 100;

    String warning() default "%node cannot be over %limit nor have letters included! Defaulted to value of %limit.";

    boolean correct() default true;
}
