package com.tuannguyen.liquibase.util.args;

import java.util.ArrayList;
import java.util.List;

import com.tuannguyen.liquibase.util.handlers.CommandHandler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class Command
{
	public static Command HELP =
			new Command(null)
					.cmd("")
					.addOption()
					.alias("help")
					.shortcut("h").build();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private ArgumentSpec parent;

	@Setter(AccessLevel.NONE)
	private List<CommandOption> commandOptions = new ArrayList<>();

	private String helpText;

	private String cmd;

	private Class<? extends CommandHandler> commandHandler;

	public Command(ArgumentSpec parent)
	{
		this.parent = parent;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Command command = (Command) o;

		return (commandOptions != null ? commandOptions.equals(command.commandOptions) : command.commandOptions == null)
				&& (cmd != null ? cmd.equals(command.cmd) : command.cmd == null);
	}

	@Override
	public int hashCode()
	{
		int result = commandOptions != null ? commandOptions.hashCode() : 0;
		result = 31 * result + (cmd != null ? cmd.hashCode() : 0);
		return result;
	}

	public CommandOption addOption()
	{
		CommandOption commandOption = new CommandOption(this);
		commandOptions.add(commandOption);
		return commandOption;
	}

	public CommandOption findOption(String name)
	{
		return commandOptions
				.stream()
				.filter(commandOption -> name.equalsIgnoreCase(commandOption.alias()) || name
						.equalsIgnoreCase(commandOption.shortcut()))
				.findFirst().orElse(null);
	}

	public ArgumentSpec build()
	{
		return this.parent;
	}

	public String getHelpText(String usage)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(usage).append(" ").append(cmd).append(" ");
		for (CommandOption commandOption : commandOptions) {
			if (commandOption.optional()) {
				stringBuilder
						.append("[")
						.append("--")
						.append(commandOption.alias())
						.append("]");
			} else {
				stringBuilder
						.append("--")
						.append(commandOption.alias());
			}
			stringBuilder.append(" ");
		}
		stringBuilder.append("\n\n");
		for (CommandOption commandOption : commandOptions) {
			stringBuilder
					.append(commandOption.alias())
					.append(" (")
					.append(commandOption.type().toString().toLowerCase())
					.append("): ")
					.append(commandOption.helpText())
					.append("\n");
		}
		return stringBuilder.toString();
	}
}
