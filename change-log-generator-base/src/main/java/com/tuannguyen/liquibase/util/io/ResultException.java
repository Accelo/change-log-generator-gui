package com.tuannguyen.liquibase.util.io;


public class ResultException extends RuntimeException {
	public ResultException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResultException(String message) {
		super(message);
	}
}
