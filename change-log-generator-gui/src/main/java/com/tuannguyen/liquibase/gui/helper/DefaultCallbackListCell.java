package com.tuannguyen.liquibase.gui.helper;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public abstract class DefaultCallbackListCell<T> implements Callback<ListView<T>, ListCell<T>> {
	@Override
	public ListCell<T> call(ListView<T> listView) {
		return new ListCell<T>() {
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setGraphic(null);
				} else {
					setText(getTitle(item));
				}
				this.setVisible(item != null || !empty);
			}
		};
	}

	public abstract String getTitle(T item);
}
