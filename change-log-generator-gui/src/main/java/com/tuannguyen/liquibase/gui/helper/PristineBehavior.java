package com.tuannguyen.liquibase.gui.helper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextInputControl;

public class PristineBehavior {
	private BooleanProperty pristine;
	private TextInputControl textInputControl;

	public PristineBehavior(TextInputControl textInputControl) {
		this.textInputControl = textInputControl;
		this.textInputControl.textProperty().addListener((observable, oldValue, newValue) -> {
			if (isPristine() && newValue != null && !newValue.isEmpty()) {
				setPristine(false);
			}
		});
	}


	boolean isPristine() {
		return pristine().get();
	}

	BooleanProperty pristine() {
		if (pristine == null) {
			pristine = new SimpleBooleanProperty(true);
		}
		return pristine;
	}

	void setPristine(boolean pristine) {
		this.pristine()
		    .set(pristine);
	}
}
