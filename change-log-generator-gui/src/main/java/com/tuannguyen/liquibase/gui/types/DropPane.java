package com.tuannguyen.liquibase.gui.types;

import com.tuannguyen.liquibase.config.model.BooleanWrapper;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DropPane extends SubtypePane {

	@FXML
	private JFXTextFieldWrapper tableNameTF;

	@Override
	public List<TextInputControl> textInputControlList() {
		return Collections.singletonList(tableNameTF);
	}

	@Override
	public void initialize() {
		super.initialize();
		tableNameTF.textProperty()
		           .addListener((observable, oldValue, newValue) -> {
			           changeInformation.setTable(newValue);
		           });
	}

	public DropPane() {
		super("/drop-pane.fxml");
	}

	@Override
	public void setChangeInformation(ChangeInformation currentInformation) {
		super.setChangeInformation(currentInformation);
		tableNameTF.textProperty()
		           .setValue(currentInformation.table()
		                                       .getValue());
	}

	@Override
	public void reset() {
		super.reset();
	}
}
