package com.tuannguyen.liquibase.gui.types;

import java.util.Collections;
import java.util.List;

import com.tuannguyen.liquibase.gui.helper.JFXTextAreaWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

public class SQLPane extends SubtypePane
{
	@FXML
	private JFXTextAreaWrapper sqlTF;

	public SQLPane()
	{
		super("/sql-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList()
	{
		return Collections.singletonList(sqlTF);
	}

	@Override
	@FXML
	public void initialize()
	{
		super.initialize();
		sqlTF.textProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setSql(newValue));
	}

	@Override
	public void setChangeInformation(ChangeInformation changeInformation)
	{
		super.setChangeInformation(changeInformation);
		sqlTF.textProperty()
				.setValue(changeInformation.sql()
						.getValue());
	}
}
