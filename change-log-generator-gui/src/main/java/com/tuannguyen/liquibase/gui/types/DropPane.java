package com.tuannguyen.liquibase.gui.types;

import java.util.Collections;
import java.util.List;

import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

public class DropPane extends SubtypePane
{
	@FXML
	private JFXTextFieldWrapper tableNameTF;

	public DropPane()
	{
		super("/drop-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList()
	{
		return Collections.singletonList(tableNameTF);
	}

	@Override
	public void initialize()
	{
		super.initialize();
		tableNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setTable(newValue);
				});
	}

	@Override
	public void setChangeInformation(ChangeInformation currentInformation)
	{
		super.setChangeInformation(currentInformation);
		tableNameTF.textProperty()
				.setValue(currentInformation.table()
						.getValue());
	}

	@Override
	public void reset()
	{
		super.reset();
	}
}
