package com.tuannguyen.liquibase.gui.types;

import java.util.List;

import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import com.tuannguyen.liquibase.gui.helper.JFXPristine;
import com.tuannguyen.liquibase.gui.helper.JFXTextAreaWrapper;
import com.tuannguyen.liquibase.gui.helper.JFXTextFieldWrapper;
import com.tuannguyen.liquibase.gui.helper.LoadablePane;
import com.tuannguyen.liquibase.gui.model.ChangeInformation;

import javafx.scene.control.TextInputControl;

public abstract class SubtypePane extends LoadablePane
{
	protected ChangeInformation changeInformation;

	SubtypePane(String fxmlLocation)
	{
		super(fxmlLocation);
	}

	public abstract List<TextInputControl> textInputControlList();

	public void initialize()
	{
		reset();
		textInputControlList().forEach(field -> {
			field.textProperty()
					.addListener((observable, oldValue, newValue) -> {
						if (field instanceof JFXPristine) {
							if (!((JFXPristine) field).isPristine()) {
								validate(field);
							}
						}
					});
			setValidator(field);
		});
	}

	public void reset()
	{
		textInputControlList().forEach(textInputControl -> {
			resetValidation(textInputControl);
			if (textInputControl instanceof JFXPristine) {
				((JFXPristine) textInputControl).setPristine(true);
			}
			textInputControl.setText("");
		});
	}

	public boolean validate()
	{
		boolean validate = true;
		for (TextInputControl textInputControl : textInputControlList()) {
			validate = validate & validate(textInputControl);
		}
		return validate;
	}

	private void setValidator(TextInputControl textInputControl)
	{
		ValidatorBase validator = new RequiredFieldValidator();
		validator.setMessage("This field is not optional");
		if (textInputControl instanceof JFXTextFieldWrapper) {
			((JFXTextFieldWrapper) textInputControl).setValidators(validator);
		} else if (textInputControl instanceof JFXTextAreaWrapper) {
			((JFXTextAreaWrapper) textInputControl).setValidators(validator);
		}
	}

	private boolean validate(TextInputControl textInputControl)
	{
		resetValidation(textInputControl);
		if (!textInputControl.isVisible()) {
			return true;
		}
		if (textInputControl instanceof JFXTextFieldWrapper) {
			return ((JFXTextFieldWrapper) textInputControl).validate();
		} else if (textInputControl instanceof JFXTextAreaWrapper) {
			return ((JFXTextAreaWrapper) textInputControl).validate();
		}
		return false;
	}

	private void resetValidation(TextInputControl textInputControl)
	{
		if (textInputControl instanceof JFXTextFieldWrapper) {
			((JFXTextFieldWrapper) textInputControl).resetValidation();
		} else if (textInputControl instanceof JFXTextAreaWrapper) {
			((JFXTextAreaWrapper) textInputControl).resetValidation();
		}
	}

	public ChangeInformation getChangeInformation()
	{
		return changeInformation;
	}

	public void setChangeInformation(ChangeInformation currentInformation)
	{
		this.changeInformation = currentInformation;
		textInputControlList().forEach(field -> {
			if (field instanceof JFXPristine) {
				((JFXPristine) field).setPristine(true);
			}
		});
	}
}
