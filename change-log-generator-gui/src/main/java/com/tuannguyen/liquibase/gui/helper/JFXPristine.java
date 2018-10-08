package com.tuannguyen.liquibase.gui.helper;

import javafx.beans.property.BooleanProperty;

public interface JFXPristine {
	BooleanProperty pristine();
	boolean isPristine();
	void setPristine(boolean pristine);
}
