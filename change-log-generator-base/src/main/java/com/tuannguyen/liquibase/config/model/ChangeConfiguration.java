package com.tuannguyen.liquibase.config.model;

import java.util.function.Predicate;

import com.tuannguyen.liquibase.config.annotations.ConditionalOn;
import com.tuannguyen.liquibase.config.annotations.PromptConfig;
import com.tuannguyen.liquibase.util.transform.BooleanToStringConverter;
import com.tuannguyen.liquibase.util.transform.ModificationTypeToStringConverter;
import com.tuannguyen.liquibase.util.transform.ValueTypeToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class ChangeConfiguration
{
	@Builder.Default
	@PromptConfig(prompt = "type of modification", helpText = "(a)dd|(d)elete|(m)odify|(u)pdate|(r)ename|(s)ql|(dr)op table", converter = ModificationTypeToStringConverter.class)
	private ModificationType modificationType = ModificationType.A;

	@ConditionalOn(field = "modificationType", value = { "A", "M", "D", "U", "R", "DR" })
	@PromptConfig(prompt = "table")
	private String table;

	@ConditionalOn(field = "modificationType", value = { "A", "M", "D", "U", "R" })
	@PromptConfig(prompt = "name")
	private String name;

	@ConditionalOn(field = "modificationType", value = { "A", "M" })
	@PromptConfig(prompt = "defaultValue")
	private String defaultValue;

	@ConditionalOn(field = "modificationType", value = { "U" })
	@PromptConfig(prompt = "value")
	private String value;

	@Builder.Default
	@ConditionalOn(field = "modificationType", value = { "A", "M", "U" })
	@PromptConfig(prompt = "value type", helpText = "numeric|string|date|computed|boolean", converter = ValueTypeToStringConverter.class)
	private ValueType valueType = ValueType.STRING;

	@ConditionalOn(field = "modificationType", value = { "A", "M" })
	@PromptConfig(prompt = "unique", helpText = "y|n|empty", converter = BooleanToStringConverter.class, nullIfEmpty = true)
	private Boolean unique;

	@ConditionalOn(field = "modificationType", value = { "A", "M" })
	@ConditionalOn(predicateClass = UniqueConstraintPredicate.class)
	@PromptConfig(prompt = "unique constraint name")
	private String uniqueConstraintName;

	@ConditionalOn(field = "modificationType", value = { "U" })
	@PromptConfig(prompt = "where")
	private String where;

	@ConditionalOn(field = "modificationType", value = { "S" })
	@PromptConfig(prompt = "sql")
	private String sql;

	@ConditionalOn(field = "modificationType", value = { "A", "M" })
	@PromptConfig(prompt = "nullable", helpText = "y|n|empty", converter = BooleanToStringConverter.class, nullIfEmpty = true)
	private Boolean nullable;

	@ConditionalOn(field = "modificationType", value = { "A", "M" })
	@PromptConfig(prompt = "type")
	private String type;

	@ConditionalOn(field = "modificationType", value = { "R" })
	@PromptConfig(prompt = "newColumn")
	private String newColumn;

	@ConditionalOn(field = "modificationType", value = { "A" })
	@PromptConfig(prompt = "afterColumn")
	private String afterColumn;

	public static class UniqueConstraintPredicate implements Predicate<ChangeConfiguration>
	{
		@Override
		public boolean test(ChangeConfiguration changeConfiguration)
		{
			ModificationType modificationType = changeConfiguration.modificationType;
			Boolean unique = changeConfiguration.unique;
			if (modificationType == ModificationType.A) {
				return unique == Boolean.TRUE;
			} else if (modificationType == ModificationType.M) {
				return unique != null;
			}
			return false;
		}
	}
}
