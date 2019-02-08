package com.tuannguyen.liquibase.gui;

import java.io.IOException;

import com.tuannguyen.liquibase.util.container.BeanFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GUIApp extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException
	{
		primaryStage.setTitle("Change Log Generator");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/change-log.fxml"));
		fxmlLoader.load();
		ChangeLogController changeLogController = fxmlLoader.getController();
		Pane root = fxmlLoader.getRoot();
		changeLogController.setStage(primaryStage);
		changeLogController.setBeanFactory(new BeanFactory());
		changeLogController.init();
		Scene scene = new Scene(root);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
