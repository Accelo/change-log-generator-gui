package com.tuannguyen.liquibase.db;

import com.tuannguyen.liquibase.config.model.GenerateTableConfiguration;
import com.tuannguyen.liquibase.config.reader.PropertyLoader;
import org.h2.tools.RunScript;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class InitDatabase {
	private static GenerateTableConfiguration generateTableConfiguration;
	private static ConnectionManager connectionManager;
	private static PropertyLoader propertyLoader = new PropertyLoader();
	private static boolean initDatabase;

	public static void initDatabase() throws SQLException {
		if (initDatabase) {
			return;
		}
		propertyLoader.load(null);
		generateTableConfiguration = propertyLoader.getConfiguration(GenerateTableConfiguration.class);
		connectionManager = new ConnectionManager(generateTableConfiguration.getDatabaseConfiguration());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ConnectionManagerTest.class.getResourceAsStream("/data.sql")));
		RunScript.execute(connectionManager.getConnection(), bufferedReader);
		initDatabase = true;
	}

	public static GenerateTableConfiguration getGenerateTableConfiguration() {
		return generateTableConfiguration;
	}

	public static ConnectionManager getConnectionManager() {
		return connectionManager;
	}

}
