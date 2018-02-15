package com.cosmicdan.turboshell.gui;

import com.cosmicdan.turboshell.Environment;
import com.cosmicdan.turboshell.winapi.User32Ex;
import com.cosmicdan.turboshell.winapi.WinUser;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
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

	public void setSysButtonEnabled(int index, boolean enabled) {
		Platform.runLater(() -> mModel.setCtrlSysbtnEnabled(index, enabled));
	}

	public void eventCloseButtonClick(MouseEvent event) {
		if (isPrimaryButton(event)) {
			WinDef.HWND lastActive = Environment.getInstance().getLastActiveHwnd();
			if (null != lastActive) {
				// TODO: CRASHES! Issue reported - https://github.com/java-native-access/jna/issues/905
				//User32Ex.INSTANCE.PostMessageW(Environment.getInstance().getLastActiveHwnd(), WinUser.WM_CLOSE, null, null);
				// workaround - interface mapping
				User32.INSTANCE.PostMessage(lastActive, WinUser.WM_CLOSE, null, null);
			}
		}
	}

	public void eventCloseButtonHeld(MouseEvent event) {
		WinDef.HWND lastActive = Environment.getInstance().getLastActiveHwnd();
		if (null != lastActive) {
			if (isPrimaryButton(event)) { // regular force-quit
				// TODO: Update if/when native binding PostMessage is fixed
				User32.INSTANCE.PostMessage(lastActive, WinUser.WM_QUIT, null, null);
				log.info("Sent WM_QUIT message to hWnd " + lastActive);
			} else { // force kill
				final IntByReference pid = new IntByReference();
				User32.INSTANCE.GetWindowThreadProcessId(lastActive, pid);
				final WinNT.HANDLE hProcess = Kernel32.INSTANCE.OpenProcess(Kernel32.PROCESS_TERMINATE, false, pid.getValue());
				final boolean result = Kernel32.INSTANCE.TerminateProcess(hProcess, 0);
				log.info("Called TerminateProcess on hWnd " + lastActive +
						"; result = " + result + " (GetLastError = " + Kernel32.INSTANCE.GetLastError() + ")");
			}
		}
	}

	public void eventResizeButtonClick(MouseEvent event) {
		if (isPrimaryButton(event)) {
			WinDef.HWND lastActive = Environment.getInstance().getLastActiveHwnd();
			if (null != lastActive) {
				if (mModel.isCtrlResizeMaximize()) {
					User32Ex.INSTANCE.ShowWindowAsync(Environment.getInstance().getLastActiveHwnd(), WinUser.SW_MAXIMIZE);
				} else {
					User32Ex.INSTANCE.ShowWindowAsync(Environment.getInstance().getLastActiveHwnd(), WinUser.SW_RESTORE);
				}
			}
		}
	}

	public void eventMinimizeButtonClick(MouseEvent event) {
		if (isPrimaryButton(event)) {
			WinDef.HWND lastActive = Environment.getInstance().getLastActiveHwnd();
			if (null != lastActive) {
				User32Ex.INSTANCE.ShowWindowAsync(lastActive, WinUser.SW_MINIMIZE);
			}
		}
	}

	private boolean isPrimaryButton(MouseEvent event) {
		return MouseButton.PRIMARY == event.getButton();
	}
}