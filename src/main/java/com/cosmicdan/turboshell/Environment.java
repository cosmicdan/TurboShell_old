package com.cosmicdan.turboshell;

import com.cosmicdan.turboshell.gui.TurboBar;
import com.cosmicdan.turboshell.util.SizedStack;
import com.cosmicdan.turboshell.winapi.SystemParametersInfo;
import com.cosmicdan.turboshell.winapi.User32Ex;
import com.cosmicdan.turboshell.winapi.WinUser;
import com.sun.istack.internal.Nullable;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Environment {
	/********** "Initialization-on-demand holder" singleton pattern **********/
	public static Environment getInstance() { return LazyHolder.INSTANCE; }
	private Environment() {}
	private static class LazyHolder { static final Environment INSTANCE = new Environment();}
	/*************************************************************************/

	private final Object mHWndActiveLock = new Object();

	private final SizedStack<WinDef.HWND> mHWndActiveStack = new SizedStack<>(8); //TODO: Offload stack size to config

	public void addActiveHwnd(WinDef.HWND hWnd) {
		synchronized(mHWndActiveLock) {
			mHWndActiveStack.push(hWnd);
		}
	}

	@Nullable
	public WinDef.HWND getLastActiveHwnd() {
		synchronized(mHWndActiveLock) {
			// TODO: Use a parallel stack of window titles. Do a FindWindow call on the hwnd to ensure it also matches. This will solve any chance of hwnd recycling causing false matches.
			WinDef.HWND lastActive = null;
			if (mHWndActiveStack.isEmpty())
				return null;
			while (true) {
				if (mHWndActiveStack.isEmpty())
					break;
				lastActive = mHWndActiveStack.peek();
				if (User32Ex.INSTANCE.IsWindow(lastActive) && User32Ex.INSTANCE.IsWindowVisible(lastActive))
					break;
				else
					// If the window not longer exists or isn't visible then we no longer want to keep it in the stack
					mHWndActiveStack.pop();
			}
			//log.info(mHWndActiveStack);
			return lastActive;
		}
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

	private WinDef.RECT getWorkArea() {
		int dwFlags = WinUser.MONITOR_DEFAULTTOPRIMARY;
		WinDef.POINT.ByValue pt = new WinDef.POINT.ByValue(0, 0);
		WinUser.HMONITOR hwMonitor = User32.INSTANCE.MonitorFromPoint(pt, dwFlags);
		WinUser.MONITORINFO mainMonitorInfo = new WinUser.MONITORINFO();
		User32.INSTANCE.GetMonitorInfo(hwMonitor, mainMonitorInfo);
		return mainMonitorInfo.rcWork;
	}

	public void doFullscreenCheck() { // TODO: Refactor to "isForegroundFullscreen" and move hiding logic to the bar controller instead
		boolean setVisible = true;
		WinDef.HWND lastActiveWindow = getLastActiveHwnd();
		WinDef.RECT rectActive = new WinDef.RECT();
		if (null != lastActiveWindow && User32Ex.INSTANCE.GetWindowRect(lastActiveWindow, rectActive)) {
			int screenWidth = User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN);
			int screenHeight = User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN);
			if ((screenWidth <= rectActive.right - rectActive.left) &&
					(screenHeight <= rectActive.bottom - rectActive.top)) {
				// set hidden
				setVisible = false;
			}
		}
		TurboBar.setVisible(setVisible);
	}
}
