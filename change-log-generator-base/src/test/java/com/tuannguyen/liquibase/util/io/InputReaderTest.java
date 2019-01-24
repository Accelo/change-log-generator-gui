package com.tuannguyen.liquibase.util.io;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(Parameterized.class)
public class InputReaderTest {

	@Parameterized.Parameters(name = "{index}: inputReader.read({0}) = {1}")
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][] {
			{"test", "test"}, {" ", "default"}
		});
	}
	private InputReader inputReader;

	private String result;


	public InputReaderTest(String input, String result){
		InputStream inputStream = new ByteArrayInputStream(input.getBytes());
		Scanner scanner = new Scanner(inputStream);
		inputReader = new InputReader(scanner);
		this.result = result;
	}

	@Test
	public void readString_givenNonEmptyString_shouldReturnThatValue() {
		assertThat(inputReader.readString("Test", "default"), equalTo(result));
	}
}
