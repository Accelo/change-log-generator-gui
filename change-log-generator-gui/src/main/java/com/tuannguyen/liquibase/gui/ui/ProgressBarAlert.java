package com.tuannguyen.liquibase.gui.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

public class ProgressBarAlert extends Alert
{
	private ProgressBar progressBar;

	private DialogPane dialogPane;

	private Runnable closeListener = () -> {
	};

	public ProgressBarAlert(AlertType alertType)
	{
		super(alertType);
		init();
	}

	private void init()
	{
		VBox vbox = new VBox();
		progressBar = new ProgressBar();
		progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		progressBar.setMaxWidth(Double.MAX_VALUE);
		dialogPane = getDialogPane();
		vbox.getChildren()
				.add(progressBar);
		setResizable(true);
		dialogPane.setPrefSize(450, 180);
		dialogPane.setContent(vbox);
		dialogPane.getButtonTypes()
				.setAll();
	}

	public void setDoneText(String text)
	{
		Label label = new Label(text);
		label.setWrapText(true);
		dialogPane.setContent(label);
		dialogPane.getButtonTypes()
				.setAll(ButtonType.CLOSE);
		((Button) dialogPane.lookupButton(ButtonType.CLOSE)).setOnAction((e) -> closeListener.run());
	}

	public void setCloseListener(Runnable closeListener)
	{
		this.closeListener = closeListener;
	}
}
