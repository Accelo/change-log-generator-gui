package com.tuannguyen.liquibase.util.args;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ArgumentOptionResult {
	private Map<String, Object> values;
	private Command command;
	private boolean matchedConfigValues;
}
