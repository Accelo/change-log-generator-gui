package com.tuannguyen.liquibase.db.metadata.adapters.column;

import com.tuannguyen.liquibase.db.metadata.ColumnMetadata;

import java.util.Arrays;
import java.util.List;

public interface ColumnMetadataAdapter {
	List<ColumnMetadata> getColumnMetadata(String table);

	default boolean isValueComputed(String type, String defaultValue) {
		if (type.toLowerCase().startsWith("timestamp") && Arrays.asList("now()", "current_timestamp", "current_timestamp()").contains(defaultValue.toLowerCase())) {
			return true;
		}

		return false;

	}
}
