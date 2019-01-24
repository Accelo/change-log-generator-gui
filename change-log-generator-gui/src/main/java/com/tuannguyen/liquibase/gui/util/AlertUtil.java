package com.tuannguyen.liquibase.gui.util;

import com.tuannguyen.liquibase.gui.ui.ProgressBarAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class AlertUtil {
	public static void showAlert(Alert.AlertType alertType,String contentText) {
		showAlert(alertType, null, contentText);
	}

	public static void showError(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter  pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		TextArea textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setPrefHeight(500);
		textArea.setText(sw.toString());

		Dialog dialog = new Dialog();
		dialog.setTitle("Error");
		dialog.getDialogPane().setContent(textArea);
		dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
		dialog.showAndWait();
	}

	public static void showConfirmation(String title, String message, Runnable runnable) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(title);
		alert.setContentText(message);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.orElse(ButtonType.CANCEL) == ButtonType.OK) {
			runnable.run();
		}
	}

	public static void showAlert(Alert.AlertType alertType,  String headerText, String contentText) {
		Alert alert = new Alert(alertType);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.getButtonTypes().setAll(ButtonType.OK);
		alert.showAndWait();
	}

	public static void showInformation(String headerText, String contentText) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, contentText, ButtonType.CLOSE);
		alert.setHeaderText(headerText);
		alert.showAndWait();
	}

	public static ProgressBarAlert showProgressAlert(String headText, String title) {
		ProgressBarAlert progressBarAlert = new ProgressBarAlert(Alert.AlertType.CONFIRMATION);
		progressBarAlert.setHeaderText(headText);
		progressBarAlert.setTitle(title);
		progressBarAlert.show();
		return progressBarAlert;
	}
}
