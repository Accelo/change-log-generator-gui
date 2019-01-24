package com.tuannguyen.liquibase.db.metadata.adapters;

import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.db.InitDatabase;
import com.tuannguyen.liquibase.db.metadata.ColumnMetadata;
import com.tuannguyen.liquibase.db.metadata.adapters.column.ColumnMetadataAdapter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ColumnMetadataAdapterTest {
	private ColumnMetadataAdapter adapter;

	@BeforeClass
	public static void setupClass() throws SQLException {
		InitDatabase.initDatabase();
	}

	@Before
	public void setup() {
		ConnectionManager connectionManager = InitDatabase.getConnectionManager();
		adapter = connectionManager.getColumnMetadataAdapter();
	}

	@Test
	public void getColumnMetadata_givenValidTable_shouldReturnCorrectMetadata() {
		List<ColumnMetadata> actualMetadata = adapter.getColumnMetadata("complex_table");
		List<ColumnMetadata> expectedMetadata = new ArrayList<>();
		ColumnMetadata columnMetadata = ColumnMetadata
				.builder()
				.type("VARCHAR(10)")
				.autoIncrement(true)
				.primaryKey(true)
				.nullable(false)
				.unique(true)
				.name("A")
				.table("complex_table")
				.build();
		expectedMetadata.add(columnMetadata);
		columnMetadata = ColumnMetadata
				.builder()
				.type("INTEGER(10)")
				.nullable(false)
				.defaultValue("5")
				.name("B")
				.unique(true)
				.table("complex_table")
				.build();
		expectedMetadata.add(columnMetadata);
		columnMetadata = ColumnMetadata
				.builder()
				.type("BOOLEAN(1)")
				.defaultValue("TRUE")
				.name("C")
				.nullable(true)
				.table("complex_table")
				.build();
		expectedMetadata.add(columnMetadata);

		columnMetadata = ColumnMetadata
				.builder()
				.type("TIMESTAMP(23)")
				.defaultValue("CURRENT_TIMESTAMP()")
				.defaultComputed(true)
				.name("D")
				.nullable(false)
				.table("complex_table")
				.build();
		expectedMetadata.add(columnMetadata);

		columnMetadata = ColumnMetadata
				.builder()
				.type("TIMESTAMP(23)")
				.defaultValue("NOW()")
				.defaultComputed(true)
				.name("E")
				.nullable(false)
				.table("complex_table")
				.build();
		expectedMetadata.add(columnMetadata);
		assertThat(actualMetadata, equalTo(expectedMetadata));
	}
}
