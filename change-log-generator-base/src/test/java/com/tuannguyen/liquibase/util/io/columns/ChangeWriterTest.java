package com.tuannguyen.liquibase.util.io.columns;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tuannguyen.liquibase.config.model.ChangeConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.config.model.ModificationType;
import com.tuannguyen.liquibase.config.model.ValueType;
import com.tuannguyen.liquibase.db.IdGenerator;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.io.TemplateHelper;
import com.tuannguyen.liquibase.util.io.XmlHelper;

public class ChangeWriterTest
{
	private static TemplateHelper templateHelper;

	private static XmlHelper xmlHelper;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private ChangeWriter changeWriter;

	private GenerateChangeConfiguration configuration;

	@BeforeClass
	public static void setupClass() throws SQLException
	{
		BeanFactory beanFactory = new BeanFactory();
		templateHelper = beanFactory.getTemplateHelper();
		xmlHelper = beanFactory.getXmlHelper();
	}

	@Before
	public void setup()
	{
		changeWriter = new ChangeWriter(new IdGenerator("HH:mm:ss"), templateHelper, xmlHelper);
		configuration = GenerateChangeConfiguration.builder()
				.authorName("tuan")
				.outputFileName("test")
				.jiraNumber("AFFINITY-17898")
				.schema("accelo_shared")
				.baseProjectDir(ChangeWriterTest.class.getResource("/projectDir")
						.getPath())
				.changeConfigurationList(Arrays.asList(
						ChangeConfiguration.builder()
								.table("test")
								.name("new1")
								.nullable(false)
								.uniqueConstraintName("test")
								.afterColumn("old")
								.defaultValue("a")
								.type("varchar(20)")
								.unique(true)
								.modificationType(ModificationType.A)
								.build(),
						ChangeConfiguration.builder()
								.table("test2")
								.name("new")
								.nullable(false)
								.valueType(ValueType.NUMERIC)
								.defaultValue("6")
								.type("INT(10)")
								.modificationType(ModificationType.A)
								.build(),
						ChangeConfiguration.builder()
								.table("test")
								.name("new2")
								.nullable(false)
								.afterColumn("new1")
								.defaultValue("TRUE")
								.type("BOOLEAN")
								.unique(true)
								.uniqueConstraintName("")
								.valueType(ValueType.BOOLEAN)
								.modificationType(ModificationType.M)
								.build(),
						ChangeConfiguration.builder()
								.table("test")
								.name("new2")
								.defaultValue("1997/07/23")
								.valueType(ValueType.DATE)
								.modificationType(ModificationType.M)
								.build(),
						ChangeConfiguration.builder()
								.table("bye")
								.name("delete_column")
								.modificationType(ModificationType.D)
								.build(),
						ChangeConfiguration.builder()
								.table("update_table")
								.name("update_column")
								.modificationType(ModificationType.U)
								.value("a")
								.where("b IS NOT NULL")
								.build(),
						ChangeConfiguration.builder()
								.table("update_table")
								.name("update_all_column")
								.modificationType(ModificationType.U)
								.value("b")
								.build(),
						ChangeConfiguration.builder()
								.table("update_table")
								.name("new_date")
								.type("TIMESTAMP")
								.modificationType(ModificationType.M)
								.valueType(ValueType.COMPUTED)
								.defaultValue("CURRENT_TIMESTAMP")
								.extra("ON UPDATE CURRENT_TIMESTAMP")
								.build(),
						ChangeConfiguration.builder()
								.table("unique_test")
								.name("now_unique")
								.modificationType(ModificationType.M)
								.unique(true)
								.uniqueConstraintName("unique_constraint")
								.build(),
						ChangeConfiguration.builder()
								.table("unique_test")
								.name("not_unique")
								.modificationType(ModificationType.M)
								.unique(false)
								.build(),
						ChangeConfiguration.builder()
								.table("blah")
								.modificationType(ModificationType.DR)
								.build(),
						ChangeConfiguration.builder()
								.table("job_settings")
								.name("editing_level")
								.type("enum('editable', 'not_editable')")
								.defaultValue("editable")
								.modificationType(ModificationType.M)
								.build(),
						ChangeConfiguration.builder()
								.table("job_settings")
								.name("test_date")
								.type("DATE")
								.defaultValue("NOW()")
								.valueType(ValueType.COMPUTED)
								.extra("ON UPDATE CURRENT TIMESTAMP")
								.modificationType(ModificationType.A)
								.build(),
						ChangeConfiguration.builder()
								.table("job_settings")
								.name("updated_date")
								.value("NOW()")
								.where("id = 7")
								.valueType(ValueType.COMPUTED)
								.modificationType(ModificationType.U)
								.build(),
						ChangeConfiguration.builder()
								.table("job_settings")
								.name("new_date")
								.value("12")
								.where("id = 7")
								.valueType(ValueType.STRING)
								.modificationType(ModificationType.U)
								.build(),
						ChangeConfiguration.builder()
								.table("job_settings")
								.name("new_date")
								.value("NULL")
								.where("id = 7")
								.valueType(ValueType.RAW)
								.modificationType(ModificationType.U)
								.build(),
						ChangeConfiguration.builder()
								.table("job_settings")
								.name("updated_date")
								.newColumn("new_updated_date")
								.type("DATE")
								.modificationType(ModificationType.R)
								.build(),
						ChangeConfiguration.builder()
								.table("expense")
								.name("created")
								.newColumn("new_updated_date")
								.valueType(ValueType.COMPUTED)
								.value("IF(incurred IS NULL, NULL, CONCAT(incurred, '12:00:00'))")
								.modificationType(ModificationType.U)
								.build(),
						ChangeConfiguration.builder()
								.sql("UPDATE test SET value = 7")
								.modificationType(ModificationType.S)
								.build(),
						ChangeConfiguration.builder()
								.table("index_table")
								.name("test_index")
								.value("a DESC, b ASC, c desc, d asc")
								.modificationType(ModificationType.I)
								.build(),
						ChangeConfiguration.builder()
								.table("index_table")
								.name("test_index")
								.value("a DESC, b ASC, c desc, d asc")
								.unique(true)
								.modificationType(ModificationType.I)
								.build()
				))
				.build();
		configuration.afterPropertiesSet();
	}

	@Test
	public void writeColumn_givenValidConfiguration_shouldWriteFile()
	{
		changeWriter.writeMultitenantChange(configuration);
	}

	@Test
	public void writeColumnSQL_givenValidConfiguration_shouldWriteFile()
	{
		changeWriter.writeSingleTenantChange(configuration);
	}

	@Test
	public void writePerlUpdate_givenValidDestination_shouldWriteFile() throws IOException
	{
		changeWriter.writePerlUpdate(configuration, tempFolder.newFile());
	}
}
