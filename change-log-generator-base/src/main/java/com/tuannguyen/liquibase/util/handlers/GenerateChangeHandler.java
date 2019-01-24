package com.tuannguyen.liquibase.util.handlers;

import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.util.args.ArgumentOptionResult;
import com.tuannguyen.liquibase.util.container.BeanFactory;
import com.tuannguyen.liquibase.util.io.columns.ChangeWriter;

import java.util.Map;

public class GenerateChangeHandler extends CommandHandler<GenerateChangeConfiguration> {
	public GenerateChangeHandler(BeanFactory beanFactory) {
		super(beanFactory);
	}

	@Override
	GenerateChangeConfiguration init(ArgumentOptionResult arguments) throws Exception {
		Map<String, Object> argumentValues = arguments.getValues();
		String filename = (String) argumentValues.get("filename");
		boolean promptMode = argumentValues.get("yes") != Boolean.TRUE;
		beanFactory.getAppConfigurationReader().init(filename, promptMode);
		return beanFactory.getAppConfigurationReader().readConfiguration(GenerateChangeConfiguration.class);
	}

	@Override
	void run(ArgumentOptionResult arguments, GenerateChangeConfiguration configuration) {
		ChangeWriter changeWriter = beanFactory.getChangeWriter();
		changeWriter.writeSingleTenantChange(configuration);
		changeWriter.writeMultitenantChange(configuration);
	}

	@Override
	void close() throws Exception {
		//not required
	}

}
