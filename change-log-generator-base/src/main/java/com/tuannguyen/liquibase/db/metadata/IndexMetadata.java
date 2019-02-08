package com.tuannguyen.liquibase.db.metadata;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class IndexMetadata
{
	private List<IndexPart> columns;

	private String name;

	private boolean unique;

	private String table;

	void addPart(IndexPart indexPart)
	{
		columns.add(indexPart);
	}

	@Data
	@AllArgsConstructor
	public static class IndexPart
	{
		private String name;

		private IndexDirection direction;

		public enum IndexDirection
		{
			ASC, DESC
		}
	}
}
