package com.tuannguyen.liquibase.db.metadata;

import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.db.metadata.adapters.column.ColumnMetadataAdapter;

import java.util.List;

public class ColumnMetadataReader
{
	List<ColumnMetadata> getColumnMetadata(ConnectionManager connectionManager, String tableName)
	{
		ColumnMetadataAdapter columnMetadataAdapter = connectionManager.getColumnMetadataAdapter();
		return columnMetadataAdapter.getColumnMetadata(tableName);
	}
}
