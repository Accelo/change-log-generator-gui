package com.tuannguyen.liquibase.util.io.tables;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tuannguyen.liquibase.config.model.GenerateTableConfiguration;
import com.tuannguyen.liquibase.db.IdGenerator;
import com.tuannguyen.liquibase.db.metadata.TableMetadata;
import com.tuannguyen.liquibase.util.io.ResultException;
import com.tuannguyen.liquibase.util.io.TemplateHelper;
import com.tuannguyen.liquibase.util.io.XmlHelper;

import lombok.extern.log4j.Log4j;

@Log4j
public class TableWriter
{
	private TemplateHelper templateHelper;

	private IdGenerator idGenerator;

	private XmlHelper xmlHelper;

	public TableWriter(IdGenerator idGenerator, TemplateHelper templateHelper, XmlHelper xmlHelper)
	{
		this.idGenerator = idGenerator;
		this.templateHelper = templateHelper;
		this.xmlHelper = xmlHelper;
	}

	public void writeTrigger(GenerateTableConfiguration generateTableConfiguration, TableMetadata tableMetadata)
	{
		try {
			log.info("Writing triggers changelog");
			File triggerDir = generateTableConfiguration.getTriggersDir();
			String triggerName = (tableMetadata.getName() + "_before_insert").toLowerCase();
			if (!(triggerDir != null && triggerDir.exists())) {
				log.warn(triggerDir + " not found. Creating files in current directory");
				triggerDir = templateHelper.prepareDir("triggers");
			} else {
				appendToUpdate(generateTableConfiguration.getTriggerFile(), "triggers/" + triggerName + ".xml");
			}
			File triggerFile = new File(triggerDir, triggerName + ".xml");
			Map<String, Object> data = getBaseData(generateTableConfiguration, tableMetadata);
			data.put("trigger", triggerName);
			templateHelper.write(triggerFile, "trigger.ftl", data);
		} catch (Exception e) {
			throw new ResultException("Failed to write table change log", e);
		}
	}

	public void writeView(GenerateTableConfiguration generateTableConfiguration, TableMetadata tableMetadata)
	{
		try {
			log.info("Writing views changelog");
			File viewDir = generateTableConfiguration.getViewsDir();
			String viewName = tableMetadata.getName().toLowerCase();
			if (!(viewDir != null && viewDir.exists())) {
				log.warn(viewDir + " not found. Creating files in current directory");
				viewDir = templateHelper.prepareDir("views");
			} else {
				appendToUpdate(generateTableConfiguration.getViewFile(), "views/" + viewName + ".xml");
			}
			File viewFile = new File(viewDir, viewName + ".xml");
			Map<String, Object> data = getBaseData(generateTableConfiguration, tableMetadata);
			data.put("authorName", generateTableConfiguration.getAuthorName());
			templateHelper.write(viewFile, "view.ftl", data);
		} catch (Exception e) {
			throw new ResultException("Failed to write table change log", e);
		}
	}

	public void writeTable(GenerateTableConfiguration generateTableConfiguration, TableMetadata tableMetadata)
	{
		try {
			log.info("Writing table changelog");
			File tableDir = generateTableConfiguration.getTableDir();
			String tableName = tableMetadata.getName().toLowerCase();
			if (!(tableDir != null && tableDir.exists())) {
				log.warn(tableDir + " not found. Creating files in current directory");
				tableDir = templateHelper.prepareDir("tables");
			} else {
				appendToUpdate(generateTableConfiguration.getTableFile(), "tables/" + tableName + ".xml");
			}
			File tableFile = new File(tableDir, tableName + ".xml");
			Map<String, Object> data = getBaseData(generateTableConfiguration, tableMetadata);
			data.put("generator", idGenerator);
			StringWriter stringWriter = new StringWriter();
			templateHelper.write(stringWriter, "table.ftl", data);

			String prettieredXMLString = xmlHelper.prettyXMLString(stringWriter.toString(), 4);
			try (BufferedWriter bufferedWriter = Files.newBufferedWriter(tableFile.toPath())) {
				bufferedWriter.write(prettieredXMLString);
			}
		} catch (Exception e) {
			throw new ResultException("Failed to write table change log", e);
		}
	}

	private Map<String, Object> getBaseData(
			GenerateTableConfiguration generateTableConfiguration, TableMetadata tableMetadata
	)
	{
		Map<String, Object> data = new HashMap<>();
		data.put("timestamp", idGenerator.getId());
		data.put("config", generateTableConfiguration);
		data.put("metadata", tableMetadata);
		return data;
	}

	private void appendToUpdate(File updateFile, String newFileName) throws Exception
	{
		Document doc = xmlHelper.getDocument(updateFile);
		Element rootElement = doc.getDocumentElement();
		Element newElement = doc.createElement("include");
		newElement.setAttribute("file", newFileName);
		newElement.setAttribute("relativeToChangelogFile", "true");
		rootElement.appendChild(newElement);
		List<Element> elementList = new ArrayList<>();
		NodeList childNodes = rootElement.getChildNodes();
		for (int i = 0; i < childNodes
				.getLength(); i++) {
			if (childNodes
					.item(i)
					.getNodeType() == Node.ELEMENT_NODE)
			{
				elementList.add((Element) childNodes
						.item(i));
			}
		}

		elementList.sort(Comparator.comparing(element -> element.getAttribute("file")));
		while (rootElement.hasChildNodes()) {
			rootElement.removeChild(rootElement.getFirstChild());
		}

		elementList.forEach(rootElement::appendChild);

		xmlHelper.writeDocument(doc, new FileOutputStream(updateFile), 2);
	}
}
