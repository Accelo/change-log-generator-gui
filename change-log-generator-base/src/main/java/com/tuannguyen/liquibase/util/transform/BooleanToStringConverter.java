package com.tuannguyen.liquibase.util.transform;

public class BooleanToStringConverter implements Converter<Boolean>
{
	@Override
	public String convertToString(Boolean value)
	{
		return value ? "y" : "n";
	}

	@Override
	public Boolean convertFromString(String value)
	{
		String lowercaseValue = value.trim().toLowerCase();
		return "y".equals(lowercaseValue) || "yes".equals(lowercaseValue);
	}
}
