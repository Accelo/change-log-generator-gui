package com.tuannguyen.liquibase.util.handlers;

import com.tuannguyen.liquibase.config.model.DatabaseConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateTableConfiguration;
import com.tuannguyen.liquibase.config.reader.AppConfigurationReader;
import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.db.metadata.DatabaseMetadaReader;
import com.tuannguyen.liquibase.db.metadata.TableMetadata;
import com.tuannguyen.liquibase.util.args.ArgumentOptionResult;
import com.tuannguyen.liquibase.util.args.Command;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.io.tables.TableWriter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenerateTableHandlerTest {
	private GenerateTableHandler generateTableHandler;
	@Mock
	private BeanFactory beanFactory;

	@Mock
	private AppConfigurationReader appConfigurationReader;

	@Mock
	private DatabaseMetadaReader databaseMetadaReader;

	@Mock
	private GenerateTableConfiguration generateTableConfiguration;

	@Mock
	private DatabaseConfiguration databaseConfiguration;

	@Mock
	private TableMetadata tableMetadata;

	@Mock
	private TableWriter tableWriter;

	@Mock
	private Command command;

	@Mock
	private ConnectionManager connectionManager;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		generateTableHandler = new GenerateTableHandler(beanFactory);
		when(beanFactory.getAppConfigurationReader()).thenReturn(appConfigurationReader);
		when(beanFactory.getDatabaseMetadaReader()).thenReturn(databaseMetadaReader);
		when(beanFactory.getTableWriter()).thenReturn(tableWriter);
		when(appConfigurationReader.readConfiguration(GenerateTableConfiguration.class)).thenReturn(generateTableConfiguration);
		when(databaseMetadaReader.readMetadata(eq(connectionManager), anyString())).thenReturn(tableMetadata);
		when(beanFactory.getConnectionManager(anyObject())).thenReturn(connectionManager);
		when(generateTableConfiguration.getGenerateTables()).thenReturn(Collections.singletonList("user"));
		when(generateTableConfiguration.getDatabaseConfiguration()).thenReturn(databaseConfiguration);
		when(databaseConfiguration.getDriverClass()).thenReturn(GenerateTableHandlerTest.class.getName());
	}

	@Test
	public void init_givenCorrectArguments_shouldReturnCorrectConfigurtaion() {
		Map<String, Object> argumentValues = new HashMap<>();
		String fileName = "test.properties";
		argumentValues.put("filename", fileName);
		ArgumentOptionResult argumentOptionResult = new ArgumentOptionResult(argumentValues, command, true);
		GenerateTableConfiguration configuration = generateTableHandler.init(argumentOptionResult);
		assertThat(configuration, equalTo(generateTableConfiguration));
		verify(appConfigurationReader).init(eq(fileName), eq(true));
	}

	@Test
	public void run_givenCorrectArguments_shouldPerformCorrectly() throws SQLException {
		ArgumentOptionResult argumentOptionResult = new ArgumentOptionResult(null, command, true);
		generateTableHandler.run(argumentOptionResult, generateTableConfiguration);
		verify(databaseMetadaReader).readMetadata(eq(connectionManager), eq("user"));
		InOrder inOrder = Mockito.inOrder(tableWriter);
		inOrder.verify(tableWriter).writeTable(eq(generateTableConfiguration), eq(tableMetadata));
		inOrder.verify(tableWriter).writeView(eq(generateTableConfiguration), eq(tableMetadata));
		inOrder.verify(tableWriter).writeTrigger(eq(generateTableConfiguration), eq(tableMetadata));
	}
}
