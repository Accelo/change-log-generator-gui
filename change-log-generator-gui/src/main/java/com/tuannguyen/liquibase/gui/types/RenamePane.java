package com.tuannguyen.liquibase.gui.types;

import java.util.Arrays;
import java.util.List;

import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

public class RenamePane extends SubtypePane
{
	@FXML
	private JFXTextFieldWrapper tableNameTF;

	@FXML
	private JFXTextFieldWrapper columnNameTF;

	@FXML
	private JFXTextFieldWrapper newColumnTF;

	@FXML
	private JFXTextFieldWrapper typeTF;

	public RenamePane()
	{
		super("/rename-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList()
	{
		return Arrays.asList(typeTF, tableNameTF, columnNameTF, newColumnTF);
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
		newColumnTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setNewColumn(newValue));
		typeTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setType(newValue));
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
		newColumnTF.textProperty()
				.setValue(changeInformation.newColumn()
						.getValue());
		typeTF.textProperty()
				.setValue(changeInformation.getType());
	}
}
