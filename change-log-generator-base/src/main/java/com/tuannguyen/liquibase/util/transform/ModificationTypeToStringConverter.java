package com.tuannguyen.liquibase.util.transform;

import com.tuannguyen.liquibase.config.model.ModificationType;

public class ModificationTypeToStringConverter implements Converter<ModificationType> {
	@Override
	public String convertToString(ModificationType value) {
		return value.name();
	}

	@Override
	public ModificationType convertFromString(String value) {
		for (ModificationType modificationType: ModificationType.values()) {
			if (modificationType.name().equalsIgnoreCase(value)) {
				return modificationType;
			}
		}
		return ModificationType.A;
	}
}
