package com.tuannguyen.liquibase.gui.helper;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class LoadablePane extends Pane {
	public LoadablePane(String fxmlLocation) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setController(this);
			fxmlLoader.setLocation(getClass().getResource(fxmlLocation));
			Node root = fxmlLoader.load();
			getChildren().add(root);
		} catch (IOException e) {
			throw new RuntimeException("Pane " + fxmlLocation + " not found");
		}
	}
}
