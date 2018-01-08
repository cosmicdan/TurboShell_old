package com.cosmicdan.turboshell.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

public class TurboBarModel {
	private final IntegerProperty mCtrlResizeGraphicIndex = new SimpleIntegerProperty();

	/*
	private final IntegerProperty x = new SimpleIntegerProperty();
	private final IntegerProperty y = new SimpleIntegerProperty();
	private final ReadOnlyIntegerWrapper sum = new ReadOnlyIntegerWrapper();
	*/

	public TurboBarModel() {
		//sum.bind(x.add(y));
	}

	public final IntegerProperty ctrlResizeGraphicIndexProp() {
		return mCtrlResizeGraphicIndex;
	}

	public final int getCtrlResizeGraphicIndex() {
		return mCtrlResizeGraphicIndex.get();
	}

	public final void setCtrlResizeGraphicIndex(final int newValue) {
		mCtrlResizeGraphicIndex.set(newValue);
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