package com.cosmicdan.turboshell;

import lombok.Setter;

/**
 * Runtime loop for reacting to situations that can't be reliably hooked
 */
class RuntimeLoop implements Runnable {
	@Setter
	private volatile boolean shouldStop = false;

	@Override
	public void run() {
		while (true) {
			// nothing yet

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (shouldStop)
				break;
		}
	}
}
