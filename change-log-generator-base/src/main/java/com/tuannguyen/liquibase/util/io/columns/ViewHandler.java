package com.tuannguyen.liquibase.util.io.columns;

import com.tuannguyen.liquibase.config.model.ChangeConfiguration;
import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.config.model.ModificationType;
import com.tuannguyen.liquibase.db.metadata.ColumnMetadata;
import com.tuannguyen.liquibase.db.metadata.TableMetadata;
import com.tuannguyen.liquibase.util.io.TemplateHelper;
import com.tuannguyen.liquibase.util.io.XmlHelper;
import lombok.extern.log4j.Log4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j
class ViewHandler {
	private static final String                      METADATA_KEY = "metadata";
	private              GenerateChangeConfiguration generateChangeConfiguration;
	private              ChangeConfiguration         changeConfiguration;
	private              TemplateHelper              templateHelper;
	private              File                        viewFile;
	private              XmlHelper                   xmlHelper;

	ViewHandler(GenerateChangeConfiguration generateChangeConfiguration, ChangeConfiguration changeConfiguration, TemplateHelper templateHelper, XmlHelper xmlHelper, File viewFile) {
		this.generateChangeConfiguration = generateChangeConfiguration;
		this.changeConfiguration = changeConfiguration;
		this.templateHelper = templateHelper;
		this.viewFile = viewFile;
		this.xmlHelper = xmlHelper;
	}

	void process() {
		try {
			if (changeConfiguration.getModificationType() == ModificationType.A) {
				handleAddition();
			} else {
				handleDeletion();
			}
		} catch (SAXException e) {
			log.error("Cannot read xml file", e);
		} catch (ParserConfigurationException e) {
			log.error("Cannot parse xml file", e);
		} catch (IOException e) {
			log.error("Failed to process xml file", e);
		}
	}

	private void handleDeletion() throws ParserConfigurationException, IOException, SAXException {
		Map<String, Object> parseData = getParseData();
		TableMetadata tableMetadata = (TableMetadata) parseData.get(METADATA_KEY);
		final List<ColumnMetadata> columnMetadataList = tableMetadata.getColumnMetadata();
		OptionalInt optionalIndex = IntStream.range(0, columnMetadataList.size())
				.filter(index -> columnMetadataList.get(index).getName().equals(changeConfiguration.getName()))
				.findFirst();
		if (optionalIndex.isPresent()) {
			int index = optionalIndex.getAsInt();
			columnMetadataList.remove(index);
		}
		writeToFile(parseData);
	}

	private void handleAddition() throws ParserConfigurationException, IOException, SAXException {
		Map<String, Object> parseData = getParseData();
		TableMetadata tableMetadata = (TableMetadata) parseData.get(METADATA_KEY);
		final List<ColumnMetadata> columnMetadataList = tableMetadata.getColumnMetadata();
		OptionalInt optionalIndex = IntStream.range(0, columnMetadataList.size() - 1)
				.filter(index -> columnMetadataList.get(index).getName().equals(changeConfiguration.getAfterColumn()))
				.findFirst();
		ColumnMetadata columnMetadata = ColumnMetadata.builder().name(changeConfiguration.getName()).build();
		if (optionalIndex.isPresent()) {
			int index = optionalIndex.getAsInt();
			columnMetadataList.add(index + 1, columnMetadata);
		} else {
			columnMetadataList.add(columnMetadata);
		}
		writeToFile(parseData);
	}

	private Map<String, Object> getParseData() throws ParserConfigurationException, IOException, SAXException {
		Document doc = xmlHelper.getDocument(viewFile);
		Element rootElement = doc.getDocumentElement();
		Element viewElement = (Element) doc.getElementsByTagName("createView").item(0);
		String timestamp = ((Element)rootElement.getElementsByTagName("changeSet").item(0)).getAttribute("id");
		String author = ((Element) doc.getElementsByTagName("changeSet").item(0)).getAttribute("author");
		String content = viewElement.getTextContent();
		String schema = generateChangeConfiguration.getSchema();
		String table = changeConfiguration.getTable();
		String[] lines = content.split("\\r?\\n");
		Pattern pattern = Pattern.compile(String.format("\\s+`%s`\\.`%s`\\.`(.*)` AS `(.*)`", schema, table));
		List<ColumnMetadata> columnMetadataList = Arrays.stream(lines).map(line -> {
			Matcher matcher = pattern.matcher(line);
			if (matcher.lookingAt()) {
				return matcher.group(1);
			}
			return null;
		}).filter(Objects::nonNull).map(column -> ColumnMetadata.builder().name(column).build()).collect(Collectors.toList());
		Map<String, Object> data = new HashMap<>();
		TableMetadata tableMetadata = new TableMetadata(table, columnMetadataList, null);
		data.put("timestamp", timestamp);
		data.put(METADATA_KEY, tableMetadata);
		data.put("config", generateChangeConfiguration);
		data.put("authorName", author);
		return data;
	}

	private void writeToFile(Map<String, Object> data) {
		templateHelper.write(viewFile, "view.ftl", data);
	}
}
