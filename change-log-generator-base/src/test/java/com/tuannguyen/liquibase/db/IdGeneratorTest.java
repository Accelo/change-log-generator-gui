package com.tuannguyen.liquibase.db;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class IdGeneratorTest {
	private IdGenerator idGenerator;
	private SimpleDateFormat sdf;

	@Before
	public void setup(){
		String format = "HH:mm:ss";
		sdf = new SimpleDateFormat(format);
		idGenerator = new IdGenerator(format);
	}

	@Test
	public void getId_whenCallMultipleTimes_shouldReturnDifferentIds() throws InterruptedException {
		String id = idGenerator.getId();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 10);
		String expectedId = sdf.format(calendar.getTime());
		assertThat(id, equalTo(expectedId));

		calendar.add(Calendar.SECOND, 10);
		expectedId = sdf.format(calendar.getTime());
		id = idGenerator.getId();
		assertThat(id, equalTo(expectedId));
	}
}
