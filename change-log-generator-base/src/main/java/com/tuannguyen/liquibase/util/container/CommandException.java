package com.tuannguyen.liquibase.util.container;

public class CommandException extends RuntimeException
{
	public CommandException(String message, Throwable e)
	{
		super(message, e);
	}
}
