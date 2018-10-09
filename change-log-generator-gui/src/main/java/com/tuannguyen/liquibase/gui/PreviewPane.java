package com.tuannguyen.liquibase.gui;

import com.jfoenix.controls.JFXTabPane;
import com.tuannguyen.liquibase.config.model.GenerateChangeConfiguration;
import com.tuannguyen.liquibase.gui.util.AlertUtil;
import com.tuannguyen.liquibase.util.io.columns.ChangeWriter;
import javafx.scene.control.Tab;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class PreviewPane extends JFXTabPane {
	private final ChangeWriter changeWriter;

	public PreviewPane(ChangeWriter changeWriter) {
		this.changeWriter = changeWriter;
	}

	public void setGenerateChangeConfiguration(GenerateChangeConfiguration generateChangeConfiguration) {
		List<Tab> allTabs = getTabs();
		allTabs.clear();

		try {
			Tab tab = getXMLChangeLogTab(generateChangeConfiguration);
			if (tab != null) {
				allTabs.add(tab);
			}

			tab = getSQLTab(generateChangeConfiguration);
			if (tab != null) {
				allTabs.add(tab);
			}

			tab = getPerlTab(generateChangeConfiguration);
			if (tab != null) {
				allTabs.add(tab);
			}

			tab = getXMLUpdatesTab(generateChangeConfiguration);
			if (tab != null) {
				allTabs.add(tab);
			}
		} catch (Exception e) {
			AlertUtil.showError(e);
		}

	}

	private Tab getXMLChangeLogTab(GenerateChangeConfiguration generateChangeConfiguration) throws Exception {
		StringWriter stringWriter = new StringWriter();
		changeWriter.writeMultitenantChangeLog(generateChangeConfiguration, stringWriter);
		Tab tab = new Tab(generateChangeConfiguration.getXmlChangeLogFile()
		                                             .getName());
		PreviewTab previewTab = new PreviewTab();
		previewTab.setContent(generateChangeConfiguration.getXmlChangeLogFile()
		                                                 .getAbsolutePath(), stringWriter.toString());
		tab.setContent(previewTab);
		return tab;
	}

	private Tab getXMLUpdatesTab(GenerateChangeConfiguration generateChangeConfiguration) {
		if (!generateChangeConfiguration.getUpdatesFile().exists()) {
			return null;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		changeWriter.writeNewUpdateFile(generateChangeConfiguration, byteArrayOutputStream);
		Tab tab = new Tab(generateChangeConfiguration.getUpdatesFile()
		                                             .getName());
		PreviewTab previewTab = new PreviewTab();
		previewTab.setContent(generateChangeConfiguration.getUpdatesFile()
		                                                 .getAbsolutePath(), byteArrayOutputStream.toString());
		tab.setContent(previewTab);
		return tab;
	}

	private Tab getPerlTab(GenerateChangeConfiguration generateChangeConfiguration) {
		StringWriter stringWriter = new StringWriter();
		changeWriter.writePerlUpdate(generateChangeConfiguration, stringWriter);
		Tab tab = new Tab(generateChangeConfiguration.getPerlFile()
		                                             .getName());
		PreviewTab previewTab = new PreviewTab();
		previewTab.setContent(generateChangeConfiguration.getPerlFile()
		                                                 .getAbsolutePath(), stringWriter.toString());
		tab.setContent(previewTab);
		return tab;
	}

	private Tab getSQLTab(GenerateChangeConfiguration generateChangeConfiguration) throws IOException {
		StringWriter stringWriter = new StringWriter();
		changeWriter.writeSingleTenantSQL(generateChangeConfiguration, stringWriter);
		Tab tab = new Tab(generateChangeConfiguration.getSqlFile()
		                                             .getName());
		PreviewTab previewTab = new PreviewTab();
		previewTab.setContent(generateChangeConfiguration.getSqlFile()
		                                                 .getAbsolutePath(), stringWriter.toString());
		tab.setContent(previewTab);
		return tab;
	}
}
