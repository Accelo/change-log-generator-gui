package com.tuannguyen.liquibase.util.transform;

import org.junit.Before;
import org.junit.Test;

import com.tuannguyen.liquibase.config.model.ModificationType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ModificationTypeToStringConverterTest
{
	private ModificationTypeToStringConverter modificationTypeToStringConverter;

	@Before
	public void setup()
	{
		modificationTypeToStringConverter = new ModificationTypeToStringConverter();
	}

	@Test
	public void convertToString_givenBoolean_shouldConvertToString()
	{
		assertThat(modificationTypeToStringConverter.convertToString(ModificationType.A), equalTo("A"));
	}

	@Test
	public void convertFromString_givenString_shouldConvertToBoolean()
	{
		assertThat(modificationTypeToStringConverter.convertFromString("a"), equalTo(ModificationType.A));
	}
}
