package com.tuannguyen.liquibase.util.io.columns;

import com.tuannguyen.liquibase.config.model.ChangeConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.config.model.ModificationType;
import com.tuannguyen.liquibase.util.io.TemplateHelper;
import com.tuannguyen.liquibase.util.io.XmlHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ViewHandlerTest {
	private static GenerateChangeConfiguration configuration;
	private static TemplateHelper              templateHelper;
	private static XmlHelper                   xmlHelper;
	private        File                        prospectFile;
	private        String                      originalContent;

	@BeforeClass
	public static void setupClass() {
		configuration = GenerateChangeConfiguration.builder()
		                                           .authorName("tuan")
		                                           .outputFileName("test")
		                                           .jiraNumber("AFFINITY-17898")
		                                           .schema("accelo_shared")
		                                           .changeConfigurationList(Arrays.asList(
				                                           ChangeConfiguration.builder().table("prospect")
				                                                              .name("first_col")
				                                                              .nullable(false)
				                                                              .uniqueConstraintName("test")
				                                                              .afterColumn("modified")
				                                                              .defaultValue("'a'")
				                                                              .type("varchar(20)")
				                                                              .unique(true)
				                                                              .modificationType(ModificationType.A)
				                                                              .build(),
				                                           ChangeConfiguration.builder().table("prospect")
				                                                              .name("second_col")
				                                                              .nullable(false)
				                                                              .afterColumn("doesnt_exists")
				                                                              .defaultValue("'a'")
				                                                              .type("enum('a', 'b')")
				                                                              .unique(true)
				                                                              .uniqueConstraintName("")
				                                                              .modificationType(ModificationType.A)
				                                                              .build(),
				                                           ChangeConfiguration.builder().table("prospect")
				                                                              .name("third_col")
				                                                              .nullable(false)
				                                                              .defaultValue("6")
				                                                              .type("INT(10)")
				                                                              .modificationType(ModificationType.A)
				                                                              .build(),
				                                           ChangeConfiguration.builder().table("prospect")
				                                                              .name("modified")
				                                                              .modificationType(ModificationType.D)
				                                                              .build(),
				                                           ChangeConfiguration.builder().table("prospect")
				                                                              .name("last_interaction")
				                                                              .modificationType(ModificationType.D)
				                                                              .build()
				)).build();
		xmlHelper = new XmlHelper();
		templateHelper = new TemplateHelper();
	}

	@Before
	public void setup() throws URISyntaxException, FileNotFoundException {
		URL url = this.getClass().getResource("/prospect.xml");
		prospectFile = new File(url.toURI());
		originalContent = readFileAsString(prospectFile);
	}

	@Test
	public void process_givenNoAfterColumn_shouldWriteToTheEnd() throws IOException {
		ViewHandler viewHandler = new ViewHandler(configuration, configuration.getChangeConfigurationList().get(2), templateHelper, xmlHelper, prospectFile);
		viewHandler.process();
		List<String> allLines = readLines(prospectFile);
		assertThat(allLines.get(32), equalTo("  `accelo_shared`.`prospect`.`third_col` AS `third_col`"));
		assertThat(allLines.get(31), equalTo("  `accelo_shared`.`prospect`.`last_interaction` AS `last_interaction`,"));
	}

	@Test
	public void process_givenInvalidAfterColumn_shouldWriteToTheEnd() throws IOException {
		ViewHandler viewHandler = new ViewHandler(configuration, configuration.getChangeConfigurationList().get(1), templateHelper, xmlHelper, prospectFile);
		viewHandler.process();
		List<String> allLines = readLines(prospectFile);
		assertThat(allLines.get(32), equalTo("  `accelo_shared`.`prospect`.`second_col` AS `second_col`"));
		assertThat(allLines.get(31), equalTo("  `accelo_shared`.`prospect`.`last_interaction` AS `last_interaction`,"));
	}

	@Test
	public void process_givenValidAfterColumn_shouldWriteToCorrectPosition() throws IOException {
		ViewHandler viewHandler = new ViewHandler(configuration, configuration.getChangeConfigurationList().get(0), templateHelper, xmlHelper, prospectFile);
		viewHandler.process();
		List<String> allLines = readLines(prospectFile);
		assertThat(allLines.get(13), equalTo("  `accelo_shared`.`prospect`.`first_col` AS `first_col`,"));
	}

	@Test
	public void process_givenColumnToDeleteAtTheMiddle_shouldDeleteColumn() throws IOException {
		ViewHandler viewHandler = new ViewHandler(configuration, configuration.getChangeConfigurationList().get(3), templateHelper, xmlHelper, prospectFile);
		viewHandler.process();
		List<String> allLines = readLines(prospectFile);
		assertThat(allLines.get(12), equalTo("  `accelo_shared`.`prospect`.`actioned` AS `actioned`,"));
	}

	@Test
	public void process_givenColumnToDeleteAtTheEnd_shouldDeleteColumn() throws IOException {
		ViewHandler viewHandler = new ViewHandler(configuration, configuration.getChangeConfigurationList().get(4), templateHelper, xmlHelper, prospectFile);
		viewHandler.process();
		List<String> allLines = readLines(prospectFile);
		assertThat(allLines.get(31), equalTo("from `accelo_shared`.`prospect`"));
	}


	private String readFileAsString(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		String content = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return content;
	}

	private List<String> readLines(File file) throws IOException {
		return Files.readAllLines(file.toPath());
	}

	@After
	public void teardown() throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(prospectFile)) {
			out.print(originalContent);
		}
	}
}
