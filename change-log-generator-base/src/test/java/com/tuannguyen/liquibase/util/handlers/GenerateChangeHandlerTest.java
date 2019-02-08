package com.tuannguyen.liquibase.util.handlers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.config.reader.AppConfigurationReader;
import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.util.args.ArgumentOptionResult;
import com.tuannguyen.liquibase.util.args.Command;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.io.columns.ChangeWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenerateChangeHandlerTest
{
	@Mock
	private BeanFactory beanFactory;

	@Mock
	private AppConfigurationReader appConfigurationReader;

	@Mock
	private GenerateChangeHandler generateChangeHandler;

	@Mock
	private GenerateChangeConfiguration generateChangeConfiguration;

	@Mock
	private ChangeWriter changeWriter;

	@Mock
	private Command command;

	@Mock
	private ConnectionManager connectionManager;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		generateChangeHandler = new GenerateChangeHandler(beanFactory);
		when(beanFactory.getAppConfigurationReader()).thenReturn(appConfigurationReader);
		when(beanFactory.getChangeWriter()).thenReturn(changeWriter);
		when(appConfigurationReader.readConfiguration(GenerateChangeConfiguration.class)).thenReturn(
				generateChangeConfiguration);
	}

	@Test
	public void init_givenCorrectArguments_shouldReturnCorrectConfigurtaion() throws Exception
	{
		Map<String, Object> argumentValues = new HashMap<>();
		String fileName = "test.properties";
		argumentValues.put("filename", fileName);
		ArgumentOptionResult argumentOptionResult = new ArgumentOptionResult(argumentValues, command, true);
		GenerateChangeConfiguration configuration = generateChangeHandler.init(argumentOptionResult);
		assertThat(configuration, equalTo(generateChangeConfiguration));
		verify(appConfigurationReader).init(eq(fileName), eq(true));
	}

	@Test
	public void run_givenCorrectArguments_shouldPerformCorrectly() throws SQLException
	{
		ArgumentOptionResult argumentOptionResult = new ArgumentOptionResult(null, command, true);
		generateChangeHandler.run(argumentOptionResult, generateChangeConfiguration);
		InOrder inOrder = Mockito.inOrder(changeWriter);
		inOrder.verify(changeWriter).writeSingleTenantChange(eq(generateChangeConfiguration));
		inOrder.verify(changeWriter).writeMultitenantChange(eq(generateChangeConfiguration));
	}
}
