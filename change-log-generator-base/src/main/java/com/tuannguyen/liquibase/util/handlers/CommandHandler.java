package com.tuannguyen.liquibase.util.handlers;

import com.tuannguyen.liquibase.util.args.ArgumentOptionResult;
import com.tuannguyen.liquibase.util.container.BeanFactory;

public abstract class CommandHandler<T>
{
	protected BeanFactory beanFactory;

	CommandHandler(BeanFactory beanFactory)
	{
		this.beanFactory = beanFactory;
	}

	public void execute(ArgumentOptionResult arguments) throws Exception
	{
		T configuration = init(arguments);
		run(arguments, configuration);
	}

	abstract T init(ArgumentOptionResult arguments) throws Exception;

	abstract void run(ArgumentOptionResult arguments, T configuration);

	abstract void close() throws Exception;
}
