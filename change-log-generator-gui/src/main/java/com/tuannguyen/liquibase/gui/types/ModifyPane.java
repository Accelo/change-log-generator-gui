package com.tuannguyen.liquibase.gui.types;

import java.util.Arrays;
import java.util.List;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import com.tuannguyen.liquibase.config.model.BooleanWrapper;
import com.tuannguyen.liquibase.config.model.ValueType;
import com.tuannguyen.liquibase.gui.helper.DefaultCallbackListCell;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

public class ModifyPane extends SubtypePane
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
	private JFXComboBox<ValueType> valueTypeCb;

	@FXML
	private JFXComboBox<BooleanWrapper> uniqueCb;

	@FXML
	private JFXComboBox<BooleanWrapper> nullableCb;

	public ModifyPane()
	{
		super("/modify-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList()
	{
		return Arrays.asList(columnNameTF, constraintTF, tableNameTF, columnNameTF);
	}

	@FXML
	public void initialize()
	{
		super.initialize();
		Arrays.stream(ValueType.values()).forEach(valueType -> valueTypeCb.getItems().add(valueType));
		valueTypeCb.setConverter(getDefaultValueTypeConverter());
		typeTF.setValidators(new ValidatorBase()
		{
			@Override protected void eval()
			{
				if ((nullableCb.getValue() != BooleanWrapper.NULL || !"".equals(extraTF.getText().trim())) && ""
						.equals(typeTF.getText().trim()))
				{
					this.hasErrors.set(true);
					this.setMessage("This field is required");
				} else {
					this.hasErrors.set(false);
				}
			}
		});

		initialiseTriState(uniqueCb);
		initialiseTriState(nullableCb);

		extraTF.textProperty().addListener((observable, oldValue, newValue) -> {
			changeInformation.setExtra(newValue);
		});

		valueTypeCb.valueProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setValueType(newValue));
	}

	private void initialiseTriState(JFXComboBox<BooleanWrapper> comboBox)
	{
		DefaultCallbackListCell<BooleanWrapper> uniqueCellFactory = new DefaultCallbackListCell<BooleanWrapper>()
		{
			@Override
			public String getTitle(BooleanWrapper item)
			{
				return item.getTitle();
			}
		};

		comboBox.setItems(FXCollections.observableArrayList(Arrays.asList(
				BooleanWrapper.NULL,
				BooleanWrapper.TRUE,
				BooleanWrapper.FALSE
		)));
		comboBox.setCellFactory(uniqueCellFactory);

		constraintTF.visibleProperty()
				.bind(Bindings.createBooleanBinding(
						() -> uniqueCb.getValue() != BooleanWrapper.NULL,
						uniqueCb.valueProperty()
				));

		tableNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setTable(newValue);
				});
		columnNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setName(newValue);
				});
		defaultValueTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setDefaultValue(newValue));
		typeTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setType(newValue));
		constraintTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setConstraintName(newValue));
		nullableCb.valueProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setNullable(newValue));
		uniqueCb.valueProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setUnique(newValue));
	}

	@Override
	public void setChangeInformation(ChangeInformation changeInformation)
	{
		super.setChangeInformation(changeInformation);
		tableNameTF.textProperty()
				.setValue(changeInformation.table()
						.getValue());
		columnNameTF.textProperty()
				.setValue(changeInformation.name()
						.getValue());
		defaultValueTF.textProperty()
				.setValue(changeInformation.defaultValue()
						.getValue());
		typeTF.textProperty()
				.setValue(changeInformation.type()
						.getValue());
		valueTypeCb.valueProperty()
				.setValue(changeInformation.getValueType());
		constraintTF.textProperty()
				.setValue(changeInformation.constraintName()
						.getValue());
		nullableCb.valueProperty()
				.setValue(changeInformation.getNullable());

		extraTF.textProperty().setValue(changeInformation.getExtra());
	}

	@Override
	public void reset()
	{
		super.reset();
		valueTypeCb.setValue(ValueType.STRING);
		defaultValueTF.setText("");
		extraTF.setText("");
		nullableCb.setValue(BooleanWrapper.NULL);
		uniqueCb.setValue(BooleanWrapper.NULL);
	}

	public boolean validate()
	{
		boolean isValid = super.validate();
		if (!isValid) {
			return isValid;
		}

		return typeTF.validate();
	}
}
