package com.tuannguyen.liquibase.util.transform;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ListToStringConverterTest {
	private ListToStringConverter listToStringConverter;
	@Before
	public void setup(){
		listToStringConverter = new ListToStringConverter();
	}

	@Test
	public void convertToString_givenList_shouldConvertToString(){
		assertThat(listToStringConverter.convertToString(Arrays.asList("a", "b")), equalTo("a,b"));
	}

	@Test
	public void convertFromString_givenString_shouldConvertToList(){
		assertThat(listToStringConverter.convertFromString("a,b"), equalTo(Arrays.asList("a", "b")));
	}

	@Test
	public void convertFromString_givenEmptyString_shouldConvertToEmptyList(){
		assertThat(listToStringConverter.convertFromString(" "), equalTo(new ArrayList()));
	}
}
