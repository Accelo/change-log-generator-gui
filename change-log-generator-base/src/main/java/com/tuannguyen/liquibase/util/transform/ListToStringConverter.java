package com.tuannguyen.liquibase.util.transform;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListToStringConverter implements Converter<List<String>>
{
	@Override
	public String convertToString(List<String> value)
	{
		return value.stream().collect(Collectors.joining(","));
	}

	@Override
	public List<String> convertFromString(String value)
	{
		return Arrays.stream(value
				.split(","))
				.map(String::trim)
				.filter(str -> !str.isEmpty())
				.collect(Collectors.toList());
	}
}
