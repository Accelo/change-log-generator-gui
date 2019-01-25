package com.tuannguyen.liquibase.gui.ui;

import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ProgressBarAlert extends Alert {
	private ProgressBar progressBar;
	private DialogPane  dialogPane;
	private Runnable    closeListener = () -> {
	};

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
		setResizable(true);
		dialogPane.setPrefSize(450, 180);
		dialogPane.setContent(vbox);
		dialogPane.getButtonTypes()
		          .setAll();
	}

	public void setDoneText(String text) {
		Label label = new Label(text);
		label.setWrapText(true);
		dialogPane.setContent(label);
		dialogPane.getButtonTypes()
		          .setAll(ButtonType.CLOSE);
		((Button) dialogPane.lookupButton(ButtonType.CLOSE)).setOnAction((e) -> closeListener.run());
	}

	public void setCloseListener(Runnable closeListener) {
		this.closeListener = closeListener;
	}
}
