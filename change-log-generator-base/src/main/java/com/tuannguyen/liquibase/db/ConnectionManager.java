package com.tuannguyen.liquibase.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.tuannguyen.liquibase.config.model.DatabaseConfiguration;
import com.tuannguyen.liquibase.db.metadata.adapters.column.ColumnMetadataAdapter;
import com.tuannguyen.liquibase.db.metadata.adapters.column.DefaultColumnMetadataAdapter;
import com.tuannguyen.liquibase.db.metadata.adapters.column.H2ColumnMetadataAdapter;

public class ConnectionManager
{
	private Connection connection;

	private DatabaseConfiguration databaseConfiguration;

	private Provider provider;

	public ConnectionManager(DatabaseConfiguration databaseConfiguration)
	{
		init(databaseConfiguration);
	}

	private void init(DatabaseConfiguration databaseConfiguration)
	{
		try {
			this.databaseConfiguration = databaseConfiguration;
			Class.forName(this.databaseConfiguration.getDriverClass());
			connection = DriverManager.getConnection(this.databaseConfiguration.getConnectionUrl(),
					this.databaseConfiguration.getUsername(), this.databaseConfiguration.getPassword());
			for (Provider supportedProvider : Provider.values()) {
				if (this.databaseConfiguration.getDriverClass().contains(supportedProvider.toString().toLowerCase())) {
					this.provider = supportedProvider;
				}
			}
		} catch (Exception e) {
			throw new JdbcException("Unable to initialise database connection", e);
		}
	}

	public ResultSet execute(String sqlStatement) throws SQLException
	{
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sqlStatement);
		statement.closeOnCompletion();
		return resultSet;
	}

	public int executeUpdate(String sqlStatement) throws SQLException
	{
		try (Statement statement = connection.createStatement()) {
			return statement.executeUpdate(sqlStatement);
		}
	}

	public ResultSet prepare(String sqlStatement, Object... parameters) throws SQLException
	{
		PreparedStatement statement = connection.prepareStatement(sqlStatement);
		int cnt = 1;
		for (Object parameter : parameters) {
			statement.setObject(cnt++, parameter);
		}
		ResultSet resultSet = statement.executeQuery();
		statement.closeOnCompletion();
		return resultSet;
	}

	public int prepareUpdate(String sqlStatement, Object... parameters) throws SQLException
	{
		try (PreparedStatement statement = connection.prepareStatement(sqlStatement)) {
			int cnt = 1;
			for (Object parameter : parameters) {
				statement.setObject(cnt++, parameter);
			}
			return statement.executeUpdate();
		}
	}

	public ColumnMetadataAdapter getColumnMetadataAdapter()
	{
		switch (provider) {
			case H2:
				return new H2ColumnMetadataAdapter(this);
			default:
				return new DefaultColumnMetadataAdapter(this);
		}
	}

	public void close() throws SQLException
	{
		connection.close();
	}

	public Connection getConnection()
	{
		return connection;
	}

	public enum Provider
	{
		MYSQL, MARIADB, H2,
	}
}
