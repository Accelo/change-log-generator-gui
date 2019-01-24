package com.tuannguyen.liquibase.db;

import org.junit.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ConnectionManagerTest {
	private static ConnectionManager connectionManager;

	@BeforeClass
	public static void setupClass() throws SQLException {
		InitDatabase.initDatabase();
		connectionManager = new ConnectionManager(InitDatabase.getGenerateTableConfiguration().getDatabaseConfiguration());
	}

	@Before
	public void setup() throws SQLException {
		connectionManager.executeUpdate("INSERT INTO test VALUES(1)");
	}

	@Test
	public void execute_shouldReturnCorrectResult() throws SQLException {
		ResultSet resultSet = connectionManager.execute("SELECT COUNT(*) FROM test");
		resultSet.next();
		long count = resultSet.getLong(1);
		assertThat(count, equalTo(1L));
		resultSet.close();
	}

	@Test
	public void prepare_shouldReturnCorrectResult() throws SQLException {
		ResultSet resultSet = connectionManager.prepare("SELECT * FROM test WHERE a = ?", 1);
		resultSet.next();
		int a = resultSet.getInt(1);
		assertThat(a, equalTo(1));
		resultSet.close();
	}

	@Test
	public void prepareUpdate_shouldUpdateRow() throws SQLException {
		int updated = connectionManager.prepareUpdate("UPDATE test SET a = ? WHERE a = ?", 2, 1);
		assertThat(updated, equalTo(1));
		ResultSet resultSet = connectionManager.prepare("SELECT * FROM test WHERE a = ?", 2);
		resultSet.next();
		int a = resultSet.getInt(1);
		assertThat(a, equalTo(2));
		resultSet.close();
	}

	@After
	public void teardown() throws SQLException {
		connectionManager.executeUpdate("DELETE FROM test");
	}

	@AfterClass
	public static void tearDownClass() throws SQLException {
		connectionManager.close();
	}
}
