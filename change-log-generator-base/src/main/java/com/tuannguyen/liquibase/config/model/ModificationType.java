package com.tuannguyen.liquibase.config.model;

public enum ModificationType
{
	A("add"), D("delete"), U("update"), M("modify"), S("sql"), R("rename"), DR("drop");

	private String name;

	ModificationType(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
