package com.cosmicdan.turboshell;

import com.cosmicdan.turboshell.gui.TurboBar;
import com.cosmicdan.turboshell.hooks.WinEventHooks;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

	public static void main(String[] args) {
		log.info("Startup...");

		final TurboBar turboBar = new TurboBar();
		final Thread turboBarThread = new Thread(turboBar);
		turboBarThread.start();
		final RuntimeLoop runtimeLoop = new RuntimeLoop();
		final Thread runtimeLoopThread = new Thread(runtimeLoop);
		runtimeLoopThread.start();

		
		WinEventHooks.getInstance().start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Shutting down TurboShell...");
			// nothing to do here (yet)
		}));
	}
}