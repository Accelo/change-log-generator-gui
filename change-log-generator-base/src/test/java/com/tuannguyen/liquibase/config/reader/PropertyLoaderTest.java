package com.tuannguyen.liquibase.config.reader;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.tuannguyen.liquibase.config.model.DatabaseConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateTableConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class PropertyLoaderTest
{
	private PropertyLoader propertyLoader;

	@Before
	public void setup() throws IOException
	{
		propertyLoader = new PropertyLoader();
		propertyLoader.load(null);
	}

	@Test
	public void readConfiguration_givenValidPropertiesFile_shouldLoadCorrectConfiguration()
			throws InstantiationException, IllegalAccessException
	{
		GenerateTableConfiguration actualConfiguration =
				propertyLoader.getConfiguration(GenerateTableConfiguration.class);
		GenerateTableConfiguration expectedConfiguration = GenerateTableConfiguration.builder()
				.authorName("tuannguyen")
				.databaseConfiguration(DatabaseConfiguration.builder()
						.connectionUrl("jdbc:h2:mem:test")
						.driverClass("org.h2.Driver")
						.username("root")
						.password("sa").build())
				.schema("test")
				.baseProjectDir("/data/affinitylive")
				.generateTables(new ArrayList<>()).build();
		assertThat(actualConfiguration, equalTo(expectedConfiguration));
	}
}
