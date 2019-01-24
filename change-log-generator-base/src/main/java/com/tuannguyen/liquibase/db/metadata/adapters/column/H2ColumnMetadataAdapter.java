package com.tuannguyen.liquibase.db.metadata.adapters.column;

import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.db.JdbcException;
import com.tuannguyen.liquibase.db.metadata.ColumnMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class H2ColumnMetadataAdapter implements ColumnMetadataAdapter {

	private final ConnectionManager connectionManager;

	public H2ColumnMetadataAdapter(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	@Override
	public List<ColumnMetadata> getColumnMetadata(String tableName) {
		try (ResultSet rs = connectionManager.execute("SHOW COLUMNS FROM " + tableName.toUpperCase())) {
			List<ColumnMetadata> columnMetadataList = new ArrayList<>();
			while (rs.next()) {
				String name = rs.getString("Field");
				String definition = rs.getString("Type");
				boolean nullable = "yes".equals(rs.getString("Null").toLowerCase());
				String key = rs.getString("Key").toLowerCase();
				boolean unique = "uni".equals(key) || "pri".equals(key);
				boolean primaryKey = "pri".equals(key);
				String defaultValue = rs.getString("Default");
				boolean autoIncrement =  defaultValue.toLowerCase().contains("next value");
				if (primaryKey) {
					defaultValue = null;
				}
				ColumnMetadata columnMetadata =
						ColumnMetadata
								.builder()
								.name(name)
								.type(definition)
								.nullable(nullable)
								.table(tableName)
								.primaryKey(primaryKey)
								.unique(unique)
								.defaultValue(defaultValue)
								.autoIncrement(autoIncrement)
								.defaultComputed(isValueComputed(definition, defaultValue))
								.build();
				columnMetadataList.add(columnMetadata);
			}
			return columnMetadataList;
		} catch (SQLException e) {
			throw new JdbcException("Failed to read column metadata from default adapter", e);
		}
	}
}
