package com.cosmicdan.turboshell.gui;

import com.cosmicdan.turboshell.Environment;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TurboBar extends AbstractRunnableGui {
	public static final int TURBOBAR_HEIGHT = 23;
	public static final String WINDOW_NAME = "TurboShell's TurboBar";

	private static final Object mViewLock = new Object();
	public static TurboBarView VIEW;

	public TurboBar() {
		super(App.class);
		log.info("Setting up...");
	}

	public static TurboBarView getView() {
		synchronized (mViewLock) {
			return VIEW;
		}
	}

	public static class App extends Application {
		@Override
		public void start(Stage primaryStage) throws Exception {
			log.info("TurboBar GUI starting...");

			TurboBarModel model = new TurboBarModel();
			TurboBarController controller = new TurboBarController(model);
			VIEW = new TurboBarView(model, controller);

			int[] posAndWidth = Environment.getInstance().getWorkAreaStartAndWidth();
			Scene scene = new Scene(VIEW.asParent(), posAndWidth[1], TURBOBAR_HEIGHT);
			scene.getStylesheets().add(getClass().getResource("TurboBar.css").toExternalForm());
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setScene(scene);
			primaryStage.setTitle(WINDOW_NAME);

			// set new workarea
			Environment.getInstance().adjustWorkArea(TURBOBAR_HEIGHT);
			// move the turbobar to the top edge
			primaryStage.setX(posAndWidth[0]);
			primaryStage.setY(0);

			primaryStage.show();

			// set extended style *after* the GUI is shown (such workarounds are to be expected when dealing with java(fx) abstraction)
			WinDef.HWND hWnd = User32.INSTANCE.FindWindow(null, WINDOW_NAME);
			//int WS_EX_TOOLWINDOW = 0x00000080; // redundant?
			int WS_EX_NOACTIVATE = 0x08000000;

			WinDef.HWND HWND_TOPMOST = new WinDef.HWND(Pointer.createConstant(-1));

			User32.INSTANCE.SetWindowLong(hWnd, WinUser.GWL_EXSTYLE, WS_EX_NOACTIVATE);
			// WS_EX_TOPMOST doesn't work for already-existing windows; so use SetWindowPos instead...
			User32.INSTANCE.SetWindowPos(hWnd, HWND_TOPMOST, 0, 0, 0, 0, WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE);

			// TODO: Prevent minimize on 'Show Desktop' action. Neither of these work...
			//User32.INSTANCE.SetWindowLong(hWnd, WinUser.GWL_HWNDPARENT, (int) Pointer.nativeValue(User32.INSTANCE.GetDesktopWindow().getPointer()));
			//User32.INSTANCE.SetParent(hWnd, User32.INSTANCE.GetDesktopWindow());

		}

		@Override
		public void stop() throws Exception {
			log.info("TurboBar GUI stopping...");
			// TODO: Check if 'Restore workarea on quit' option is disabled
			Environment.getInstance().adjustWorkArea(0);
		}
	}
}
