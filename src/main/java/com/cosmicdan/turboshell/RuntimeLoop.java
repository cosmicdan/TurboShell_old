package com.cosmicdan.turboshell;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.log4j.Log4j2;

/**
 * Runtime loop for reacting to situations that can't be reliably hooked
 */
@Log4j2
class RuntimeLoop implements Runnable {
	private volatile boolean shouldStop = false;

	public void stop() {
		log.info("Stopping RuntimeLoop...");
		shouldStop = true;
	}

	@Override
	public void run() {
		log.info("Starting...");

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Stopping RuntimeLoop...");
			shouldStop = true;
		}));

		while (!shouldStop) {
			// nothing yet

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
