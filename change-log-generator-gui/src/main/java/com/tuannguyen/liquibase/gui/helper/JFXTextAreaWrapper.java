package com.tuannguyen.liquibase.gui.helper;

import com.jfoenix.controls.JFXTextArea;
import javafx.beans.property.BooleanProperty;

public class JFXTextAreaWrapper extends JFXTextArea implements JFXPristine {
	private com.tuannguyen.liquibase.gui.helper.PristineBehavior pristineBehavior;

	public JFXTextAreaWrapper() {
		this.pristineBehavior = new PristineBehavior(this);
	}

	@Override
	public BooleanProperty pristine() {
		return pristineBehavior.pristine();
	}

	@Override
	public boolean isPristine() {
		return pristineBehavior.isPristine();
	}

	@Override
	public void setPristine(boolean pristine) {
		pristineBehavior.setPristine(pristine);
	}
}
