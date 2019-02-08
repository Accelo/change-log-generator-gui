package com.tuannguyen.liquibase.gui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertiesUtil
{
	public static Properties loadProperties(String fileName) throws IOException
	{
		try (InputStream inputStream = PropertiesUtil.class.getResourceAsStream("/" + fileName)) {
			Properties properties = new Properties();
			properties.load(inputStream);
			return properties;
		}
	}

	public static Properties saveProperties(String fileName, Properties properties)
			throws IOException, URISyntaxException
	{
		try (OutputStream outputStream = new FileOutputStream(new File(PropertiesUtil.class.getResource("/" + fileName)
				.toURI())))
		{
			properties.store(outputStream, "ChangeLogGenerator");
			return properties;
		}
	}
}
