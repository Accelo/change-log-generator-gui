package com.tuannguyen.liquibase.gui.types;

import java.util.Arrays;
import java.util.List;

import com.jfoenix.controls.JFXCheckBox;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

public class UpdatePane extends SubtypePane
{
	@FXML
	private JFXTextFieldWrapper tableNameTF;

	@FXML
	private JFXTextFieldWrapper columnNameTF;

	@FXML
	private JFXTextFieldWrapper valueTF;

	@FXML
	private JFXTextFieldWrapper whereTF;

	@FXML
	private JFXCheckBox computedValue;

	@FXML
	private JFXCheckBox quotedValue;

	public UpdatePane()
	{
		super("/update-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList()
	{
		return Arrays.asList(tableNameTF, columnNameTF, valueTF);
	}

	@Override
	@FXML
	public void initialize()
	{
		super.initialize();
		quotedValue.disableProperty()
				.bind(Bindings.createBooleanBinding(
						() -> computedValue.selectedProperty()
								.getValue(),
						computedValue.selectedProperty()
				));
		computedValue.disableProperty()
				.bind(Bindings.createBooleanBinding(
						() -> quotedValue.selectedProperty()
								.getValue(),
						quotedValue.selectedProperty()
				));
		tableNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setTable(newValue);
				});
		columnNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setColumn(newValue);
				});
		valueTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setValue(newValue));
		quotedValue.selectedProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setQuoted(newValue));
		computedValue.selectedProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setComputed(
						newValue));
		whereTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setWhere(newValue));
		valueTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setValue(newValue));
	}

	@Override
	public void setChangeInformation(ChangeInformation changeInformation)
	{
		super.setChangeInformation(changeInformation);
		tableNameTF.textProperty()
				.setValue(changeInformation.table()
						.getValue());
		columnNameTF.textProperty()
				.setValue(changeInformation.column()
						.getValue());
		valueTF.textProperty()
				.setValue(changeInformation.value()
						.getValue());
		quotedValue.selectedProperty()
				.setValue(changeInformation.quoted()
						.getValue());
		computedValue.selectedProperty()
				.setValue(changeInformation.computed()
						.getValue());
		whereTF.textProperty()
				.setValue(changeInformation.where()
						.getValue());
	}
}
