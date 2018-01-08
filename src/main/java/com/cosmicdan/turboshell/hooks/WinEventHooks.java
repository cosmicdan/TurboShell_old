package com.cosmicdan.turboshell.hooks;

import com.cosmicdan.turboshell.winapi.SetWinEventHook;
import com.cosmicdan.turboshell.winapi.User32Ex;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WinEventHooks {
	// Following "Initialization-on-demand holder idiom"
	private final WinEventHookThread THREAD;

	private WinEventHooks() {
		THREAD = new WinEventHookThread();
	}

	private static class LazyHolder {
		static final WinEventHooks INSTANCE = new WinEventHooks();
	}

	public static WinEventHooks getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void start() {
		THREAD.start();
	}

	private static class WinEventHookThread extends Thread {
		@Override
		public void run() {
			WinUser.WinEventProc callback = new MyWinEventProc();

			SetWinEventHook hookLocationOrNameChange = new SetWinEventHook(
					SetWinEventHook.EVENT_OBJECT_LOCATIONCHANGE,
					SetWinEventHook.EVENT_OBJECT_NAMECHANGE,
					callback
			);
			SetWinEventHook hookForegroundChange = new SetWinEventHook(
					SetWinEventHook.EVENT_SYSTEM_FOREGROUND,
					SetWinEventHook.EVENT_SYSTEM_FOREGROUND,
					callback
			);

			//log.info("SetWinEventHook result =" + winEventHook.getResult().toString());

			//log.info("Message loop started...");
			int result;
			WinUser.MSG msg = new WinUser.MSG();
			result = -1;
			while (0 != result) {
				result = User32Ex.INSTANCE.GetMessageW(msg, null, 0, 0);
				if (result == -1) {
					//System.err.println("error in get message");
					break;
				} else {
					//System.err.println("got message");
					User32.INSTANCE.TranslateMessage(msg);
					User32.INSTANCE.DispatchMessage(msg);
				}
			}
			// no need to optimize this with a direct mapping
			User32.INSTANCE.UnhookWinEvent(hookLocationOrNameChange.getResult());
			User32.INSTANCE.UnhookWinEvent(hookForegroundChange.getResult());
		}

		private class MyWinEventProc implements WinUser.WinEventProc {
			/* idObject values */
			/** The window itself rather than a child object */
			static final long OBJID_WINDOW = 0x00000000;

			/* WINDOWPLACEMENT flags */
			static final int SW_SHOWNORMAL = 1;
			static final int SW_MAXIMIZE = 3;


			public final WinUser.WINDOWPLACEMENT WindowPlacementStruct = new WinUser.WINDOWPLACEMENT();

			@Override
			public void callback(WinNT.HANDLE hWinEventHook,
								 WinDef.DWORD event,
								 WinDef.HWND hwnd,
								 WinDef.LONG idObject,
								 WinDef.LONG idChild,
								 WinDef.DWORD dwEventThread,
								 WinDef.DWORD dwmsEventTime) {
				if (OBJID_WINDOW == idObject.longValue() && isWindowInteresting(hwnd)) {
					//noinspection NumericCastThatLosesPrecision,SwitchStatement
					switch ((int) event.longValue()) {
						case SetWinEventHook.EVENT_SYSTEM_FOREGROUND:
							log.info("New foreground window...");
							checkWindowMaximized(hwnd);
							break;
						case SetWinEventHook.EVENT_OBJECT_LOCATIONCHANGE:
							// window position changed - check if it is maximized or restored
							checkWindowMaximized(hwnd);
							break;
						case SetWinEventHook.EVENT_OBJECT_NAMECHANGE:
							// window name changed
							break;
						default:
							log.warn("WinEventProc callback somehow got an unknown event: " + Long.toHexString(event.longValue()));
							break;
					}
				}
			}

			private void checkWindowMaximized(WinDef.HWND hwnd) {
				if (User32.INSTANCE.GetWindowPlacement(hwnd, WindowPlacementStruct).booleanValue()) {
					if (SW_MAXIMIZE == (WindowPlacementStruct.showCmd & SW_MAXIMIZE)) {
						log.info("Maximized!");
					} else if (SW_SHOWNORMAL == (WindowPlacementStruct.showCmd & SW_SHOWNORMAL)) {
						log.info("Restored!");
					}
				}
			}

			private boolean isWindowInteresting(WinDef.HWND hwnd) {
				boolean isInteresting = false;
				// TODO: native these things
				int styleFlags = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_STYLE);
				if (WinUser.WS_VISIBLE == (styleFlags & WinUser.WS_VISIBLE)) {
					// window is visible...
					if (WinUser.WS_CAPTION == (styleFlags & WinUser.WS_CAPTION)) {
						// ...and has WS_CAPTION style. We originally checked for WS_SYSMENU style, but some windows don't have this style (e.g. Discord).
						isInteresting = true;
					}
				}
				return isInteresting;
			}
		}
	}
}
