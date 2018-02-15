package com.cosmicdan.turboshell.gui;

import javafx.beans.property.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
class TurboBarModel {
	@Getter	private final IntegerProperty ctrlResizeGraphicIndex = new SimpleIntegerProperty();
	@Getter	private final BooleanProperty[] ctrlSysbtnEnabled = new SimpleBooleanProperty[3];

	/*
	private final IntegerProperty x = new SimpleIntegerProperty();
	private final IntegerProperty y = new SimpleIntegerProperty();
	private final ReadOnlyIntegerWrapper sum = new ReadOnlyIntegerWrapper();
	*/

	public TurboBarModel() {
		for (int i = 0; i < ctrlSysbtnEnabled.length; i++) {
			ctrlSysbtnEnabled[i] = new SimpleBooleanProperty();
		}
		//sum.bind(x.add(y));
	}

	public final void setCtrlResizeGraphicIndex(final int newValue) {
		this.ctrlResizeGraphicIndex.set(newValue);
	}

	public final void setCtrlSysbtnEnabled(final int index, final boolean enabled) {
		this.ctrlSysbtnEnabled[index].set(enabled);
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