package com.tuannguyen.liquibase.gui;

import com.tuannguyen.liquibase.gui.helper.LoadablePane;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

class PreviewTab extends LoadablePane {

	@FXML
	private TextField fileTF;

	@FXML
	private TextArea contentTF;

	void setContent(String file, String content) {
		fileTF.textProperty()
		      .setValue(file);
		contentTF.textProperty()
		         .setValue(content);
	}

	PreviewTab() {
		super("/preview-pane.fxml");
	}
}
