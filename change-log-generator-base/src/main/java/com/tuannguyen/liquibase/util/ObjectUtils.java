package com.tuannguyen.liquibase.util;

public class ObjectUtils
{
	public static <T> T defaultIfNull(T value, T defaultValue)
	{
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public static boolean isEmptyString(String value)
	{
		return value == null || value.trim().isEmpty();
	}

	public static String substituteIfNotNull(String value, String fallback)
	{
		return isEmptyString(value) ? "" : fallback;
	}
}
