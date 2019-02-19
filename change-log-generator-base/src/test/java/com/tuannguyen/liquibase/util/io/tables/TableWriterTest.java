package com.tuannguyen.liquibase.util.io.tables;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.tuannguyen.liquibase.config.model.GenerateTableConfiguration;
import com.tuannguyen.liquibase.db.IdGenerator;
import com.tuannguyen.liquibase.db.InitDatabase;
import com.tuannguyen.liquibase.db.metadata.ColumnMetadataReader;
import com.tuannguyen.liquibase.db.metadata.DatabaseMetadaReader;
import com.tuannguyen.liquibase.db.metadata.IndexMetadataReader;
import com.tuannguyen.liquibase.db.metadata.TableMetadata;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.helper.FileManager;
import com.tuannguyen.liquibase.util.io.TemplateHelper;
import com.tuannguyen.liquibase.util.io.XmlHelper;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IdGenerator.class })
public class TableWriterTest
{
	private static TemplateHelper templateHelper;

	private static XmlHelper xmlHelper;

	private TableWriter tableWriter;

	private TableMetadata tableMetadata;

	private GenerateTableConfiguration generateTableConfiguration;

	private FileManager fileManager;

	@BeforeClass
	public static void setupClass() throws SQLException
	{
		InitDatabase.initDatabase();
		BeanFactory beanFactory = new BeanFactory();
		templateHelper = beanFactory.getTemplateHelper();
		xmlHelper = beanFactory.getXmlHelper();
	}

	@Before
	public void setup()
	{
		Calendar today = Calendar.getInstance();
		today.set(2019, Calendar.FEBRUARY, 18, 10, 0,0);
		PowerMockito.mockStatic(Calendar.class);
		Mockito.when(Calendar.getInstance()).thenReturn(today);
		tableWriter = new TableWriter(new IdGenerator("HH:mm:ss"), templateHelper, xmlHelper);
		tableMetadata = new DatabaseMetadaReader(new IndexMetadataReader(), new ColumnMetadataReader())
				.readMetadata(InitDatabase.getConnectionManager(), "COMPLEX_TABLE");
		generateTableConfiguration = InitDatabase.getGenerateTableConfiguration();
		generateTableConfiguration.setBaseProjectDir(getClass().getResource("/projectDir").getFile());
		generateTableConfiguration.afterPropertiesSet();
		fileManager = new FileManager();
	}

	@After
	public void teardown()
	{
		fileManager.clean();
	}

	@Test
	public void writeTable_givenValidMetadata_shouldWriteFile() throws URISyntaxException
	{
		File tablesFile = generateTableConfiguration.getTableFile();
		fileManager.addNewFile(tablesFile);
		tableWriter.writeTable(generateTableConfiguration, tableMetadata);
		File expectedTablesFile = new File(getClass().getResource("/expected/tables.xml").toURI());
		assertEquals(fileManager.getFileContent(expectedTablesFile).orElse(""),
				fileManager.getFileContent(tablesFile).orElse(""));

		File expectedTableFile = new File(getClass().getResource("/expected/table_complex_table.xml").toURI());
		File newTableFile = new File(generateTableConfiguration.getTableDir(), "complex_table.xml");
		assertEquals(fileManager.getFileContent(expectedTableFile).orElse(""),
				fileManager.getFileContent(newTableFile).orElse(""));
	}

	@Test
	public void writeTrigger_givenValidMetadata_shouldWriteFile() throws URISyntaxException
	{
		File triggersFile = generateTableConfiguration.getTriggerFile();
		fileManager.addNewFile(triggersFile);
		tableWriter.writeTrigger(generateTableConfiguration, tableMetadata);

		File expectedTriggersFile = new File(getClass().getResource("/expected/triggers.xml").toURI());
		assertEquals(fileManager.getFileContent(expectedTriggersFile).orElse(""),
				fileManager.getFileContent(triggersFile).orElse(""));

		File expectedTriggerFile = new File(getClass().getResource("/expected/trigger_complex_table.xml").toURI());
		File newTriggerFile = new File(generateTableConfiguration.getTriggersDir(), "complex_table_before_insert.xml");
		assertEquals(fileManager.getFileContent(expectedTriggerFile).orElse(""),
				fileManager.getFileContent(newTriggerFile).orElse(""));
	}

	@Test
	public void writeView_givenValidMetadata_shouldWriteFile() throws URISyntaxException
	{
		File viewsFile = generateTableConfiguration.getViewFile();
		fileManager.addNewFile(viewsFile);
		tableWriter.writeView(generateTableConfiguration, tableMetadata);

		File expectedViewsFile = new File(getClass().getResource("/expected/views.xml").toURI());
		assertEquals(fileManager.getFileContent(expectedViewsFile).orElse(""),
				fileManager.getFileContent(viewsFile).orElse(""));

		File expectedViewFile = new File(getClass().getResource("/expected/view_complex_table.xml").toURI());
		File newViewFile = new File(generateTableConfiguration.getViewsDir(), "complex_table.xml");
		assertEquals(fileManager.getFileContent(expectedViewFile).orElse(""),
				fileManager.getFileContent(newViewFile).orElse(""));
	}
}
