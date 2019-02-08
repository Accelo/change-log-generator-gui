package com.tuannguyen.liquibase.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tuannguyen.liquibase.util.transform.Converter;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface PromptConfig
{
	String prompt();

	String config() default "";

	String helpText() default "";

	boolean nullIfEmpty() default false;

	Class<? extends Converter> converter() default Converter.class;
}
