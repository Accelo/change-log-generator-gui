package com.tuannguyen.liquibase.util.transform;

import com.tuannguyen.liquibase.config.model.ValueType;

public class ValueTypeToStringConverter implements Converter<ValueType> {
	@Override
	public String convertToString(ValueType value) {
		return value.name()
		            .toLowerCase();
	}

	@Override
	public ValueType convertFromString(String value) {
		for (ValueType valueType : ValueType.values()) {
			if (valueType.name()
			             .equalsIgnoreCase(value)) {
				return valueType;
			}
		}
		return ValueType.STRING;
	}
}
