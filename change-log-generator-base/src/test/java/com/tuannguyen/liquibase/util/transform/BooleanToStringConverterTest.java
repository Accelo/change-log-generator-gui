package com.tuannguyen.liquibase.util.transform;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class BooleanToStringConverterTest {
	private BooleanToStringConverter booleanToStringConverter;
	@Before
	public void setup(){
		booleanToStringConverter = new BooleanToStringConverter();
	}

	@Test
	public void convertToString_givenBoolean_shouldConvertToString(){
		assertThat(booleanToStringConverter.convertToString(true), equalTo("y"));
		assertThat(booleanToStringConverter.convertToString(false), equalTo("n"));
	}

	@Test
	public void convertFromString_givenString_shouldConvertToBoolean(){
		assertThat(booleanToStringConverter.convertFromString("y"), equalTo(true));
		assertThat(booleanToStringConverter.convertFromString("n"), equalTo(false));
	}
}
