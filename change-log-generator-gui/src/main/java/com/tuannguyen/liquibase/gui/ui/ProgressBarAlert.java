package com.tuannguyen.liquibase.gui.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class ProgressBarAlert extends Alert {
	private ProgressBar progressBar;
	private DialogPane  dialogPane;

	public ProgressBarAlert(AlertType alertType) {
		super(alertType);
		init();
	}

	private void init() {
		VBox vbox = new VBox();
		progressBar = new ProgressBar();
		progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		progressBar.setMaxWidth(Double.MAX_VALUE);
		dialogPane = getDialogPane();
		vbox.getChildren()
		    .add(progressBar);
		dialogPane.setContent(vbox);
		dialogPane.getButtonTypes()
		          .setAll();
		dialogPane.setPrefWidth(350);
		dialogPane.setPrefHeight(150);
	}

	public void setDoneText(String text) {
		Label label = new Label(text);
		dialogPane.setContent(label);
		dialogPane.getButtonTypes()
		          .setAll(ButtonType.CLOSE);
	}
}
