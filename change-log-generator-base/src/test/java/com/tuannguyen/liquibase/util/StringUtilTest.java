package com.tuannguyen.liquibase.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class StringUtilTest
{
	@Test
	public void trimRedundantWhitespaces_givenRedundantSpaces_shouldTrimCorrectly()
	{
		assertThat(StringUtils.trimRedundantWhitespaces("new     string"), equalTo("new string"));
		assertThat(StringUtils.trimRedundantWhitespaces("\nnew\t\t\tstring\n\n\n\n"), equalTo("\nnew\tstring\n"));
		assertThat(StringUtils.trimRedundantWhitespaces("\n\nnew     string\n\n\n\n"), equalTo("\nnew string\n"));
	}
}
