package com.tuannguyen.liquibase.util.io.tables;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tuannguyen.liquibase.db.IdGenerator;
import com.tuannguyen.liquibase.db.InitDatabase;
import com.tuannguyen.liquibase.db.metadata.ColumnMetadataReader;
import com.tuannguyen.liquibase.db.metadata.DatabaseMetadaReader;
import com.tuannguyen.liquibase.db.metadata.IndexMetadataReader;
import com.tuannguyen.liquibase.db.metadata.TableMetadata;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.io.TemplateHelper;
import com.tuannguyen.liquibase.util.io.XmlHelper;

public class TableWriterTest
{
	private static TemplateHelper templateHelper;

	private static XmlHelper xmlHelper;

	private TableWriter tableWriter;

	private TableMetadata tableMetadata;

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
		tableWriter = new TableWriter(new IdGenerator("HH:mm:ss"), templateHelper, xmlHelper);
		tableMetadata = new DatabaseMetadaReader(new IndexMetadataReader(), new ColumnMetadataReader())
				.readMetadata(InitDatabase.getConnectionManager(), "COMPLEX_TABLE");
	}

	@Test
	public void writeTable_givenValidMetadata_shouldWriteFile()
	{
		tableWriter.writeTable(InitDatabase.getGenerateTableConfiguration(), tableMetadata);
	}

	@Test
	public void writeTrigger_givenValidMetadata_shouldWriteFile()
	{
		tableWriter.writeTrigger(InitDatabase.getGenerateTableConfiguration(), tableMetadata);
	}

	@Test
	public void writeView_givenValidMetadata_shouldWriteFile()
	{
		tableWriter.writeView(InitDatabase.getGenerateTableConfiguration(), tableMetadata);
	}
}
