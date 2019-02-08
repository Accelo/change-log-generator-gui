package com.tuannguyen.liquibase.util.args;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArgumentOptionResult
{
	private Map<String, Object> values;

	private Command command;

	private boolean matchedConfigValues;
}
