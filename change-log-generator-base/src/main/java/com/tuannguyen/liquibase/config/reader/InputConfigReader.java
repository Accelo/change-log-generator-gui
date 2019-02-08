package com.tuannguyen.liquibase.config.reader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.tuannguyen.liquibase.config.ConfigException;
import com.tuannguyen.liquibase.config.annotations.ConditionalList;
import com.tuannguyen.liquibase.config.annotations.ConditionalOn;
import com.tuannguyen.liquibase.config.annotations.ConfigList;
import com.tuannguyen.liquibase.config.annotations.ConfigWrapper;
import com.tuannguyen.liquibase.config.annotations.PromptConfig;
import com.tuannguyen.liquibase.config.model.AfterPropertiesSet;
import com.tuannguyen.liquibase.util.ObjectUtils;
import com.tuannguyen.liquibase.util.io.InputReader;
import com.tuannguyen.liquibase.util.transform.Converter;

import lombok.extern.log4j.Log4j;

@Log4j
public class InputConfigReader
{
	private InputReader inputReader;

	public InputConfigReader(InputReader inputReader)
	{
		this.inputReader = inputReader;
	}

	<T> T getConfiguration(Class<T> configurationClass, Object defaultPropertyValues)
	{
		try {
			T configuration = configurationClass.newInstance();
			for (Field field : configurationClass.getDeclaredFields()) {
				if (!shouldDisplayField(field, configuration)) {
					continue;
				}
				field.setAccessible(true);
				if (field.isAnnotationPresent(PromptConfig.class)) {
					PromptConfig promptConfig = field.getAnnotation(PromptConfig.class);
					Object inputValue = promptForInput(promptConfig, field.get(defaultPropertyValues));
					field.set(configuration, inputValue);
				} else if (field.isAnnotationPresent(ConfigWrapper.class)) {
					Object defaultObjectValue = field.get(defaultPropertyValues);
					Object object = getConfiguration(field.getType(), defaultObjectValue);
					field.set(configuration, object);
				} else if (field.isAnnotationPresent(ConfigList.class)) {
					List<?> inputList = getInputList(field);
					field.set(configuration, inputList);
				}
			}
			if (AfterPropertiesSet.class.isAssignableFrom(configurationClass)) {
				((AfterPropertiesSet) configuration).afterPropertiesSet();
			}
			return configuration;
		} catch (Exception e) {
			throw new ConfigException("Failed to read user input", e);
		}
	}

	private <T> boolean shouldDisplayField(Field field, T configuration) throws NoSuchFieldException,
			IllegalAccessException,
			InstantiationException
	{
		if (!field.isAnnotationPresent(ConditionalOn.class) && !field.isAnnotationPresent(ConditionalList.class)) {
			return true;
		}
		ConditionalOn[] conditionalOnList = field.getAnnotationsByType(ConditionalOn.class);
		for (ConditionalOn conditionalOn : conditionalOnList) {
			if (!Predicate.class.equals(conditionalOn.predicateClass())) {
				Class<? extends Predicate> predicateClass = conditionalOn.predicateClass();
				Predicate predicate = predicateClass.newInstance();
				if (!predicate.test(configuration)) {
					return false;
				}
			} else {
				String fieldName = conditionalOn.field();
				String[] acceptedValues = conditionalOn.value();
				Field dependentField = configuration.getClass()
						.getDeclaredField(fieldName);
				dependentField.setAccessible(true);
				Object value = dependentField.get(configuration);
				String valueString = value instanceof Enum ? ((Enum) value).name() : value.toString();
				boolean hasValue = Arrays.stream(acceptedValues)
						.anyMatch(acceptedValue -> acceptedValue.equalsIgnoreCase(valueString));
				if (!hasValue) {
					return false;
				}
			}
		}
		return true;
	}

	<T> T getRequiredConfiguration(Class<T> configurationClass, Object defaultPropertyValues)
	{
		try {
			T configuration = configurationClass.newInstance();
			for (Field field : configurationClass.getDeclaredFields()) {
				if (!shouldDisplayField(field, configuration)) {
					continue;
				}
				field.setAccessible(true);
				if (field.isAnnotationPresent(PromptConfig.class)) {
					PromptConfig promptConfig = field.getAnnotation(PromptConfig.class);
					if (!promptConfig.config()
							.isEmpty())
					{
						//field is already initialise
						field.set(configuration, field.get(defaultPropertyValues));
						continue;
					}
					Object inputValue = promptForInput(promptConfig, field.get(defaultPropertyValues));
					field.set(configuration, inputValue);
				} else if (field.isAnnotationPresent(ConfigWrapper.class)) {
					Object defaultObjectValue = field.get(defaultPropertyValues);
					Object object = getRequiredConfiguration(field.getType(), defaultObjectValue);
					field.set(configuration, object);
				} else if (field.isAnnotationPresent(ConfigList.class)) {
					List<?> inputList = getInputList(field);
					field.set(configuration, inputList);
				}
			}
			if (AfterPropertiesSet.class.isAssignableFrom(configurationClass)) {
				((AfterPropertiesSet) configuration).afterPropertiesSet();
			}
			return configuration;
		} catch (Exception e) {
			throw new ConfigException("Failed to read required user input", e);
		}
	}

	private List<Object> getInputList(Field configurationField) throws IllegalAccessException, InstantiationException,
			NoSuchFieldException
	{
		if (!(List.class.isAssignableFrom(configurationField.getType()))) {
			throw new IllegalArgumentException("Not a list");
		}
		boolean next;
		ConfigList annotation = configurationField.getAnnotation(ConfigList.class);
		Class configClass = annotation.configurationClass();
		List<Object> configList = new ArrayList<>();
		int count = 1;
		do {
			log.info(String.format("%s %d: %n", annotation.value(), count));
			Object configuration = configClass.newInstance();
			for (Field field : configClass.getDeclaredFields()) {
				if (!shouldDisplayField(field, configuration)) {
					continue;
				}
				if (field.isAnnotationPresent(PromptConfig.class)) {
					log.info("\t");
					PromptConfig promptConfig = field.getAnnotation(PromptConfig.class);
					String value = inputReader.readString(
							promptConfig.prompt(),
							"",
							promptConfig.helpText()
					);
					Object fieldValue = getObjectValue(promptConfig, value);
					field.setAccessible(true);
					field.set(configuration, fieldValue);
				}
			}
			configList.add(configuration);
			count++;
			next = ("y".equals(inputReader.readChoice("Continue", Arrays.asList("y", "n"))));
		} while (next);
		return configList;
	}

	private Object promptForInput(PromptConfig promptConfig, Object defaultValue) throws IllegalAccessException,
			InstantiationException
	{
		defaultValue = ObjectUtils.defaultIfNull(defaultValue, "");
		String defaultValueString = getStringValue(promptConfig, defaultValue);
		String configValue = inputReader.readString(
				promptConfig.prompt(),
				defaultValueString,
				promptConfig.helpText()
		);
		return getObjectValue(promptConfig, configValue);
	}

	private String getStringValue(PromptConfig promptConfig, Object object) throws IllegalAccessException,
			InstantiationException
	{
		Class<? extends Converter> converterClass = promptConfig.converter();
		if (Converter.class.equals(converterClass)) {
			return (String) object;
		} else {
			Converter converter = converterClass.newInstance();
			return converter.convertToString(object);
		}
	}

	private Object getObjectValue(PromptConfig promptConfig, String string) throws IllegalAccessException,
			InstantiationException
	{
		Class<? extends Converter> converterClass = promptConfig.converter();
		if (promptConfig.nullIfEmpty()) {
			if (string == null || string.isEmpty()) {
				return null;
			}
		}
		if (Converter.class.equals(converterClass)) {
			return string;
		} else {
			Converter converter = converterClass.newInstance();
			return converter.convertFromString(string);
		}
	}
}
