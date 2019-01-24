package com.tuannguyen.liquibase.config.model;

public enum BooleanWrapper {
	TRUE(true, "True"), FALSE(false, "False"), NULL(null, "Default");

	private Boolean value;
	private String  title;

	BooleanWrapper(Boolean value, String title) {
		this.value = value;
		this.title = title;
	}

	public static BooleanWrapper of(Boolean value) {
		for (BooleanWrapper booleanWrapper : BooleanWrapper.values()) {
			if (booleanWrapper.value == value) {
				return booleanWrapper;
			}
		}
		return BooleanWrapper.NULL;
	}

	public Boolean getValue() {
		return value;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return title;
	}
}
