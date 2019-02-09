package com.tuannguyen.liquibase.gui.types;

import java.util.Arrays;
import java.util.List;

import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

public class DeletePane extends SubtypePane
{
	@FXML
	private JFXTextFieldWrapper tableNameTF;

	@FXML
	private JFXTextFieldWrapper columnNameTF;

	public DeletePane()
	{
		super("/delete-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList()
	{
		return Arrays.asList(tableNameTF, columnNameTF);
	}

	@Override
	@FXML
	public void initialize()
	{
		super.initialize();
		tableNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setTable(newValue);
				});
		columnNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setName(newValue);
				});
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
	}
}
