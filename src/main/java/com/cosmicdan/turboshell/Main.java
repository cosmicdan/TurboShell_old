package com.cosmicdan.turboshell;

import com.cosmicdan.turboshell.gui.TurboBar;
import com.cosmicdan.turboshell.hooks.WinEventHooks;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

	public static void main(String[] args) {
		log.info("Startup...");

		TurboBar turboBar = new TurboBar();
		Thread sTurboBarThread = new Thread(turboBar);
		sTurboBarThread.start();
		RuntimeLoop runtimeLoop = new RuntimeLoop();
		Thread sWatcherThread = new Thread(runtimeLoop);
		sWatcherThread.start();
		
		WinEventHooks.getInstance().start();
	}
}