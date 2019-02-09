package com.tuannguyen.liquibase.gui.types;

import java.util.Arrays;
import java.util.List;

import com.jfoenix.controls.JFXToggleButton;
import com.tuannguyen.liquibase.config.model.BooleanWrapper;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputControl;

public class IndexPane extends SubtypePane
{
	@FXML
	private JFXTextFieldWrapper tableNameTF;

	@FXML
	private JFXTextFieldWrapper indexNameTF;

	@FXML
	private JFXTextFieldWrapper indexColumnsTF;

	@FXML
	private JFXToggleButton uniqueToggle;

	public IndexPane()
	{
		super("/index-pane.fxml");
	}

	@Override
	public List<TextInputControl> textInputControlList()
	{
		return Arrays.asList(tableNameTF, indexNameTF, indexColumnsTF);
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
		indexNameTF.textProperty()
				.addListener((observable, oldValue, newValue) -> {
					changeInformation.setName(newValue);
				});
		uniqueToggle.selectedProperty()
				.addListener((observable, oldValue, newValue) -> changeInformation.setUnique(BooleanWrapper.of(
						uniqueToggle.isSelected())));
		indexColumnsTF.textProperty().
				addListener((observable, oldValue, newValue) ->
						changeInformation.setValue(newValue));
	}

	@Override
	public void setChangeInformation(ChangeInformation changeInformation)
	{
		super.setChangeInformation(changeInformation);
		tableNameTF.textProperty()
				.setValue(changeInformation.getTable());
		indexColumnsTF.textProperty().setValue(changeInformation.getValue());
		uniqueToggle.selectedProperty()
				.setValue(changeInformation.getUnique() == BooleanWrapper.TRUE);

		indexNameTF.textProperty()
				.setValue(changeInformation.getName());
	}

	@Override
	public void reset()
	{
		super.reset();
		tableNameTF.setText("");
		indexColumnsTF.setText("");
		indexNameTF.setText("");
		uniqueToggle.setSelected(false);
	}
}
