package com.tuannguyen.liquibase.util.args;


import java.util.*;
import java.util.stream.Collectors;

public class ArgumentParser {
	public ArgumentOptionResult parseArguments(String[] args, ArgumentSpec argumentSpec) {
		List<Command> commands = argumentSpec.commands();
		if (args.length == 0) {
			return null;
		}
		for (Command command : commands) {
			ArgumentMatcher argumentMatcher = new ArgumentMatcher(args, command);
			ArgumentOptionResult argumentOptionResult = argumentMatcher.matches();
			if (argumentOptionResult != null) {
				return argumentOptionResult;
			}
		}
		//general help command
		if (Arrays.asList("help", "h").contains(standardise(args[0]).toLowerCase())) {
			return new ArgumentOptionResult(null, Command.HELP, true);
		}
		return null;
	}

	private static class ArgumentMatcher {
		private String[] args;
		private Command command;
		private List<String> cmdParts;

		ArgumentMatcher(String[] args, Command command) {
			this.args = args;
			this.command = command;
			this.cmdParts = Arrays.asList(command.cmd().split("\\s+"));
		}

		ArgumentOptionResult matches() {
			List<String> nonConfigArgs = new ArrayList<>();
			Map<String, Object> configArgs = new HashMap<>();
			int index = 0;
			boolean syntaxError = false;
			boolean help = false;
			while (index < args.length) {
				if (!isConfigArgument(args[index])) {
					nonConfigArgs.add(args[index]);
					if (!cmdParts.containsAll(nonConfigArgs)) {
						syntaxError = true;
						break;
					}
				} else {
					String key = standardise(args[index]);
					if (Arrays.asList("help", "h").contains(key)) {
						help = true;
					} else {
						CommandOption commandOption = command.findOption(key);
						if (commandOption == null) {
							syntaxError = true;
							break;
						}
						CommandOptionType type = commandOption.type();
						String value = "";
						if (type != CommandOptionType.BOOLEAN) {
							value = (index < args.length) ? args[++index] : "";
						}
						Object parsedValue = commandOption.getValue(value);
						if (parsedValue == null) {
							syntaxError = true;
							break;
						}
						configArgs.put(commandOption.alias(), parsedValue);
					}
				}
				index++;
			}
			boolean matchedCommand = nonConfigArgs.containsAll(cmdParts);
			if (!matchedCommand) {
				return null;
			}
			boolean needHelp = syntaxError || help;
			if (needHelp) {
				return new ArgumentOptionResult(configArgs, command, false);
			}
			List<String> requiredProperties = command.commandOptions().stream().filter(commandOption -> !commandOption.optional()).map(CommandOption::alias).collect(Collectors.toList());
			Set<String> foundKeys = configArgs.keySet();
			if (!foundKeys.containsAll(requiredProperties)) {
				return new ArgumentOptionResult(configArgs, command, false);
			}
			return new ArgumentOptionResult(configArgs, command, true);
		}

		private boolean isConfigArgument(String key) {
			return key.startsWith("-") || key.startsWith("--");
		}
	}

	private static String standardise(String key) {
		key = key.trim();
		if (key.startsWith("--")) {
			return key.substring(2);
		} else if (key.startsWith("-")) {
			return key.substring(1);
		}
		return key;
	}
}
