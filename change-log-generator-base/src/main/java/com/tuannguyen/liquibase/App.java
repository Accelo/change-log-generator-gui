package com.tuannguyen.liquibase;

import com.tuannguyen.liquibase.util.args.ArgumentOptionResult;
import com.tuannguyen.liquibase.util.args.ArgumentSpec;
import com.tuannguyen.liquibase.util.args.Command;
import com.tuannguyen.liquibase.util.args.CommandOptionType;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.handlers.CommandHandler;
import com.tuannguyen.liquibase.util.handlers.GenerateChangeHandler;
import com.tuannguyen.liquibase.util.handlers.GenerateTableHandler;

import lombok.extern.log4j.Log4j;

@Log4j
public class App
{
	private static final String RUN_COMMAND = "java -jar changelog-generator.jar";

	private static BeanFactory beanFactory = new BeanFactory();

	private static ArgumentSpec argumentSpec;

	static {
		argumentSpec = new ArgumentSpec();
		argumentSpec
				.addCommand()
				.cmd("table")
				.helpText("Generate liquibase change log for mysql tables")
				.commandHandler(GenerateTableHandler.class)
				.addOption()
				.shortcut("f")
				.alias("filename")
				.type(CommandOptionType.STRING)
				.helpText("File to read database properties (Default: liquibase.properties)")
				.build()
				.addOption()
				.shortcut("y")
				.alias("yes")
				.type(CommandOptionType.BOOLEAN)
				.helpText("Whether to override config values")
				.build()
				.build()
				.addCommand()
				.cmd("change")
				.helpText("Generate liquibase change log for new changes")
				.commandHandler(GenerateChangeHandler.class)
				.addOption()
				.shortcut("f")
				.alias("filename")
				.type(CommandOptionType.STRING)
				.helpText("File to read database properties (Default: liquibase.properties)")
				.build()
				.addOption()
				.shortcut("y")
				.alias("yes")
				.type(CommandOptionType.BOOLEAN)
				.helpText("Whether to override config values")
				.build();
	}

	public static void main(String[] args) throws Exception
	{
		ArgumentOptionResult arguments = parseArguments(args);
		if (arguments == null || arguments.getCommand() == Command.HELP) {
			log.info(argumentSpec.getHelpText(RUN_COMMAND));
			return;
		} else if (!arguments.isMatchedConfigValues()) {
			log.info(arguments.getCommand().getHelpText(RUN_COMMAND));
			return;
		}
		executeCommand(arguments);
	}

	private static void executeCommand(ArgumentOptionResult arguments) throws Exception
	{
		CommandHandler commandHandler = beanFactory.getHandler(arguments.getCommand());
		commandHandler.execute(arguments);
	}

	private static ArgumentOptionResult parseArguments(String[] args)
	{
		return beanFactory.getArgumentParser().parseArguments(args, argumentSpec);
	}
}
