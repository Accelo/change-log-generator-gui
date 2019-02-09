package com.tuannguyen.liquibase.gui.types;

import java.util.Arrays;
import java.util.List;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.tuannguyen.liquibase.config.model.BooleanWrapper;
import com.tuannguyen.liquibase.config.model.ValueType;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

public class AddPane extends SubtypePane
{
	@FXML
	private JFXTextFieldWrapper tableNameTF;

	@FXML
	private JFXTextFieldWrapper columnNameTF;

	@FXML
	private JFXTextFieldWrapper constraintTF;

	@FXML
	private JFXTextFieldWrapper typeTF;

	@FXML
	private JFXTextField extraTF;

	@FXML
	private JFXTextFieldWrapper defaultValueTF;

	@FXML
	private JFXToggleButton uniqueToggle;

	@FXML
	private JFXToggleButton nullableToggle;

	@FXML
	private JFXComboBox<ValueType> valueTypeCb;

	@FXML
	private JFXTextFieldWrapper afterColumn;

	public AddPane()
	{
		super("/add-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList()
	{
		return Arrays.asList(typeTF, columnNameTF, tableNameTF, constraintTF);
	}

	@Override
	public void initialize()
	{
		super.initialize();
		Arrays.stream(ValueType.values()).forEach(valueType -> valueTypeCb.getItems().add(valueType));
		valueTypeCb.setConverter(getDefaultValueTypeConverter());

		constraintTF.visibleProperty()
				.bind(Bindings.createBooleanBinding(
						() -> uniqueToggle.selectedProperty()
								.getValue(),
						uniqueToggle.selectedProperty()
				));

		tableNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setTable(newValue);
				});
		columnNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setName(newValue);
				});
		extraTF.textProperty().addListener((observable, oldValue, newValue) -> {
			changeInformation.setExtra(newValue);
		});
		defaultValueTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setDefaultValue(newValue));
		typeTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setType(newValue));
		valueTypeCb.valueProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setValueType(newValue));
		constraintTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setConstraintName(newValue));
		nullableToggle.selectedProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setNullable(BooleanWrapper.of(
						nullableToggle.isSelected())));

		uniqueToggle.selectedProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setUnique(BooleanWrapper.of(
						uniqueToggle.isSelected())));

		constraintTF.visibleProperty()
				.bind(Bindings.createBooleanBinding(
						() -> uniqueToggle.isSelected(),
						uniqueToggle.selectedProperty()
				));

		afterColumn.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setAfterColumn(newValue));
	}

	@Override
	public void setChangeInformation(ChangeInformation currentInformation)
	{
		super.setChangeInformation(currentInformation);
		tableNameTF.textProperty()
				.setValue(currentInformation.table()
						.getValue());
		columnNameTF.textProperty()
				.setValue(currentInformation.name()
						.getValue());
		defaultValueTF.textProperty()
				.setValue(currentInformation.defaultValue()
						.getValue());

		typeTF.textProperty()
				.setValue(currentInformation.type()
						.getValue());
		valueTypeCb.valueProperty()
				.setValue(currentInformation.getValueType());
		constraintTF.textProperty()
				.setValue(currentInformation.constraintName()
						.getValue());
		nullableToggle.selectedProperty()
				.setValue(currentInformation.getNullable() != BooleanWrapper.FALSE);

		uniqueToggle.selectedProperty()
				.setValue(currentInformation.getUnique() == BooleanWrapper.TRUE);

		afterColumn.textProperty()
				.setValue(currentInformation.getAfterColumn());
		extraTF.textProperty().setValue(currentInformation.getExtra());
	}

	@Override
	public void reset()
	{
		super.reset();
		defaultValueTF.setText("");
		valueTypeCb.setValue(ValueType.STRING);
		nullableToggle.setSelected(true);
		uniqueToggle.setSelected(false);
		extraTF.setText("");
	}
}
