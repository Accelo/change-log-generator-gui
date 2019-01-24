package com.tuannguyen.liquibase.config.reader;

import com.tuannguyen.liquibase.config.model.DatabaseConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateTableConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AppConfigurationReaderTest {
	@Mock
	private InputConfigReader inputConfigReader;

	@Mock
	private PropertyLoader propertyLoader;

	private AppConfigurationReader appConfigurationReader;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		appConfigurationReader = new AppConfigurationReader(inputConfigReader, propertyLoader);
	}

	@Test
	public void getConfiguration_givenPromptMode_shouldUseInputConfigReader() throws InstantiationException, IllegalAccessException, IOException {
		DatabaseConfiguration config1 = Mockito.mock(DatabaseConfiguration.class);
		DatabaseConfiguration config2 = Mockito.mock(DatabaseConfiguration.class);
		when(propertyLoader.getConfiguration(DatabaseConfiguration.class)).thenReturn(config1);
		when(inputConfigReader.getConfiguration(DatabaseConfiguration.class, config1)).thenReturn(config2);
		appConfigurationReader.init("test.properties", true);
		assertThat(appConfigurationReader.readConfiguration(DatabaseConfiguration.class), equalTo(config2));
	}

	@Test
	public void getConfiguration_givenNoPromptMode_shouldUsePropertyLoader() throws InstantiationException, IllegalAccessException, IOException {
		DatabaseConfiguration config = Mockito.mock(DatabaseConfiguration.class);
		when(propertyLoader.getConfiguration(DatabaseConfiguration.class)).thenReturn(config);
		appConfigurationReader.init("test.properties", false);
		assertThat(appConfigurationReader.readConfiguration(DatabaseConfiguration.class), equalTo(config));
		verifyNoMoreInteractions(inputConfigReader);
	}

	@Test
	public void getConfiguration_givenNoPromptModeButRequireConfiguration_shouldUsePropertyLoader() throws InstantiationException, IllegalAccessException, IOException {
		GenerateChangeConfiguration config  = Mockito.mock(GenerateChangeConfiguration.class);
		GenerateChangeConfiguration config2 = Mockito.mock(GenerateChangeConfiguration.class);
		when(propertyLoader.getConfiguration(GenerateChangeConfiguration.class)).thenReturn(config);
		when(inputConfigReader.getRequiredConfiguration(GenerateChangeConfiguration.class, config)).thenReturn(config2);
		appConfigurationReader.init("test.properties", false);
		assertThat(appConfigurationReader.readConfiguration(GenerateChangeConfiguration.class), equalTo(config2));
	}

	@Test
	public void requireUserInput_givenPromptConfigWithNoConfigKey_shouldReturnTrue(){
		assertThat(appConfigurationReader.requireUserInput(GenerateTableConfiguration.class), is(true));
	}

	@Test
	public void requireUserInput_givenConfigList_shouldReturnTrue(){
		assertThat(appConfigurationReader.requireUserInput(GenerateChangeConfiguration.class), is(true));
	}

	@Test
	public void requireUserInput_givenNormalPromptConfig_shouldReturnFalse(){
		assertThat(appConfigurationReader.requireUserInput(DatabaseConfiguration.class), is(false));
	}
}
