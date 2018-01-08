package com.cosmicdan.turboshell.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableIntegerValue;
import lombok.AccessLevel;
import lombok.Getter;

public class TurboBarModel {
	@Getter
	private final IntegerProperty ctrlResizeGraphicIndex = new SimpleIntegerProperty();

	/*
	private final IntegerProperty x = new SimpleIntegerProperty();
	private final IntegerProperty y = new SimpleIntegerProperty();
	private final ReadOnlyIntegerWrapper sum = new ReadOnlyIntegerWrapper();
	*/

	public TurboBarModel() {
		//sum.bind(x.add(y));
	}

	public final void setCtrlResizeGraphicIndex(final int newValue) {
		this.ctrlResizeGraphicIndex.set(newValue);
	}

	public final boolean isCtrlResizeMaximize() {
		return 0 != this.ctrlResizeGraphicIndex.get();
	}

	/*
	public final javafx.beans.property.ReadOnlyIntegerProperty sumProperty() {
		return this.sum.getReadOnlyProperty();
	}

	public final int getSum() {
		return this.sumProperty().get();
	}
	*/


}