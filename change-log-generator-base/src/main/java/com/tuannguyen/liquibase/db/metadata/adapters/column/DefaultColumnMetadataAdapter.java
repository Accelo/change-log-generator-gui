package com.tuannguyen.liquibase.db.metadata.adapters.column;

import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.db.JdbcException;
import com.tuannguyen.liquibase.db.metadata.ColumnMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefaultColumnMetadataAdapter implements ColumnMetadataAdapter {
	private ConnectionManager connectionManager;

	public DefaultColumnMetadataAdapter(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public List<ColumnMetadata> getColumnMetadata(String tableName) {
		try (ResultSet rs = connectionManager.execute("SHOW FULL COLUMNS FROM " + tableName)) {
			List<ColumnMetadata> columnMetadataList = new ArrayList<>();
			while (rs.next()) {
				String name = rs.getString("Field");
				String definition = rs.getString("Type");
				boolean nullable = "yes".equalsIgnoreCase(rs.getString("Null"));
				String key = rs.getString("Key").toLowerCase();
				boolean unique = "uni".equals(key) || "pri".equals(key);
				boolean primaryKey = "pri".equals(key);
				String defaultValue = rs.getString("Default");
				boolean autoIncrement = "auto_increment".equals(rs.getString("Extra"));
				String comment = rs.getString("comment");
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
								.comment(comment)
								.build();
				columnMetadataList.add(columnMetadata);
			}
			return columnMetadataList;
		} catch (SQLException e) {
			throw new JdbcException("Failed to read column metadata from default adapter", e);
		}

	}
}
