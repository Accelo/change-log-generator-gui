package com.tuannguyen.liquibase.config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ValueType
{
	STRING("defaultValue", "value"),
	RAW("defaultValue", "value"),
	NUMERIC("defaultValueNumeric", "valueNumeric"),
	DATE("defaultValueDate", "valueDate"),
	BOOLEAN("defaultValueBoolean", "valueBoolean"),
	COMPUTED("defaultValueComputed", "valueComputed");

	private String liquibaseDefaultValueName;

	private String liquibaseValueName;
}
