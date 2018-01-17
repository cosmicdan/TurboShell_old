package com.cosmicdan.turboshell.hooks;

import com.cosmicdan.turboshell.Environment;
import com.cosmicdan.turboshell.gui.TurboBar;
import com.cosmicdan.turboshell.winapi.WindowProps;
import com.cosmicdan.turboshell.winapi.SetWinEventHook;
import com.cosmicdan.turboshell.winapi.User32Ex;
import com.cosmicdan.turboshell.winapi.WinUser;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
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
			log.info("Starting...");
			WinUser.WinEventProc callback = new MyWinEventProc();

			// TODO: Need a hook that detects privileged (???) window creation, e.g. Task Manager (ATM it is only detected if switched out then back in)

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


			/*
			// debug
			MyWinEventProcDebug callbackDebug = new MyWinEventProcDebug();
			SetWinEventHook hookAllDebug = new SetWinEventHook(
					SetWinEventHook.EVENT_MIN,
					SetWinEventHook.EVENT_SYSTEM_END,
					callbackDebug
			);
			*/


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
					User32Ex.INSTANCE.TranslateMessage(msg);
					User32Ex.INSTANCE.DispatchMessageW(msg);
				}
			}
			// no need to optimize this with a direct mapping
			User32.INSTANCE.UnhookWinEvent(hookLocationOrNameChange.getResult());
			User32.INSTANCE.UnhookWinEvent(hookForegroundChange.getResult());
		}

		/*
		private class MyWinEventProcDebug extends MyWinEventProc {
			@Override
			public void callback(WinNT.HANDLE hWinEventHook,
								 WinDef.DWORD event,
								 WinDef.HWND hwnd,
								 WinDef.LONG idObject,
								 WinDef.LONG idChild,
								 WinDef.DWORD dwEventThread,
								 WinDef.DWORD dwmsEventTime) {
				if ((OBJID_WINDOW == idObject.longValue())) {
					log.info("New window event from hWnd " + hwnd + ": " + Long.toHexString(event.longValue()) + ", isInteresting = " + isWindowInteresting(hwnd));
				}

			}
		}
		*/

		private class MyWinEventProc implements WinUser.WinEventProc {
			/* idObject values */
			/** Is a window itself, not a child object */
			static final long OBJID_WINDOW = 0x00000000;

			/* WINDOWPLACEMENT flags */
			//static final int SW_SHOWNORMAL = 1;
			static final int SW_MAXIMIZE = 3;

			private final WinUser.WINDOWPLACEMENT WindowPlacementStruct = new WinUser.WINDOWPLACEMENT();

			private WindowProps windowStyleData;
			private boolean canMaximizeOrRestore = false;
			//private WinDef.RECT rectActive = new WinDef.RECT();
			//private WinDef.RECT rectDesktop = new WinDef.RECT();

			@Override
			public void callback(WinNT.HANDLE hWinEventHook,
								 WinDef.DWORD event,
								 WinDef.HWND hWnd,
								 WinDef.LONG idObject,
								 WinDef.LONG idChild,
								 WinDef.DWORD dwEventThread,
								 WinDef.DWORD dwmsEventTime) {
				if (OBJID_WINDOW == idObject.longValue()) {
					windowStyleData = new WindowProps(hWnd);
					if (windowStyleData.isReal()) {
						//noinspection NumericCastThatLosesPrecision,SwitchStatement
						switch ((int) event.longValue()) {
							case SetWinEventHook.EVENT_SYSTEM_FOREGROUND:
								// new window brought to the front
								Environment.getInstance().addActiveHwnd(hWnd);
								refreshWindowResizeButton();
								refreshTitle();
								refreshSysbtnEnabledState();
								break;
							case SetWinEventHook.EVENT_OBJECT_LOCATIONCHANGE:
								// window position changed
								refreshWindowResizeButton();
								break;
							case SetWinEventHook.EVENT_OBJECT_NAMECHANGE:
								// window name changed
								refreshTitle();
								break;
							default:
								log.warn("WinEventProc callback somehow got an unknown event: " + Long.toHexString(event.longValue()));
								break;
						}
					}
					Environment.getInstance().doFullscreenCheck();
				}
			}

			void refreshWindowResizeButton() {
				if (windowStyleData.isReal()) {
					if (User32Ex.INSTANCE.GetWindowPlacement(windowStyleData.hWnd, WindowPlacementStruct).booleanValue()) { // returns false if it fails
						TurboBar.getController().updateResizeButton(SW_MAXIMIZE == (WindowPlacementStruct.showCmd & SW_MAXIMIZE));
					}
				}
			}

			void refreshSysbtnEnabledState() {
				// always keep close button enabled
				TurboBar.getController().setSysButtonEnabled(0, true);
				// determine if the window can be maximized or restored
				canMaximizeOrRestore = false;
				if (windowStyleData.isReal() && windowStyleData.canResize() && windowStyleData.hasResizeButton()) {
					//log.info("Resizable window has maximize sysbutton");
					canMaximizeOrRestore = true;
				} else {
					// this is not necessary (yet)
					/*
					// inspect the system menu, if possible
					WinDef.HMENU hMenu = User32Ex.INSTANCE.GetSystemMenu(hWnd, false);
					WinUser.MENUITEMINFO mii = new WinUser.MENUITEMINFO(WinUser.MENUITEMINFO.MIIM_STATE);
					// first check if RESTORE is enabled
					boolean result = User32Ex.INSTANCE.GetMenuItemInfoW(hMenu, WinUser.SC_RESTORE, false, mii);
					if (result) {
						if (WinUser.MENUITEMINFO.MFS_DISABLED == (mii.fState & WinUser.MENUITEMINFO.MFS_DISABLED)) {
							log.info("Restore is disabled");
						}
					}
					//log.info(Integer.toHexString(mii.fState.intValue()));
					//log.info("Result = " + result + "; " + Kernel32.INSTANCE.GetLastError());
					*/
				}
				TurboBar.getController().setSysButtonEnabled(1, canMaximizeOrRestore);

				// determine if the window can be minimized.
				// AFAIK, any window that appears on the taskbar can be minimized
				TurboBar.getController().setSysButtonEnabled(2, windowStyleData.isReal());
			}

			void refreshTitle() {
				final int titleLength = User32Ex.INSTANCE.GetWindowTextLengthW(windowStyleData.hWnd) + 1;
				final char[] title = new char[titleLength];
				final int length = User32Ex.INSTANCE.GetWindowTextW(windowStyleData.hWnd, title, title.length);
				String windowTitle = "[No title/process]";
				if (length > 0)
					windowTitle = new String(title);
				// TODO: else set process name to title
				log.info("New title: " + windowTitle);
			}
		}
	}
}
