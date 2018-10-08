package com.tuannguyen.liquibase.gui.types;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.tuannguyen.liquibase.config.model.BooleanWrapper;
import com.tuannguyen.liquibase.gui.helper.DefaultCallbackListCell;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

import java.util.Arrays;
import java.util.List;

public class ModifyPane extends SubtypePane {

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
	private JFXCheckBox computedValue;

	@FXML
	private JFXCheckBox quotedValue;

	@FXML
	private JFXComboBox<BooleanWrapper> uniqueCb;

	@FXML
	private JFXComboBox<BooleanWrapper> nullableCb;

	public ModifyPane() {
		super("/modify-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList() {
		return Arrays.asList(typeTF, columnNameTF, constraintTF, tableNameTF, columnNameTF);
	}

	@FXML
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
		initialiseTriState(uniqueCb);
		initialiseTriState(nullableCb);
	}

	private void initialiseTriState(JFXComboBox<BooleanWrapper> comboBox) {
		DefaultCallbackListCell<BooleanWrapper> uniqueCellFactory = new DefaultCallbackListCell<BooleanWrapper>() {
			@Override
			public String getTitle(BooleanWrapper item) {
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
		nullableCb.valueProperty()
		          .addListener((observable, oldValue, newValue) -> changeInformation.setNullable(newValue));
		uniqueCb.valueProperty()
		        .addListener((observable, oldValue, newValue) -> changeInformation.setUnique(newValue));

	}

	@Override
	public void setChangeInformation(ChangeInformation changeInformation) {
		super.setChangeInformation(changeInformation);
		tableNameTF.textProperty()
		           .setValue(changeInformation.table()
		                                      .getValue());
		columnNameTF.textProperty()
		            .setValue(changeInformation.column()
		                                       .getValue());
		defaultValueTF.textProperty()
		              .setValue(changeInformation.defaultValue()
		                                         .getValue());
		typeTF.textProperty()
		      .setValue(changeInformation.type()
		                                 .getValue());
		quotedValue.selectedProperty()
		           .setValue(changeInformation.quoted()
		                                      .getValue());
		computedValue.selectedProperty()
		             .setValue(changeInformation.computed()
		                                        .getValue());
		constraintTF.textProperty()
		            .setValue(changeInformation.constraintName()
		                                       .getValue());
		nullableCb.valueProperty()
		          .setValue(changeInformation.getNullable());
	}

	@Override
	public void reset() {
		super.reset();
		quotedValue.setSelected(false);
		computedValue.setSelected(false);
		defaultValueTF.setText("");
		nullableCb.setValue(BooleanWrapper.NULL);
		uniqueCb.setValue(BooleanWrapper.NULL);
	}
}
