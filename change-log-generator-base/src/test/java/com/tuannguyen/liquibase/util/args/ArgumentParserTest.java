package com.tuannguyen.liquibase.util.args;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tuannguyen.liquibase.util.handlers.GenerateChangeHandler;
import com.tuannguyen.liquibase.util.handlers.GenerateTableHandler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArgumentParserTest
{
	private static ArgumentSpec argumentSpec;

	private static ArgumentParser argumentParser;

	@BeforeClass
	public static void setup()
	{
		argumentSpec = new ArgumentSpec()
				.addCommand()
				.cmd("create")
				.helpText("Create an object")
				.commandHandler(GenerateTableHandler.class)
				.addOption()
				.shortcut("a")
				.alias("al")
				.type(CommandOptionType.STRING)
				.helpText("Property A")
				.build()
				.addOption()
				.shortcut("b")
				.alias("bl")
				.optional(false)
				.type(CommandOptionType.NUMBER)
				.helpText("Property B")
				.build()
				.addOption()
				.shortcut("c")
				.alias("cl")
				.type(CommandOptionType.BOOLEAN)
				.helpText("Property C")
				.build()
				.build()
				.addCommand()
				.cmd("edit")
				.helpText("Edit an object")
				.commandHandler(GenerateChangeHandler.class)
				.addOption()
				.shortcut("d")
				.alias("dl")
				.optional(false)
				.type(CommandOptionType.STRING)
				.helpText("Property D")
				.build()
				.build();
		argumentParser = new ArgumentParser();
	}

	@Test
	public void parseArguments_InvalidCommand_ShouldReturnNull()
	{
		ArgumentOptionResult result = argumentParser.parseArguments(new String[]{ "delete" }, argumentSpec);
		assertThat(result, equalTo(null));
	}

	@Test
	public void parseArguments_InvalidArgument_ShouldSetNonMatchedConfigValues()
	{
		ArgumentOptionResult result = argumentParser.parseArguments(new String[]{ "edit", "--bl", "b" }, argumentSpec);
		assertThat(result.getCommand(), is(equalTo(argumentSpec.commands().get(1))));
	}

	@Test
	public void parseArguments_InvalidArgumentType_ShouldSetNonMatchedConfigValues()
	{
		ArgumentOptionResult result =
				argumentParser.parseArguments(new String[]{ "create", "--bl", "d" }, argumentSpec);
		assertThat(result.isMatchedConfigValues(), is(false));
		assertThat(result.getCommand(), is(equalTo(argumentSpec.commands().get(0))));
	}

	@Test
	public void parseArguments_MissingArguments_ShouldSetNonMatchedConfigValues()
	{
		ArgumentOptionResult result =
				argumentParser.parseArguments(new String[]{ "create", "--al", "a", "--cl" }, argumentSpec);
		assertThat(result.isMatchedConfigValues(), is(false));
		assertThat(result.getCommand(), is(equalTo(argumentSpec.commands().get(0))));
	}

	@Test
	public void parseArguments_CorrectArguments_ShouldReturnValid()
	{
		ArgumentOptionResult result =
				argumentParser.parseArguments(new String[]{ "create", "--al", "a", "--bl", "1", "--cl" }, argumentSpec);
		assertThat(result.isMatchedConfigValues(), is(true));
		assertThat(result.getCommand(), equalTo(argumentSpec.commands().get(0)));
	}

	@Test
	public void parseArguments_HelpArguments_ShouldReturnValid()
	{
		ArgumentOptionResult result = argumentParser.parseArguments(new String[]{ "--help" }, argumentSpec);
		assertThat(result.isMatchedConfigValues(), is(true));
		assertThat(result.getCommand(), equalTo(Command.HELP));
	}
}
