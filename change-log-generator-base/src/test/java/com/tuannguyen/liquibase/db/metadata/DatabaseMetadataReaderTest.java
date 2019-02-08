package com.tuannguyen.liquibase.db.metadata;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tuannguyen.liquibase.db.ConnectionManager;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class DatabaseMetadataReaderTest
{
	private DatabaseMetadaReader databaseMetadaReader;

	@Mock
	private IndexMetadataReader indexMetadataReader;

	@Mock
	private ColumnMetadataReader columnMetadataReader;

	@Mock
	private ConnectionManager connectionManager;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		databaseMetadaReader = new DatabaseMetadaReader(indexMetadataReader, columnMetadataReader);
	}

	@Test
	public void readMetadata_givenValidSchema_shouldReturnCorrectColumnMetadata() throws SQLException
	{
		databaseMetadaReader.readMetadata(connectionManager, "test");
		verify(indexMetadataReader).getIndexMetadata(eq(connectionManager), eq("test"));
		verify(columnMetadataReader).getColumnMetadata(eq(connectionManager), eq("test"));
	}
}
