package com.tuannguyen.liquibase.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IdGenerator {
	private SimpleDateFormat simpleDateFormat;
	private Calendar currentDate = Calendar.getInstance();

	public IdGenerator(String format) {
		simpleDateFormat = new SimpleDateFormat(format);
	}

	public String getId() {
		currentDate.add(Calendar.SECOND, 10);
		return simpleDateFormat.format(currentDate.getTime());
	}
}
