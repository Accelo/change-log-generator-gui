package com.tuannguyen.liquibase.db.metadata;

import com.tuannguyen.liquibase.db.InitDatabase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class IndexMetadataReaderTest {
	private IndexMetadataReader indexMetadataReader;

	@BeforeClass
	public static void setupClass() throws SQLException {
		InitDatabase.initDatabase();
	}

	@Before
	public void setup() {
		indexMetadataReader = new IndexMetadataReader();
	}

	@Test
	public void getIndexMetadata_givenValidTable_shouldReturnCorrectMetadata() {
		List<IndexMetadata> actualIndexMetadata = indexMetadataReader.getIndexMetadata(InitDatabase.getConnectionManager(), "COMPLEX_TABLE");
		List<IndexMetadata> expectedIndexMetadata = new ArrayList<>();
		IndexMetadata indexMetadata = IndexMetadata.builder().name("PRIMARY_KEY_C").table("COMPLEX_TABLE").columns(Collections.singletonList(new IndexMetadata.IndexPart("A", IndexMetadata.IndexPart.IndexDirection.ASC))).unique(true).build();
		expectedIndexMetadata.add(indexMetadata);
		indexMetadata = IndexMetadata.builder().name("IDX").table("COMPLEX_TABLE").columns(Arrays.asList(new IndexMetadata.IndexPart("B", IndexMetadata.IndexPart.IndexDirection.DESC), new IndexMetadata.IndexPart("C", IndexMetadata.IndexPart.IndexDirection.ASC))).unique(false).build();
		expectedIndexMetadata.add(indexMetadata);
		indexMetadata = IndexMetadata.builder().name("UNIQ_B").table("COMPLEX_TABLE").columns(Collections.singletonList(new IndexMetadata.IndexPart("B", IndexMetadata.IndexPart.IndexDirection.ASC))).unique(true).build();
		expectedIndexMetadata.add(indexMetadata);
		assertThat(actualIndexMetadata, equalTo(expectedIndexMetadata));
	}
}
