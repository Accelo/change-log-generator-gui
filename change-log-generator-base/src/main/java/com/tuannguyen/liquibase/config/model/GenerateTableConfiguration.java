package com.tuannguyen.liquibase.config.model;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import com.tuannguyen.liquibase.config.annotations.ConfigWrapper;
import com.tuannguyen.liquibase.config.annotations.PromptConfig;
import com.tuannguyen.liquibase.util.ObjectUtils;
import com.tuannguyen.liquibase.util.transform.ListToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(exclude = { "triggersDir", "viewsDir", "tableDir", "viewFile", "tableFile", "triggerFile" })
public class GenerateTableConfiguration implements AfterPropertiesSet
{
	public static final String AFFINITY = "affinity";

	public static final String SQL = "sql";

	@ConfigWrapper
	private DatabaseConfiguration databaseConfiguration;

	@PromptConfig(config = "db.author_name", prompt = "Author")
	private String authorName;

	@PromptConfig(config = "db.shared_schema", prompt = "Schema")
	private String schema;

	@Setter
	@PromptConfig(config = "db.base_project_dir", prompt = "Base project directory")
	private String baseProjectDir;

	@PromptConfig(prompt = "Generate Tables", converter = ListToStringConverter.class, helpText = "separated by ,")
	private List<String> generateTables;

	private File triggersDir;

	private File viewsDir;

	private File tableDir;

	private File viewFile;

	private File tableFile;

	private File triggerFile;

	@Override
	public void afterPropertiesSet()
	{
		if (!ObjectUtils.isEmptyString(baseProjectDir)) {
			triggersDir = Paths.get(baseProjectDir, AFFINITY, SQL, "triggers").toFile();
			viewsDir = Paths.get(baseProjectDir, AFFINITY, SQL, "views").toFile();
			tableDir = Paths.get(baseProjectDir, AFFINITY, SQL, "tables").toFile();
			viewFile = Paths.get(baseProjectDir, AFFINITY, SQL, "views.xml").toFile();
			triggerFile = Paths.get(baseProjectDir, AFFINITY, SQL, "triggers.xml").toFile();
			tableFile = Paths.get(baseProjectDir, AFFINITY, SQL, "tables.xml").toFile();
		}
	}
}

