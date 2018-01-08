package com.cosmicdan.turboshell.gui;

import javafx.application.Platform;

public class TurboBarController {

	private final TurboBarModel model ;

	public TurboBarController(TurboBarModel model) {
		this.model = model ;
	}

	public void updateResizeButton(boolean isMaximized) {
		Platform.runLater(() -> model.setCtrlResizeGraphicIndex(isMaximized ? 0 : 1));
	}
}