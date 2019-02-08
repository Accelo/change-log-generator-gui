package com.tuannguyen.liquibase.util;

public class StringUtils
{
	public static String trimRedundantWhitespaces(String str)
	{
		str = str.replaceAll("^(\\s)+", "$1");
		str = str.replaceAll("(\\s)+$", "$1");
		str = str.replaceAll("([\t ])+", "$1");
		str = str.replaceAll("(\\s){3,}", "$1$1");
		return str;
	}

	;
}
