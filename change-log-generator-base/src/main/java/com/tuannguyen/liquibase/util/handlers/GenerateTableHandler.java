package com.tuannguyen.liquibase.util.handlers;

import com.tuannguyen.liquibase.config.model.GenerateTableConfiguration;
import com.tuannguyen.liquibase.config.reader.AppConfigurationReader;
import com.tuannguyen.liquibase.db.ConnectionManager;
import com.tuannguyen.liquibase.db.metadata.TableMetadata;
import com.tuannguyen.liquibase.util.args.ArgumentOptionResult;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.io.tables.TableWriter;
import lombok.extern.log4j.Log4j;

import java.util.List;
import java.util.Map;

@Log4j
public class GenerateTableHandler extends CommandHandler<GenerateTableConfiguration> {
	private ConnectionManager connectionManager;

	public GenerateTableHandler(BeanFactory beanFactory) {
		super(beanFactory);
	}

	@Override
	GenerateTableConfiguration init(ArgumentOptionResult arguments) {
		Map<String, Object> argumentValues = arguments.getValues();
		String filename = (String) argumentValues.get("filename");
		boolean promptMode = argumentValues.get("yes") != Boolean.TRUE;
		AppConfigurationReader appConfigurationReader = beanFactory.getAppConfigurationReader();
		appConfigurationReader.init(filename, promptMode);
		return appConfigurationReader.readConfiguration(GenerateTableConfiguration.class);
	}

	@Override
	void run(ArgumentOptionResult arguments, GenerateTableConfiguration configuration) {
		log.info("Generate table change logs");
		List<String> generateTables = configuration.getGenerateTables();
		if (generateTables.isEmpty()) {
			log.info("Nothing to run");
			return;
		}
		connectionManager = beanFactory.getConnectionManager(configuration.getDatabaseConfiguration());
		TableWriter tableWriter = beanFactory.getTableWriter();
		for (String table : generateTables) {
			TableMetadata tableMetadata = beanFactory.getDatabaseMetadaReader().readMetadata(connectionManager, table);
			tableWriter.writeTable(configuration, tableMetadata);
			tableWriter.writeView(configuration, tableMetadata);
			tableWriter.writeTrigger(configuration, tableMetadata);
		}
	}

	@Override
	void close() throws Exception {
		connectionManager.close();
	}
}
