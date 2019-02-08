package com.tuannguyen.liquibase.util.io;

import java.util.Arrays;
import java.util.Scanner;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InputReaderChoiceTest
{
	private InputReader inputReader;

	@Before
	public void setup()
	{
		Scanner scanner = new Scanner("test\nn\ny");
		inputReader = new InputReader(scanner);
	}

	@Test
	public void readChoice_givenNoValue_shouldReturnCorrectValue()
	{
		String choice = inputReader.readChoice("Enter choice", Arrays.asList("n", "y"));
		assertThat(choice, equalTo("n"));
	}

	@Test
	public void readChoice_givenYesValue_shouldReturnCorrectValue()
	{
		String choice = inputReader.readChoice("Enter choice", Arrays.asList("Y", "y"));
		assertThat(choice, equalTo("y"));
	}
}
