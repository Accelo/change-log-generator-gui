package com.tuannguyen.liquibase.util.io.columns;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tuannguyen.liquibase.config.model.ChangeConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.config.model.ModificationType;
import com.tuannguyen.liquibase.db.IdGenerator;
import com.tuannguyen.liquibase.util.ObjectUtils;
import com.tuannguyen.liquibase.util.StringUtils;
import com.tuannguyen.liquibase.util.io.ResultException;
import com.tuannguyen.liquibase.util.io.TemplateHelper;
import com.tuannguyen.liquibase.util.io.XmlHelper;

import lombok.extern.log4j.Log4j;

@Log4j
public class ChangeWriter
{
	private TemplateHelper templateHelper;

	private IdGenerator idGenerator;

	private XmlHelper xmlHelper;

	public ChangeWriter(IdGenerator idGenerator, TemplateHelper templateHelper, XmlHelper xmlHelper)
	{
		this.idGenerator = idGenerator;
		this.templateHelper = templateHelper;
		this.xmlHelper = xmlHelper;
	}

	public void writeSingleTenantChange(GenerateChangeConfiguration generateChangeConfiguration)
	{
		log.info("Writing change sql");
		File sqlDir = generateChangeConfiguration.getSqlDir();
		File sqlFile;
		if (sqlDir == null) {
			log.warn("Creating files in current directory");
			sqlDir = templateHelper.prepareDir("sql");
			sqlFile = new File(sqlDir, "update.sql");
		} else {
			sqlDir.mkdirs();
			writePerlUpdate(generateChangeConfiguration, generateChangeConfiguration.getPerlFile());
			sqlFile = generateChangeConfiguration.getSqlFile();
		}

		try (Writer writer = Files.newBufferedWriter(sqlFile.toPath())) {
			writeSingleTenantSQL(generateChangeConfiguration, writer);
		} catch (IOException e) {
			throw new ResultException("Failed to write to file " + sqlFile, e);
		}
	}

	void writePerlUpdate(GenerateChangeConfiguration generateChangeConfiguration, File perlUpdate)
	{
		try (Writer fileWriter = Files.newBufferedWriter(perlUpdate.toPath())) {
			writePerlUpdate(generateChangeConfiguration, fileWriter);
		} catch (IOException e) {
			throw new ResultException("Failed to write to file " + perlUpdate, e);
		}
	}

	public void writeMultitenantChange(GenerateChangeConfiguration generateChangeConfiguration)
	{
		log.info("Writing multi tenant change");
		try {
			File xmlDir = generateChangeConfiguration.getXmlUpdatesDir();
			File xmlFile;
			if (xmlDir == null) {
				log.warn("Creating files in current directory");
				xmlDir = templateHelper.prepareDir("xml");
				xmlFile = new File(xmlDir, "changelog.xml");
			} else {
				xmlDir.mkdirs();
				File viewDir = generateChangeConfiguration.getXmlViewDir();
				if (viewDir != null && viewDir.exists()) {
					putColumnsInView(generateChangeConfiguration, viewDir);
				}
				writeNewUpdateFile(generateChangeConfiguration);
				xmlFile = generateChangeConfiguration.getXmlChangeLogFile();
				removeReferences(generateChangeConfiguration);
				dropOldFiles(generateChangeConfiguration);
			}
			try (Writer writer = Files.newBufferedWriter(xmlFile.toPath())) {
				writeMultitenantChangeLog(generateChangeConfiguration, writer);
			}
		} catch (Exception e) {
			throw new ResultException("Failed to write multi tenant changelog", e);
		}
	}

	private void dropOldFiles(GenerateChangeConfiguration generateChangeConfiguration)
	{
		generateChangeConfiguration
				.getChangeConfigurationList()
				.stream()
				.filter(changeConfiguration -> changeConfiguration.getModificationType() == ModificationType.DR)
				.forEach(changeConfiguration -> {
					String table = changeConfiguration.getTable();
					File viewDir = generateChangeConfiguration.getXmlViewDir();
					File viewFile = new File(viewDir, table + ".xml");
					if (viewFile.exists()) {
						viewFile.delete();
					}

					File tableDir = generateChangeConfiguration.getXmlTableDir();
					File tableFile = new File(tableDir, table + ".xml");
					if (tableFile.exists()) {
						tableFile.delete();
					}

					File triggerDir = generateChangeConfiguration.getXmlTriggerDir();
					File triggerFile = new File(triggerDir, table + "_before_insert.xml");
					if (triggerFile.exists()) {
						triggerFile.delete();
					}
				});
	}

	private void removeReferences(GenerateChangeConfiguration generateChangeConfiguration)
	{
		generateChangeConfiguration
				.getChangeConfigurationList()
				.stream()
				.filter(changeConfiguration -> changeConfiguration.getModificationType() == ModificationType.DR)
				.forEach(changeConfiguration -> {
					String table = changeConfiguration.getTable();
					File viewFile = generateChangeConfiguration.getViewFile();
					try {
						removeRow(viewFile, "views/" + table + ".xml");
						File tableFile = generateChangeConfiguration.getTableFile();
						removeRow(tableFile, "tables/" + table + ".xml");
						File triggerFile = generateChangeConfiguration.getTriggerFile();
						removeRow(triggerFile, "triggers/" + table + "_before_insert.xml");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
	}

	private void removeRow(File file, String rowToRemove) throws Exception
	{
		Document document = xmlHelper.getDocument(file);
		Element documentElement = document.getDocumentElement();
		NodeList nodeList = documentElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element element = (Element) nodeList.item(i);
			if (rowToRemove.equals(element.getAttribute("file"))) {
				Node prev = element.getPreviousSibling();
				if (prev != null &&
						prev.getNodeType() == Node.TEXT_NODE &&
						prev.getNodeValue().trim().length() == 0)
				{
					element.getParentNode().removeChild(prev);
				}
				documentElement.removeChild(element);
			}
		}
		xmlHelper.writeDocument(document, Files.newOutputStream(file.toPath()), 2);
	}

	private void putColumnsInView(GenerateChangeConfiguration generateChangeConfiguration, File viewDir)
	{
		for (ChangeConfiguration changeConfiguration : generateChangeConfiguration.getChangeConfigurationList()) {
			if (changeConfiguration.getModificationType() == ModificationType.A
					|| changeConfiguration.getModificationType() == ModificationType.D
					|| changeConfiguration.getModificationType() == ModificationType.R)
			{
				String table = changeConfiguration.getTable();
				File viewFile = new File(viewDir, table + ".xml");
				if (!viewFile.exists()) {
					log.warn(String.format(
							"View %s not exists. Skipping column %s",
							table,
							changeConfiguration.getName()
					));
					continue;
				}
				ViewHandler viewHandler = new ViewHandler(generateChangeConfiguration,
						changeConfiguration, templateHelper, xmlHelper, viewFile
				);
				viewHandler.process();
			}
		}
	}

	private void writeNewUpdateFile(GenerateChangeConfiguration generateChangeConfiguration)
	{
		File updateFile = generateChangeConfiguration.getUpdatesFile();
		if (!(updateFile != null && updateFile.exists())) {
			log.warn("Update.xml not exists. Skipping...");
			return;
		}

		try {
			File backupFile = Files.createTempFile("tmp", null)
					.toFile();
			Files.copy(updateFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			File tmpFile = Files.createTempFile("tmp", null)
					.toFile();
			tmpFile.deleteOnExit();
			try (FileOutputStream fileOutputStream = new FileOutputStream(tmpFile)) {
				writeNewUpdateFile(generateChangeConfiguration, fileOutputStream);
				Files.copy(tmpFile.toPath(), updateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Files.copy(backupFile.toPath(), updateFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				throw e;
			}
		} catch (IOException e) {
			throw new ResultException("Failed to write to update file", e);
		}
	}

	public void writeSingleTenantSQL(GenerateChangeConfiguration generateChangeConfiguration, Writer writer) throws
			IOException
	{
		Map<String, Object> data = new HashMap<>();
		List<ChangeConfiguration> changeConfigurations = generateChangeConfiguration.getChangeConfigurationList();
		Map<String, List<ChangeConfiguration>> changesMapByTable
				= changeConfigurations.stream()
				.filter(change -> !ObjectUtils.isEmptyString(change.getTable()))
				.collect(Collectors.groupingBy(
						ChangeConfiguration::getTable));
		data.put("table_changes", changesMapByTable);
		data.put("changes", changeConfigurations);
		StringWriter stringWriter = new StringWriter();
		templateHelper.write(stringWriter, "change-sql.ftl", data);
		String trimmedString = StringUtils.trimRedundantWhitespaces(stringWriter.toString());
		writer.write(trimmedString);
	}

	public void writePerlUpdate(
			GenerateChangeConfiguration generateChangeConfiguration, Writer writer
	)
	{
		Map<String, Object> data = new HashMap<>();
		data.put("file", generateChangeConfiguration.getOutputFileName());
		templateHelper.write(writer, "perl-update.ftl", data);
	}

	public void writeMultitenantChangeLog(GenerateChangeConfiguration generateChangeConfiguration, Writer writer) throws
			Exception
	{
		Map<String, Object> data = new HashMap<>();
		data.put("generator", idGenerator);
		data.put("config", generateChangeConfiguration);
		StringWriter stringWriter = new StringWriter();
		templateHelper.write(stringWriter, "change.ftl", data);
		String prettieredXMLString = xmlHelper.prettyXMLString(stringWriter.toString(), 4);
		writer.write(prettieredXMLString);
	}

	public void writeNewUpdateFile(
			GenerateChangeConfiguration generateChangeConfiguration, OutputStream outputStream
	)
	{
		File updateFile = generateChangeConfiguration.getUpdatesFile();
		Document doc = null;
		try {
			doc = xmlHelper.getDocument(updateFile);
			Element rootElement = doc.getDocumentElement();
			Element element = doc.createElement("include");
			element.setAttribute("file", "updates/" + generateChangeConfiguration.getXmlChangeLogFile().getName());
			element.setAttribute("relativeToChangelogFile", "true");
			rootElement.appendChild(element);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			xmlHelper.writeDocument(doc, byteArrayOutputStream, 2);
			String processedString = byteArrayOutputStream.toString().replaceAll(" {2}", "\t");
			outputStream.write(processedString.getBytes());
		} catch (Exception e) {
			throw new ResultException("Failed to write to update file", e);
		}
	}
}
