package com.tuannguyen.liquibase.gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BasicInformation
{
	private StringProperty projectDir;

	private StringProperty author;

	private StringProperty jira;

	private StringProperty outputFileName;

	private StringProperty schema;

	public StringProperty projectDirProperty()
	{
		if (projectDir == null) {
			projectDir = new SimpleStringProperty();
		}
		return projectDir;
	}

	public String getProjectDir()
	{
		return projectDirProperty().get();
	}

	public void setProjectDir(String projectDir)
	{
		this.projectDirProperty()
				.set(projectDir);
	}

	public String getAuthor()
	{
		return authorProperty().get();
	}

	public void setAuthor(String author)
	{
		this.authorProperty()
				.set(author);
	}

	public String getJira()
	{
		return jiraProperty().get();
	}

	public void setJira(String jira)
	{
		this.jiraProperty()
				.set(jira);
	}

	public String getOutputFileName()
	{
		return outputFileNameProperty().get();
	}

	public void setOutputFileName(String outputFileName)
	{
		this.outputFileNameProperty()
				.set(outputFileName);
	}

	public String getSchema()
	{
		return schemaProperty().get();
	}

	public void setSchema(String schema)
	{
		this.schemaProperty()
				.set(schema);
	}

	public StringProperty authorProperty()
	{
		if (author == null) {
			author = new SimpleStringProperty();
		}
		return author;
	}

	public StringProperty jiraProperty()
	{
		if (jira == null) {
			jira = new SimpleStringProperty();
		}
		return jira;
	}

	public StringProperty outputFileNameProperty()
	{
		if (outputFileName == null) {
			outputFileName = new SimpleStringProperty();
		}
		return outputFileName;
	}

	public StringProperty schemaProperty()
	{
		if (schema == null) {
			schema = new SimpleStringProperty();
		}
		return schema;
	}
}
