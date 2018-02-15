package com.cosmicdan.turboshell;

/**
 * Runtime loop for reacting to situations that can't be reliably hooked
 */
public class RuntimeLoop implements Runnable {
	@Override
	public void run() {
		while (true) {
			// nothing yet

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
