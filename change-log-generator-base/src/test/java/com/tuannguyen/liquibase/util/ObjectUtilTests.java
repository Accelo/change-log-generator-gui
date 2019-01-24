package com.tuannguyen.liquibase.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ObjectUtilTests {
	@Test
	public void defaultIfNull_givenNull_shouldGiveDefaultString(){
		assertThat(ObjectUtils.defaultIfNull(null, "here"), equalTo("here"));
	}

	@Test
	public void defaultIfNull_givenNotNull_shouldGiveNotNull(){
		assertThat(ObjectUtils.defaultIfNull("not null", "here"), equalTo("not null"));
	}
}
