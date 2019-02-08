package com.tuannguyen.liquibase.db.metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class TableMetadata
{
	private String name;

	private List<ColumnMetadata> columnMetadata;

	private List<IndexMetadata> indexMetadata;
}
