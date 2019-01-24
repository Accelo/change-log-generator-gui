package com.tuannguyen.liquibase.db.metadata;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class ColumnMetadata {
	private String name;
	private String defaultValue;
	private boolean nullable;
	private boolean unique;
	private boolean primaryKey;
	private String table;
	private boolean autoIncrement;
	private String type;
	private String comment;
	private boolean defaultComputed;
}
