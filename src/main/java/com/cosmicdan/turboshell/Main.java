package com.cosmicdan.turboshell;

import com.cosmicdan.turboshell.gui.TurboBar;
import com.cosmicdan.turboshell.hooks.WinEventHooks;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
	private static Thread sTurboBarThread;
	private static Thread sWatcherThread;

	public static void main(String[] args) {
		log.info("Startup...");

		TurboBar turboBar = new TurboBar();
		sTurboBarThread = new Thread(turboBar);
		sTurboBarThread.start();
		RuntimeLoop watcherThread = new RuntimeLoop();
		sWatcherThread = new Thread(watcherThread);
		
		WinEventHooks.getInstance().start();
	}
}