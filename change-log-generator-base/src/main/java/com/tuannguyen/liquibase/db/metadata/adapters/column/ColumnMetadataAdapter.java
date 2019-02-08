package com.tuannguyen.liquibase.db.metadata.adapters.column;

import java.util.Arrays;
import java.util.List;

import com.tuannguyen.liquibase.db.metadata.ColumnMetadata;

public interface ColumnMetadataAdapter
{
	List<ColumnMetadata> getColumnMetadata(String table);

	default boolean isValueComputed(String type, String defaultValue)
	{
		if (type.toLowerCase().startsWith("timestamp") && Arrays
				.asList("now()", "current_timestamp", "current_timestamp()").contains(defaultValue.toLowerCase()))
		{
			return true;
		}

		return false;
	}
}
