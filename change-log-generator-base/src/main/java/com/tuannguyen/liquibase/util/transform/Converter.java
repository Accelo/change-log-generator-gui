package com.tuannguyen.liquibase.util.transform;

public interface Converter<T>
{
	String convertToString(T value);

	T convertFromString(String value);
}
