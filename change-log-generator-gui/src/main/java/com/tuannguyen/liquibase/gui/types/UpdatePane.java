package com.tuannguyen.liquibase.gui.types;

import java.util.Arrays;
import java.util.List;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.converters.base.NodeConverter;
import com.tuannguyen.liquibase.config.model.ValueType;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;

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
	private JFXComboBox<ValueType> valueTypeCb;

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
		Arrays.stream(ValueType.values()).forEach(valueType -> valueTypeCb.getItems().add(valueType));
		valueTypeCb.setConverter(getDefaultValueTypeConverter());

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
		valueTypeCb.valueProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setValueType(newValue));
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
		valueTypeCb.valueProperty()
				.setValue(changeInformation.getValueType());
		whereTF.textProperty()
				.setValue(changeInformation.where()
						.getValue());
	}
}
