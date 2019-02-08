package com.tuannguyen.liquibase.util.args;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ArgumentSpecTest
{
	private static ArgumentSpec argumentSpec;

	@BeforeClass
	public static void setup()
	{
		argumentSpec = new ArgumentSpec()
				.addCommand()
				.cmd("create")
				.helpText("Create an object")
				.build();
	}

	@Test
	public void getHelpText_givenValidSpec_shouldReturnCorrectString()
	{
		assertThat(argumentSpec.getHelpText("javac test.jar"), equalTo("javac test.jar (create)\n" +
				"\n" +
				"create: Create an object\n"));
	}
}
