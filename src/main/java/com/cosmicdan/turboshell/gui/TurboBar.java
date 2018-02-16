package com.cosmicdan.turboshell.gui;

import com.cosmicdan.turboshell.Environment;
import com.cosmicdan.turboshell.winapi.User32Ex;
import com.cosmicdan.turboshell.winapi.WinUser;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TurboBar extends AbstractRunnableGui {
	private static final int TURBOBAR_HEIGHT = 23;
	private static final String WINDOW_NAME = "TurboShell's TurboBar";
	private static final WinDef.HWND HWND_TOP = new WinDef.HWND(Pointer.createConstant(0));
	private static final int uFlagsVisible = WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOACTIVATE | WinUser.SWP_SHOWWINDOW;
	private static final int uFlagsHidden = WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOACTIVATE | WinUser.SWP_HIDEWINDOW;
	private static final int WM_USER_APPBAR = WinUser.WM_USER + 808;

	private static final Object mControllerLock = new Object();
	private static TurboBarController CONTROLLER;
	private static WinDef.HWND hWnd;
	private static boolean isVisible = false;

	public TurboBar() {
		super(App.class);
		log.info("Setting up...");
	}

	public static TurboBarController getController() {
		synchronized (mControllerLock) {
			return CONTROLLER;
		}
	}

	public static void setVisible(boolean visible) {
		if (visible == isVisible)
			return; // no change, don't do anything
		User32.INSTANCE.SetWindowPos(hWnd, HWND_TOP, 0, 0, 0, 0, visible ? uFlagsVisible : uFlagsHidden);
		isVisible = visible;
	}

	@SuppressWarnings("WeakerAccess")
	public static class App extends Application {
		private WinUser.WindowProc wndProcCallback;
		private BaseTSD.LONG_PTR orgWndProc;

		@Override
		public void start(Stage primaryStage) {
			log.info("TurboBar GUI starting...");

			TurboBarModel model = new TurboBarModel();
			CONTROLLER = new TurboBarController(model);
			TurboBarView view = new TurboBarView(model, CONTROLLER);

			int[] posAndWidth = Environment.getInstance().getWorkAreaStartAndWidth();
			Scene scene = new Scene(view.asParent(), posAndWidth[1], TURBOBAR_HEIGHT);
			scene.getStylesheets().add(getClass().getResource("TurboBar.css").toExternalForm());
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setScene(scene);
			primaryStage.setTitle(WINDOW_NAME);
			// move the turbobar to the top edge
			primaryStage.setX(posAndWidth[0]);
			primaryStage.setY(0);
			primaryStage.setAlwaysOnTop(true);

			// TODO: Get current activated window, then re-activate it after this show and visible
			primaryStage.show();
			// We need to set extended style *after* the GUI is shown
			hWnd = User32.INSTANCE.FindWindow(null, WINDOW_NAME);
			final int WS_EX_TOOLWINDOW = 0x00000080;
			final int WS_EX_NOACTIVATE = 0x08000000;
			User32.INSTANCE.SetWindowLongPtr(hWnd, WinUser.GWL_EXSTYLE, Pointer.createConstant(WS_EX_TOOLWINDOW | WS_EX_NOACTIVATE));
			setVisible(true);
			// appbar add
			final ShellAPI.APPBARDATA appBarData = new ShellAPI.APPBARDATA.ByReference();
			appBarData.cbSize.setValue(appBarData.size());
			appBarData.hWnd = hWnd;
			appBarData.uCallbackMessage.setValue(WM_USER_APPBAR);
			WinDef.UINT_PTR result = Shell32.INSTANCE.SHAppBarMessage(new WinDef.DWORD(ShellAPI.ABM_NEW), appBarData);
			if (result.intValue() > 0) {
				// appbar set position
				appBarData.uEdge.setValue(ShellAPI.ABE_TOP);
				appBarData.rc.top = 0;
				appBarData.rc.left = posAndWidth[0];
				appBarData.rc.bottom = TURBOBAR_HEIGHT;
				appBarData.rc.right = posAndWidth[1];
				result = Shell32.INSTANCE.SHAppBarMessage(new WinDef.DWORD(ShellAPI.ABM_SETPOS), appBarData);
				// TODO: throw error if result == 0
			} else {
				// TODO: throw error
			}


			orgWndProc = User32.INSTANCE.GetWindowLongPtr(hWnd, User32.GWL_WNDPROC);
			wndProcCallback = new WinUser.WindowProc() {
				@Override
				public WinDef.LRESULT callback(WinDef.HWND hWnd, int uMsg, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
					if (WM_USER_APPBAR == uMsg ) {
						//log.info(uMsg + "; " + wParam.intValue() + "; " + lParam.intValue());
						switch (wParam.intValue()) {
							case 2: // ABN_FULLSCREENAPP
								TurboBar.setVisible(lParam.intValue() == 0); // 0 == foreground window exited fullscreen
								break;
						}
					}

					// Chain call the original window procedure
					return User32Ex.INSTANCE.CallWindowProcW(orgWndProc.toPointer(), hWnd, uMsg, wParam, lParam);
				}
			};

			//Set the WndProc function to use our callback listener instead of the default one.
			BaseTSD.LONG_PTR result2 = User32Ex.INSTANCE.SetWindowLongPtrW(hWnd, User32.GWL_WNDPROC, wndProcCallback);




			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				log.info("TurboBar GUI stopping...");

				// unregister appbar
				Shell32.INSTANCE.SHAppBarMessage(new WinDef.DWORD(ShellAPI.ABM_REMOVE), appBarData);
			}));

		}
	}
}
