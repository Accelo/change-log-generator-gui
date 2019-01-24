package com.tuannguyen.liquibase.config.reader;


import com.tuannguyen.liquibase.config.ConfigException;
import com.tuannguyen.liquibase.config.annotations.ConfigWrapper;
import com.tuannguyen.liquibase.config.annotations.PromptConfig;
import com.tuannguyen.liquibase.config.model.AfterPropertiesSet;
import com.tuannguyen.liquibase.util.ObjectUtils;
import com.tuannguyen.liquibase.util.transform.Converter;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Properties;

@Log4j
public class PropertyLoader {
	private Properties properties = new Properties();

	public void load(String fileName) {
		InputStream inputStream = null;
		try {
			if (fileName != null) {
				File file = new File(fileName);
				if (!file.exists()) {
					throw new FileNotFoundException(fileName + " not found in the current directory");
				}
				inputStream = Files.newInputStream(file.toPath());
			} else {
				fileName = "liquibase.properties";
				inputStream = getClass().getResourceAsStream("/" + fileName);
			}
			log.info("Reading properties from file: " + fileName);

			properties = new Properties();
			properties.load(inputStream);

		} catch (Exception e) {
			throw new ConfigException("Failed to load config values from property file", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("Failed to close input stream", e);
				}
			}
		}

	}

	public <T> T getConfiguration(Class<T> configurationClass) {
		try {
			T configuration = configurationClass.newInstance();
			for (Field field : configurationClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(PromptConfig.class)) {
					field.setAccessible(true);
					PromptConfig promptConfig = field.getAnnotation(PromptConfig.class);
					String configName = promptConfig.config();
					Class<? extends Converter> converterClass = promptConfig.converter();
					String stringValue = ObjectUtils.defaultIfNull(properties.getProperty(configName), "");
					if (!converterClass.equals(Converter.class)) {
						Converter converter = converterClass.newInstance();
						Object convertedValue = converter.convertFromString(stringValue);
						field.set(configuration, convertedValue);
					} else {
						field.set(configuration, stringValue);
					}
				} else if (field.isAnnotationPresent(ConfigWrapper.class)) {
					Object object = getConfiguration(field.getType());
					field.setAccessible(true);
					field.set(configuration, object);
				}
			}
			if (AfterPropertiesSet.class.isAssignableFrom(configurationClass)) {
				((AfterPropertiesSet) configuration).afterPropertiesSet();
			}
			return configuration;
		} catch (Exception e) {
			throw new ConfigException("Failed to get config values", e);
		}
	}
}
