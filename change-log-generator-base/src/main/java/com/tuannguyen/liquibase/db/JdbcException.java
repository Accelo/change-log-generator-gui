package com.tuannguyen.liquibase.db;

public class JdbcException extends RuntimeException {
	public JdbcException(Throwable cause) {
		super(cause);
	}

	public JdbcException(String message, Throwable cause) {
		super(message, cause);
	}

	public JdbcException(String message) {
		super(message);
	}
}
