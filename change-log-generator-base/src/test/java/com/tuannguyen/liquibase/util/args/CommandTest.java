package com.tuannguyen.liquibase.util.args;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CommandTest
{
	private static Command command;

	@BeforeClass
	public static void setup()
	{
		ArgumentSpec argumentSpec = new ArgumentSpec()
				.addCommand()
				.cmd("create")
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
				.build();
		command = argumentSpec.commands().get(0);
	}

	@Test
	public void displayResult_givenValidCommand_shouldReturnCorrectHelpText()
	{
		assertThat(command.getHelpText("java -jar changelog-generator.jar"),
				equalTo("java -jar changelog-generator.jar create [--al] --bl [--cl] \n\nal (string): Property A\nbl (number): Property B\ncl (boolean): Property C\n"));
	}
}
