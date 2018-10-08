package com.tuannguyen.liquibase.gui.types;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import com.tuannguyen.liquibase.config.model.BooleanWrapper;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

import java.util.Arrays;
import java.util.List;

public class AddPane extends SubtypePane {

	@FXML
	private JFXTextFieldWrapper tableNameTF;

	@FXML
	private JFXTextFieldWrapper columnNameTF;

	@FXML
	private JFXTextFieldWrapper constraintTF;

	@FXML
	private JFXTextFieldWrapper typeTF;

	@FXML
	private JFXTextFieldWrapper defaultValueTF;

	@FXML
	private JFXToggleButton uniqueToggle;

	@FXML
	private JFXToggleButton nullableToggle;

	@FXML
	private JFXCheckBox computedValue;

	@FXML
	private JFXCheckBox quotedValue;

	@FXML
	private JFXTextFieldWrapper afterColumn;


	@Override
	public List<TextInputControl> textInputControlList() {
		return Arrays.asList(typeTF, columnNameTF, tableNameTF, constraintTF);
	}

	@Override
	public void initialize() {
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
			            changeInformation.setColumn(newValue);
		            });
		defaultValueTF.textProperty()
		              .addListener((observable, oldValue, newValue) -> changeInformation.setDefaultValue(newValue));
		typeTF.textProperty()
		      .addListener((observable, oldValue, newValue) -> changeInformation.setType(newValue));
		quotedValue.selectedProperty()
		           .addListener((observable, oldValue, newValue) -> changeInformation.setQuoted(newValue));
		computedValue.selectedProperty()
		             .addListener((observable, oldValue, newValue) -> changeInformation.setComputed(newValue));
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

	public AddPane() {
		super("/add-pane.fxml");
	}

	@Override
	public void setChangeInformation(ChangeInformation currentInformation) {
		super.setChangeInformation(currentInformation);
		tableNameTF.textProperty()
		           .setValue(currentInformation.table()
		                                       .getValue());
		columnNameTF.textProperty()
		            .setValue(currentInformation.column()
		                                        .getValue());
		defaultValueTF.textProperty()
		              .setValue(currentInformation.defaultValue()
		                                          .getValue());

		typeTF.textProperty()
		      .setValue(currentInformation.type()
		                                  .getValue());
		quotedValue.selectedProperty()
		           .setValue(currentInformation.quoted()
		                                       .getValue());
		computedValue.selectedProperty()
		             .setValue(currentInformation.computed()
		                                         .getValue());
		constraintTF.textProperty()
		            .setValue(currentInformation.constraintName()
		                                        .getValue());
		nullableToggle.selectedProperty()
		              .setValue(currentInformation.getNullable() != BooleanWrapper.FALSE);

		uniqueToggle.selectedProperty()
		            .setValue(currentInformation.getUnique() == BooleanWrapper.TRUE);

		afterColumn.textProperty()
		           .setValue(currentInformation.getAfterColumn());
	}

	@Override
	public void reset() {
		super.reset();
		defaultValueTF.setText("");
		quotedValue.setSelected(false);
		computedValue.setSelected(false);
		nullableToggle.setSelected(true);
		uniqueToggle.setSelected(false);
	}
}
