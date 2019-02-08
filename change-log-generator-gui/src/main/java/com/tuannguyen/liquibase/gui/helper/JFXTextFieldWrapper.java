package com.tuannguyen.liquibase.gui.helper;

import com.jfoenix.controls.JFXTextField;

import javafx.beans.property.BooleanProperty;

public class JFXTextFieldWrapper extends JFXTextField implements JFXPristine
{
	private PristineBehavior pristineBehavior;

	public JFXTextFieldWrapper()
	{
		this.pristineBehavior = new PristineBehavior(this);
	}

	@Override
	public BooleanProperty pristine()
	{
		return pristineBehavior.pristine();
	}

	@Override
	public boolean isPristine()
	{
		return pristineBehavior.isPristine();
	}

	@Override
	public void setPristine(boolean pristine)
	{
		pristineBehavior.setPristine(pristine);
	}
}
