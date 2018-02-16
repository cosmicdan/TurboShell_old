package com.cosmicdan.turboshell.winapi;

import com.sun.jna.platform.win32.WinDef;
import lombok.extern.log4j.Log4j2;

/**
 * A wrapper around GetWindowLongPtr for GWL_STYLE/GWL_EXSTYLE and some other window related properties
 */
@Log4j2
public class WindowProps {
	private final WinDef.HWND hWnd;
	private final long styleFlags;
	private final long styleExFlags;

	public WindowProps(WinDef.HWND hWnd) {
		this.hWnd = hWnd;
		styleFlags = User32Ex.INSTANCE.GetWindowLongPtrW(hWnd, WinUser.GWL_STYLE).longValue();
		styleExFlags = User32Ex.INSTANCE.GetWindowLongPtrW(hWnd, WinUser.GWL_EXSTYLE).longValue();
	}

	/**
	 * Determine if a window is "real". We consider a window real if it exists on the taskbar.
	 * References:
	 * https://stackoverflow.com/questions/16973995/
	 * https://stackoverflow.com/questions/2262726/
	 */
	public boolean isReal() {
		if (hWnd.equals(User32Ex.INSTANCE.GetAncestor(hWnd, WinUser.GA_ROOT))) {
			if (WinUser.WS_EX_APPWINDOW == (styleExFlags & WinUser.WS_EX_APPWINDOW))
				return true;
			if (User32Ex.INSTANCE.GetWindow(hWnd, WinUser.GW_OWNER) == null) {
				if (WinUser.WS_EX_NOACTIVATE == (styleExFlags & WinUser.WS_EX_NOACTIVATE))
					return false;
				if (WinUser.WS_EX_TOOLWINDOW == (styleExFlags & WinUser.WS_EX_TOOLWINDOW))
					return false;
				return true;
			}
		}

		return false;
	}

	public boolean canResize() {
		return WinUser.WS_SIZEBOX == (styleFlags & WinUser.WS_SIZEBOX);
	}

	public boolean hasResizeButton() {
		return WinUser.WS_MAXIMIZEBOX == (styleFlags & WinUser.WS_MAXIMIZEBOX);
	}

	public boolean hasMinimizeButton() {
		return WinUser.WS_MINIMIZEBOX == (styleFlags & WinUser.WS_MINIMIZEBOX);
	}
}
