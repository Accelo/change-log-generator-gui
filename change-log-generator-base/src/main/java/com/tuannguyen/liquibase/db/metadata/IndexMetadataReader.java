package com.tuannguyen.liquibase.db.metadata;

import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.db.JdbcException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tuannguyen.liquibase.db.metadata.IndexMetadata.IndexPart.IndexDirection.ASC;
import static com.tuannguyen.liquibase.db.metadata.IndexMetadata.IndexPart.IndexDirection.DESC;

public class IndexMetadataReader
{
	List<IndexMetadata> getIndexMetadata(ConnectionManager connectionManager, String tableName)
	{
		try {
			Connection connection = connectionManager.getConnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();

			try (ResultSet rs = databaseMetaData.getIndexInfo(connection.getCatalog(), null, tableName, false, true);) {
				Map<String, IndexMetadata> indexMetadataMap = new HashMap<>();
				while (rs.next()) {
					String indexName = rs.getString(rs.findColumn("INDEX_NAME"));
					String columnName = rs.getString(rs.findColumn("COLUMN_NAME"));
					boolean unique = !rs.getBoolean(rs.findColumn("NON_UNIQUE"));
					String columnDirection = rs.getString(rs.findColumn("ASC_OR_DESC"));
					IndexMetadata indexMetadata = indexMetadataMap.get(indexName);
					IndexMetadata.IndexPart.IndexDirection direction = "A".equals(columnDirection) ? ASC : DESC;
					IndexMetadata.IndexPart indexPart = new IndexMetadata.IndexPart(columnName, direction);
					if (indexMetadata == null) {
						indexMetadata = IndexMetadata
								.builder()
								.columns(new ArrayList<>())
								.unique(unique)
								.table(tableName)
								.name(indexName)
								.build();
						indexMetadataMap.put(indexName, indexMetadata);
					}
					indexMetadata.addPart(indexPart);
				}
				return new ArrayList<>(indexMetadataMap.values());
			}
		} catch (Exception e) {
			throw new JdbcException("Failed to read index metadata", e);
		}
	}
}
