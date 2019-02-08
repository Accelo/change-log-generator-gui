package com.tuannguyen.liquibase.db.metadata;

import com.tuannguyen.liquibase.db.ConnectionManager;

import java.util.List;

public class DatabaseMetadaReader
{
	private IndexMetadataReader indexMetadataReader;

	private ColumnMetadataReader columnMetadataReader;

	public DatabaseMetadaReader(IndexMetadataReader indexMetadataReader, ColumnMetadataReader columnMetadataReader)
	{
		this.indexMetadataReader = indexMetadataReader;
		this.columnMetadataReader = columnMetadataReader;
	}

	public TableMetadata readMetadata(ConnectionManager connectionManager, String tableName)
	{
		List<ColumnMetadata> columnMetadataList = columnMetadataReader.getColumnMetadata(connectionManager, tableName);
		List<IndexMetadata> indexMetadataList = indexMetadataReader.getIndexMetadata(connectionManager, tableName);
		return new TableMetadata(tableName, columnMetadataList, indexMetadataList);
	}
}
