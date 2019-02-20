package com.tuannguyen.liquibase.config.model;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tuannguyen.liquibase.config.annotations.ConfigList;
import com.tuannguyen.liquibase.config.annotations.PromptConfig;
import com.tuannguyen.liquibase.util.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GenerateChangeConfiguration implements AfterPropertiesSet
{
	public static final String AFFINITY = "affinity";

	public static final String SQL = "sql";

	@PromptConfig(config = "db.author_name", prompt = "Author")
	private String authorName;

	@PromptConfig(config = "db.shared_schema", prompt = "Schema")
	private String schema;

	@PromptConfig(config = "db.base_project_dir", prompt = "Base project directory")
	private String baseProjectDir;

	@PromptConfig(prompt = "Jira number")
	private String jiraNumber;

	@PromptConfig(prompt = "Output file name")
	private String outputFileName;

	@ConfigList(value = "change", configurationClass = ChangeConfiguration.class)
	private List<ChangeConfiguration> changeConfigurationList;

	private File sqlDir;

	private File xmlUpdatesDir;

	private File xmlViewDir;

	private File updatesFile;

	private File sqlFile;

	private File perlFile;

	private File xmlChangeLogFile;

	private File xmlTableDir;

	private File xmlTriggerDir;

	private File tableFile;

	private File viewFile;

	private File triggerFile;

	@Override
	public void afterPropertiesSet()
	{
		if (!ObjectUtils.isEmptyString(baseProjectDir)) {
			tableFile = Paths.get(baseProjectDir, AFFINITY, SQL, "tables.xml")
					.toFile();
			viewFile = Paths.get(baseProjectDir, AFFINITY, SQL, "views.xml")
					.toFile();
			triggerFile = Paths.get(baseProjectDir, AFFINITY, SQL, "triggers.xml")
					.toFile();
			File devDir = Paths.get(baseProjectDir, "bin", "updates", "dev")
					.toFile();
			xmlUpdatesDir = Paths.get(baseProjectDir, AFFINITY, SQL, "updates")
					.toFile();
			xmlTableDir = Paths.get(baseProjectDir, AFFINITY, SQL, "tables")
					.toFile();

			xmlTriggerDir = Paths.get(baseProjectDir, AFFINITY, SQL, "triggers")
					.toFile();
			xmlViewDir = Paths.get(baseProjectDir, AFFINITY, SQL, "views")
					.toFile();
			updatesFile = Paths.get(baseProjectDir, AFFINITY, SQL, "updates.xml")
					.toFile();
			sqlDir = Paths.get(devDir.getAbsolutePath(), outputFileName)
					.toFile();
			sqlFile = Paths.get(sqlDir.getAbsolutePath(), outputFileName + ".sql")
					.toFile();
			perlFile = Paths.get(sqlDir.getAbsolutePath(), "update.pl")
					.toFile();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String date = simpleDateFormat.format(Calendar.getInstance().getTime());
			File sqlFile = Paths.get(
					xmlUpdatesDir.getAbsolutePath(),
					String.format("%s-%s.xml", date, jiraNumber)
			)
					.toFile();

			xmlChangeLogFile = sqlFile.getAbsoluteFile();
		}
	}
}
