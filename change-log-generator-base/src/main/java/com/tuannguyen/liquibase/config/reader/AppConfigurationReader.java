package com.tuannguyen.liquibase.config.reader;

import com.tuannguyen.liquibase.config.annotations.ConfigList;
import com.tuannguyen.liquibase.config.annotations.ConfigWrapper;
import com.tuannguyen.liquibase.config.annotations.PromptConfig;
import lombok.extern.log4j.Log4j;

import java.lang.reflect.Field;

@Log4j
public class AppConfigurationReader {
	private InputConfigReader inputConfigReader;
	private PropertyLoader propertyLoader;
	private Boolean promptMode;

	public AppConfigurationReader(InputConfigReader inputConfigReader, PropertyLoader propertyLoader) {
		this.inputConfigReader = inputConfigReader;
		this.propertyLoader = propertyLoader;
	}

	public void init(String fileName, Boolean promptMode) {
		this.promptMode = promptMode;
		propertyLoader.load(fileName);
	}

	public <T> T readConfiguration(Class<T> configurationClass) {
		log.info("Reading configuration values");
		T defaultPropertyValues = propertyLoader.getConfiguration(configurationClass);
		T userConfigValue;
		if (promptMode == Boolean.TRUE) {
			userConfigValue = inputConfigReader.getConfiguration(configurationClass, defaultPropertyValues);
		} else if (requireUserInput(configurationClass)) {
			userConfigValue = inputConfigReader.getRequiredConfiguration(configurationClass, defaultPropertyValues);
		} else {
			userConfigValue = defaultPropertyValues;
		}
		log.info("Config values: " + userConfigValue);
		return userConfigValue;
	}

	boolean requireUserInput(Class configurationClass) {
		for (Field field : configurationClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(ConfigList.class)) {
				return true;
			}
			if (field.isAnnotationPresent(PromptConfig.class)) {
				PromptConfig promptConfig = field.getAnnotation(PromptConfig.class);
				if (promptConfig.config().isEmpty()) {
					//no configuration to read
					return true;
				}
			}
			if (field.isAnnotationPresent(ConfigWrapper.class) && requireUserInput(field.getType())) {
				return true;
			}
		}
		return false;
	}
}
