package com.tuannguyen.liquibase.config;

import org.junit.Before;
import org.junit.Test;

import com.tuannguyen.liquibase.config.model.ChangeConfiguration;
import com.tuannguyen.liquibase.config.model.ModificationType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UniqueConstraintTest
{
	private ChangeConfiguration.UniqueConstraintPredicate uniqueConstraintPredicate;

	@Before
	public void setup()
	{
		uniqueConstraintPredicate = new ChangeConfiguration.UniqueConstraintPredicate();
	}

	@Test
	public void test_givenInvalidModificationType_shouldReturnFalse()
	{
		boolean result = uniqueConstraintPredicate
				.test(ChangeConfiguration.builder().modificationType(ModificationType.D).build());
		assertThat(result, is(false));
	}

	@Test
	public void test_givenAppendModificationType_shouldReturnTrueWhenUnique()
	{
		boolean result = uniqueConstraintPredicate
				.test(ChangeConfiguration.builder().modificationType(ModificationType.A).unique(true).build());
		assertThat(result, is(true));
		result = uniqueConstraintPredicate
				.test(ChangeConfiguration.builder().modificationType(ModificationType.A).unique(false).build());
		assertThat(result, is(false));
		result = uniqueConstraintPredicate
				.test(ChangeConfiguration.builder().modificationType(ModificationType.A).unique(null).build());
		assertThat(result, is(false));
	}

	@Test
	public void test_givenModifyModificationType_shouldReturnTrueWhenUniqueIsDefined()
	{
		boolean result = uniqueConstraintPredicate
				.test(ChangeConfiguration.builder().modificationType(ModificationType.M).unique(true).build());
		assertThat(result, is(true));
		result = uniqueConstraintPredicate
				.test(ChangeConfiguration.builder().modificationType(ModificationType.M).unique(false).build());
		assertThat(result, is(true));
		result = uniqueConstraintPredicate
				.test(ChangeConfiguration.builder().modificationType(ModificationType.M).unique(null).build());
		assertThat(result, is(false));
	}
}
