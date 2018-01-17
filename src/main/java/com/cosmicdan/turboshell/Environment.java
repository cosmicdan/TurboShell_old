package com.cosmicdan.turboshell;

import com.cosmicdan.turboshell.gui.TurboBar;
import com.cosmicdan.turboshell.util.SizedStack;
import com.cosmicdan.turboshell.winapi.SystemParametersInfo;
import com.cosmicdan.turboshell.winapi.User32Ex;
import com.cosmicdan.turboshell.winapi.WinUser;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Environment {
	private final Object mHWndActiveLock = new Object();
	private final Object mHWndDesktopLock = new Object();

	private SizedStack<WinDef.HWND> mHWndActiveStack = new SizedStack<>(8); //TODO: Offload stack size to config
	private WinDef.HWND mHWndDesktop;

	/********** "Initialization-on-demand holder" singleton pattern **********/
	public static Environment getInstance() { return LazyHolder.INSTANCE; }
	private Environment() {}
	private static class LazyHolder { static final Environment INSTANCE = new Environment();}
	/*************************************************************************/

	public void addActiveHwnd(WinDef.HWND hWnd) {
		synchronized(mHWndActiveLock) {
			mHWndActiveStack.push(hWnd);
		}
	}

	public WinDef.HWND getLastActiveHwnd() {
		synchronized(mHWndActiveLock) {
			// TODO: Use a parallel stack of window titles. Do a FindWindow call on the hwnd to ensure it also matches. This will solve any chance of hwnd recycling causing false matches.
			WinDef.HWND lastActive;
			if (mHWndActiveStack.isEmpty())
				return null;
			while (true) {
				lastActive = mHWndActiveStack.peek();
				if (User32Ex.INSTANCE.IsWindow(lastActive))
					break;
				else
					mHWndActiveStack.pop();
			}
			//log.info(mHWndActiveStack);
			return lastActive;
		}
	}

	public void setDesktopHwnd(WinDef.HWND hWnd) {
		synchronized(mHWndDesktopLock) {
			mHWndDesktop = hWnd;
		}
	}

	public WinDef.HWND getDesktopHwnd() {
		synchronized(mHWndDesktopLock) {
			return mHWndDesktop;
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

	public void doFullscreenCheck() {
		boolean setVisible = true;
		WinDef.HWND lastActiveWindow = getLastActiveHwnd();
		WinDef.RECT rectActive = new WinDef.RECT();
		if (null != lastActiveWindow && User32Ex.INSTANCE.GetWindowRect(getLastActiveHwnd(), rectActive)) {
			int screenWidth = User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN);
			int screenHeight = User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN);
			if ((screenWidth <= rectActive.right - rectActive.left) &&
					(screenHeight <= rectActive.bottom - rectActive.top)) {
				// set hidden
				setVisible = false;
			}
		}
		TurboBar.setVisible(setVisible);


		//log.info(screenWidth + "x" + screenHeight);

		// update the Desktop handle and rectangle (just in case)
		/*
		WinDef.HWND desktopHandle = User32Ex.INSTANCE.GetDesktopWindow();
		setDesktopHwnd(desktopHandle);

		log.info(desktopHandle.toString());
		if (User32Ex.INSTANCE.GetWindowRect(desktopHandle, rectDesktop)) {
			log.info("2");
			if (User32Ex.INSTANCE.GetWindowRect(getLastActiveHwnd(), rectActive)) {
				log.info("3");
				if (rectActive.left >= rectDesktop.left &&
						rectActive.top >= rectDesktop.top &&
						rectActive.right >= rectDesktop.right &&
						rectActive.bottom >= rectDesktop.bottom) {
					// set hidden
					log.info("Hide TurboBar!");
				} else {
					log.info("Show TurboBar!");
				}
			}
		}
		*/


			/*
			// check if active window is fullscreen and (un)hide the turbobar
			if (User32Ex.INSTANCE.GetWindowRect(Environment.getInstance().addActiveHwnd(), rectActive)) {
				if (User32Ex.INSTANCE.GetWindowRect(User32Ex.INSTANCE.GetDesktopWindow(), rectDesktop)) {
					if (rectActive.left == rectDesktop.left &&
							rectActive.top == rectDesktop.top &&
							rectActive.right == rectDesktop.right &&
							rectActive.bottom == rectDesktop.bottom) {
						// set hidden
					}
				}
			}
			*/
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
