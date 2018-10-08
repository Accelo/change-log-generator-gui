package com.tuannguyen.liquibase.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.BasicInformation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class BasicController {
	private Stage stage;

	private BasicInformation basicInformation;


	@FXML
	private TextField projectDirTF;

	@FXML
	private JFXButton findProjectDirBtn;

	@FXML
	private JFXTextFieldWrapper schemaTF;

	@FXML
	private JFXTextFieldWrapper authorTF;

	@FXML
	private JFXTextFieldWrapper jiraTF;

	@FXML
	private JFXTextFieldWrapper outputFileTF;

	@FXML
	public void openFileChooser(ActionEvent actionEvent) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select base directory");
		File projectDir = directoryChooser.showDialog(stage);
		projectDirTF.setText(projectDir.getAbsolutePath());
		setProjectDirError(false);
	}

	void initialise(Stage stage) {
		this.stage = stage;
		basicInformation = new BasicInformation();

		Image     image     = new Image(getClass().getResourceAsStream("/search.png"));
		ImageView imageView = new ImageView(image);
		imageView.setPreserveRatio(true);
		imageView.setFitHeight(20);
		imageView.setFitWidth(20);
		findProjectDirBtn.setGraphic(imageView);

		basicInformation.projectDirProperty()
		                .bindBidirectional(projectDirTF.textProperty());
		basicInformation.jiraProperty()
		                .bindBidirectional(jiraTF.textProperty());
		basicInformation.outputFileNameProperty()
		                .bindBidirectional(outputFileTF.textProperty());
		basicInformation.authorProperty()
		                .bindBidirectional(authorTF.textProperty());
		basicInformation.schemaProperty()
		                .bindBidirectional(schemaTF.textProperty());

		Arrays.asList(jiraTF, outputFileTF, authorTF, schemaTF)
		      .forEach(field -> {
			      field.textProperty()
			           .addListener((observable, oldValue, newValue) -> {
				           validate(field);
			           });
		      });
		projectDirTF.textProperty()
		            .addListener((observable, oldValue, newValue) -> {
			            validateProjectDir();
		            });
		setValidator(jiraTF);
		setValidator(authorTF);
		setValidator(outputFileTF);
		setValidator(schemaTF);
	}

	private void setValidator(JFXTextFieldWrapper JFXTextFieldWrapper) {
		ValidatorBase validator = new RequiredFieldValidator();
		validator.setMessage("This field is not optional");
		JFXTextFieldWrapper.setValidators(validator);
	}

	boolean validate() {
		boolean valid = validateProjectDir();
		for (JFXTextFieldWrapper field : Arrays.asList(jiraTF, outputFileTF, authorTF, schemaTF)) {

			valid = valid & validate(field);
		}
		return valid;
	}

	private boolean validate(JFXTextFieldWrapper textField) {
		textField.resetValidation();
		return textField.validate();
	}

	private boolean validateProjectDir() {
		setProjectDirError(false);
		boolean valid = true;
		if (projectDirTF.getText()
		                .trim()
		                .isEmpty()) {
			valid = false;
			setProjectDirError(true);
		}
		return valid;
	}

	private void setProjectDirError(boolean error) {
		if (error) {
			projectDirTF.setTooltip(new Tooltip("This field is not optional"));
			projectDirTF.getStyleClass()
			            .add("textField--error");
		} else {
			projectDirTF.setTooltip(null);
			projectDirTF.getStyleClass()
			            .remove("textField--error");
		}
	}

	public BasicInformation getBasicInformation() {
		return basicInformation;
	}
}
