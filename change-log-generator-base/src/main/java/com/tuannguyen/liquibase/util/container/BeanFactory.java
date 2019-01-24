package com.tuannguyen.liquibase.util.container;

import com.tuannguyen.liquibase.config.model.DatabaseConfiguration;
import com.tuannguyen.liquibase.config.reader.AppConfigurationReader;
import com.tuannguyen.liquibase.config.reader.InputConfigReader;
import com.tuannguyen.liquibase.config.reader.PropertyLoader;
import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.db.IdGenerator;
import com.tuannguyen.liquibase.db.metadata.ColumnMetadataReader;
import com.tuannguyen.liquibase.db.metadata.DatabaseMetadaReader;
import com.tuannguyen.liquibase.db.metadata.IndexMetadataReader;
import com.tuannguyen.liquibase.util.args.ArgumentParser;
import com.tuannguyen.liquibase.util.args.Command;
import com.tuannguyen.liquibase.util.handlers.CommandHandler;
import com.tuannguyen.liquibase.util.io.InputReader;
import com.tuannguyen.liquibase.util.io.TemplateHelper;
import com.tuannguyen.liquibase.util.io.XmlHelper;
import com.tuannguyen.liquibase.util.io.columns.ChangeWriter;
import com.tuannguyen.liquibase.util.io.tables.TableWriter;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.util.Scanner;

@Getter
public class BeanFactory {
	private PropertyLoader         propertyLoader;
	private InputReader            inputReader;
	private DatabaseMetadaReader   databaseMetadaReader;
	private InputConfigReader      inputConfigReader;
	private AppConfigurationReader appConfigurationReader;
	private TableWriter            tableWriter;
	private ChangeWriter           changeWriter;
	private IdGenerator            idGenerator;
	private ArgumentParser         argumentParser = new ArgumentParser();
	private IndexMetadataReader    indexMetadataReader;
	private ColumnMetadataReader   columnMetadataReader;
	private TemplateHelper         templateHelper;
	private XmlHelper              xmlHelper;

	public BeanFactory() {
		propertyLoader = new PropertyLoader();
		inputReader = new InputReader(new Scanner(System.in));
		inputConfigReader = new InputConfigReader(inputReader);
		indexMetadataReader = new IndexMetadataReader();
		xmlHelper = new XmlHelper();
		columnMetadataReader = new ColumnMetadataReader();
		databaseMetadaReader = new DatabaseMetadaReader(indexMetadataReader, columnMetadataReader);
		appConfigurationReader = new AppConfigurationReader(inputConfigReader, propertyLoader);
		idGenerator = new IdGenerator("yyyyMMddHHmmss");
		templateHelper = new TemplateHelper();
		tableWriter = new TableWriter(idGenerator, templateHelper, xmlHelper);
		changeWriter = new ChangeWriter(idGenerator, templateHelper, xmlHelper);
	}

	public CommandHandler getHandler(Command command) {
		try {
			Class<? extends CommandHandler> commandHandler = command.commandHandler();
			Constructor<? extends CommandHandler> constructor = commandHandler.getDeclaredConstructor(BeanFactory.class);
			return constructor.newInstance(this);
		} catch (Exception e) {
			throw new CommandException("Failed to get handler", e);
		}
	}

	public ConnectionManager getConnectionManager(DatabaseConfiguration configuration) {
		return new ConnectionManager(configuration);
	}
}
