package com.cosmicdan.turboshell.winapi;

import com.sun.jna.platform.win32.WinDef;

/**
 * A data holder/helper for working with GetWindowLong's GWL_STYLE resulting style flags
 */
public class GetWindowLongStyle {
	private final int styleFlags;

	public GetWindowLongStyle(WinDef.HWND hWnd) {
		styleFlags = User32Ex.INSTANCE.GetWindowLongW(hWnd, WinUser.GWL_STYLE);
	}

	/**
	 * Check if the window style consists of a title bar. It probably has a close button too.
	 */
	public boolean hasTitleBar() {
		return WinUser.WS_CAPTION == (styleFlags & WinUser.WS_CAPTION);
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
