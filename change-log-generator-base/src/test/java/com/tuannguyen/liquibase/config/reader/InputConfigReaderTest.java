package com.tuannguyen.liquibase.config.reader;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tuannguyen.liquibase.config.model.DatabaseConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateTableConfiguration;
import com.tuannguyen.liquibase.util.io.InputReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class InputConfigReaderTest
{
	private InputConfigReader inputConfigReader;

	@Mock
	private InputReader inputReader;

	private GenerateTableConfiguration tableConfiguration;

	private GenerateChangeConfiguration columnConfiguration;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		inputConfigReader = new InputConfigReader(inputReader);
		tableConfiguration = GenerateTableConfiguration.builder()
				.authorName("tuannguyen")
				.schema("test")
				.databaseConfiguration(DatabaseConfiguration.builder()
						.connectionUrl("jdbc:mysql://localhost/test_db")
						.driverClass("com.mysql.jdbc.Driver")
						.username("root")
						.password("password")
						.build())
				.baseProjectDir("/data/affinitylive")
				.generateTables(Arrays.asList("user", "project")).build();

		columnConfiguration = GenerateChangeConfiguration.builder()
				.authorName("tuannguyen")
				.schema("test")
				.baseProjectDir("/data/affinitylive")
				.build();
		when(inputReader.readString(anyString(), anyString(), anyString()))
				.thenAnswer(answer -> ((String) answer.getArguments()[1]).toUpperCase());
	}

	@Test
	public void getConfiguration_givenValidDefaultProperties_shouldReturnCorrectConfiguration()
			throws InstantiationException, IllegalAccessException
	{
		GenerateTableConfiguration actualConfiguration =
				inputConfigReader.getConfiguration(GenerateTableConfiguration.class, tableConfiguration);
		GenerateTableConfiguration expectedConfiguration = GenerateTableConfiguration.builder()
				.authorName("TUANNGUYEN")
				.databaseConfiguration(DatabaseConfiguration.builder()
						.connectionUrl("JDBC:MYSQL://LOCALHOST/TEST_DB")
						.driverClass("COM.MYSQL.JDBC.DRIVER")
						.username("ROOT")
						.password("PASSWORD").build())
				.schema("TEST")
				.generateTables(Arrays.asList("USER", "PROJECT"))
				.baseProjectDir("/DATA/AFFINITYLIVE").build();
		assertThat(actualConfiguration, equalTo(expectedConfiguration));
	}

	@Test
	public void getRequiredConfiguration_givenValidDefaultProperties_shouldReturnCorrectConfiguration()
			throws InstantiationException, IllegalAccessException
	{
		GenerateTableConfiguration actualConfiguration =
				inputConfigReader.getRequiredConfiguration(GenerateTableConfiguration.class, tableConfiguration);
		GenerateTableConfiguration expectedConfiguration = GenerateTableConfiguration.builder()
				.authorName("tuannguyen")
				.databaseConfiguration(DatabaseConfiguration.builder()
						.connectionUrl("jdbc:mysql://localhost/test_db")
						.driverClass("com.mysql.jdbc.Driver")
						.username("root")
						.password("password").build())
				.schema("test")
				.generateTables(Arrays.asList("USER", "PROJECT"))
				.baseProjectDir("/data/affinitylive").build();
		assertThat(actualConfiguration, equalTo(expectedConfiguration));
	}

	@Test
	public void afterPropertySet_givenInferfaceImplemented_shouldCall()
	{
		GenerateTableConfiguration actualConfiguration =
				inputConfigReader.getConfiguration(GenerateTableConfiguration.class, tableConfiguration);
		assertThat(actualConfiguration.getTableDir(), equalTo(new File("/DATA/AFFINITYLIVE/affinity/sql/tables")));
		actualConfiguration =
				inputConfigReader.getRequiredConfiguration(GenerateTableConfiguration.class, tableConfiguration);
		assertThat(actualConfiguration.getTableDir(), equalTo(new File("/data/affinitylive/affinity/sql/tables")));
	}

	@Test
	public void readRequiredConfiguration_givenAConfigurationWithList_shouldPopulateList()
	{
		int count = 0;
		when(inputReader.readChoice(anyString(), anyListOf(String.class))).thenReturn("n");
		when(inputReader.readString(anyString(), anyString(), anyString())).thenReturn(String.valueOf(count));
		GenerateChangeConfiguration actualConfiguration =
				inputConfigReader.getRequiredConfiguration(GenerateChangeConfiguration.class, columnConfiguration);
		assertThat(actualConfiguration.getChangeConfigurationList(), hasSize(1));
	}

	@Test
	public void readConfiguration_givenAConfigurationWithList_shouldPopulateList()
	{
		int count = 0;
		when(inputReader.readChoice(anyString(), anyListOf(String.class))).thenReturn("n");
		when(inputReader.readString(anyString(), anyString(), anyString())).thenReturn(String.valueOf(count));
		GenerateChangeConfiguration actualConfiguration =
				inputConfigReader.getConfiguration(GenerateChangeConfiguration.class, columnConfiguration);
		assertThat(actualConfiguration.getChangeConfigurationList(), hasSize(1));
	}
}
