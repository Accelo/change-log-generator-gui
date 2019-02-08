package com.tuannguyen.liquibase.util.args;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class CommandOption
{
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Command parent;

	private String alias;

	private String shortcut;

	private String helpText;

	private CommandOptionType type;

	private boolean optional = true;

	CommandOption(Command parent)
	{
		this.parent = parent;
	}

	Object getValue(String value)
	{
		switch (type) {
			case STRING:
				return value.trim();
			case NUMBER:
				try {
					return Integer.parseInt(value.trim());
				} catch (NumberFormatException e) {
					return null;
				}
			case BOOLEAN:
				return value.trim().isEmpty();
		}
		return null;
	}

	public Command build()
	{
		return this.parent;
	}
}