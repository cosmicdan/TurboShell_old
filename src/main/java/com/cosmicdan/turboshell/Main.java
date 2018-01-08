package com.cosmicdan.turboshell;

import com.cosmicdan.turboshell.gui.TurboBar;
import com.cosmicdan.turboshell.hooks.WinEventHooks;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
	private static Thread sTurboBarThread;

	public static void main(String[] args) {
		log.info("Startup...");

		TurboBar turboBar = new TurboBar();
		sTurboBarThread = new Thread(turboBar);
		sTurboBarThread.start();

		log.info("Starting WinEventHooks...");
		WinEventHooks.getInstance().start();
	}
}