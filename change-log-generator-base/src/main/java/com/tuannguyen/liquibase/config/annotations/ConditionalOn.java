package com.tuannguyen.liquibase.config.annotations;

import java.lang.annotation.*;
import java.util.function.Predicate;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Repeatable(ConditionalList.class)
public @interface ConditionalOn {
	String field() default "";

	String[] value() default "";

	Class<? extends Predicate> predicateClass() default Predicate.class;
}
