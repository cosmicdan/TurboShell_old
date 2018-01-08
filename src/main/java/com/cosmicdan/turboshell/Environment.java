package com.cosmicdan.turboshell;

import com.cosmicdan.turboshell.winapi.SystemParametersInfo;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Environment {
	// Following "Initialization-on-demand holder idiom"
	private Environment() {}

	private static class LazyHolder {
		static final Environment INSTANCE = new Environment();
	}

	public static Environment getInstance() {
		return LazyHolder.INSTANCE;
	}

	// -------------------------------------------------

	private volatile WinDef.HWND mHWnd;
	private Object mHWndLock = new Object();

	public void setLastActiveHwnd(WinDef.HWND hWnd) {
		synchronized(mHWndLock) {
			mHWnd = hWnd;
		}
	}

	public WinDef.HWND getLastActiveHwnd() {
		synchronized(mHWndLock) {
			return mHWnd;
		}
	}

	public void adjustWorkArea(int top) {
		WinDef.RECT workArea = getWorkArea();
		setWorkArea(workArea.left, top, workArea.right, workArea.bottom);
	}

	/**
	 * Get the work area starting x-pos and width
	 * @return An int[] of two members:<br/>
	 * 		0 = Start position of work area (usually 0)<br/>
	 * 		1 = Width of work area (remaining)
	 */
	public int[] getWorkAreaStartAndWidth() {
		WinDef.RECT workArea = getWorkArea();
		return new int[] {workArea.left, workArea.right - workArea.left};
	}

	/********** Internal helpers **********/

	private WinDef.RECT getWorkArea() {
		int dwFlags = WinUser.MONITOR_DEFAULTTOPRIMARY;
		WinDef.POINT.ByValue pt = new WinDef.POINT.ByValue(0, 0);
		WinUser.HMONITOR hwMonitor = User32.INSTANCE.MonitorFromPoint(pt, dwFlags);
		WinUser.MONITORINFO mainMonitorInfo = new WinUser.MONITORINFO();
		User32.INSTANCE.GetMonitorInfo(hwMonitor, mainMonitorInfo);
		return mainMonitorInfo.rcWork;
	}

	private boolean setWorkArea(int left, int top, int right, int bottom) {
		WinDef.RECT rect = new WinDef.RECT();
		rect.left = left;
		rect.top = top;
		rect.right = right;
		rect.bottom = bottom;
		boolean result = new SystemParametersInfo(SystemParametersInfo.SPI_SETWORKAREA, 0, rect, 0x0).isSuccess();
		//return new SystemParametersInfo(SystemParametersInfo.SPI_SETWORKAREA, 0, rect, 0).isSuccess();
		if (result) {
			// broadcast system-wide so windows reposition themselves
			int WM_SETTINGCHANGE = 0x1a;
			long wParam_SPI_SETNONCLIENTMETRICS = 0x2a;
			final WinDef.DWORDByReference success = new WinDef.DWORDByReference();
			User32.INSTANCE.SendMessageTimeout(
					WinUser.HWND_BROADCAST,
					WM_SETTINGCHANGE,
					new WinDef.WPARAM(wParam_SPI_SETNONCLIENTMETRICS),
					new WinDef.LPARAM(0),
					WinUser.SMTO_ABORTIFHUNG,
					10000,
					success);
		}
		return result;
	}
}
