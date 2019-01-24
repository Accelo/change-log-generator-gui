package com.tuannguyen.liquibase.util.args;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(fluent = true)
public class ArgumentSpec {
	@Getter
	private List<Command> commands = new ArrayList<>();

	public Command addCommand() {
		Command command = new Command(this);
		commands.add(command);
		return command;
	}

	public String getHelpText(String usage) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(usage).append(commands.stream().map(Command::cmd).collect(Collectors.joining("|", " (", ")")));
		stringBuilder.append("\n\n");
		for (Command command : commands) {
			stringBuilder.append(String.format("%s: %s%n", command.cmd(), command.helpText()));
		}
		return stringBuilder.toString();
	}
}
