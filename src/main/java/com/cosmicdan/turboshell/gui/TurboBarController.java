package com.cosmicdan.turboshell.gui;

import com.cosmicdan.turboshell.Environment;
import com.cosmicdan.turboshell.winapi.User32Ex;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TurboBarController {

	private final TurboBarModel mModel;

	public TurboBarController(TurboBarModel model) {
		mModel = model ;
	}

	public void updateResizeButton(boolean isMaximized) {
		Platform.runLater(() -> mModel.setCtrlResizeGraphicIndex(isMaximized ? 0 : 1));
	}

	public void eventResizeButtonClick(MouseEvent event) {
		if (isPrimaryButton(event)) {
			if (mModel.isCtrlResizeMaximize()) {
				User32Ex.INSTANCE.ShowWindowAsync(Environment.getInstance().getLastActiveHwnd(), WinUser.SW_MAXIMIZE);
			} else {
				User32Ex.INSTANCE.ShowWindowAsync(Environment.getInstance().getLastActiveHwnd(), WinUser.SW_RESTORE);
			}
		}
	}

	private boolean isPrimaryButton(MouseEvent event) {
		return MouseButton.PRIMARY == event.getButton();
	}
}