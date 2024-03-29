package com.cosmicdan.turboshell.gui;

import javafx.application.Application;
import lombok.extern.log4j.Log4j2;

@Log4j2
abstract class AbstractRunnableGui implements Runnable {
	private final Class<? extends Application> sApplicationClass;

	AbstractRunnableGui(Class<? extends Application> applicationClass) {
		sApplicationClass = applicationClass;
	}

	@Override
	public void run() {
		Application.launch(sApplicationClass);
	}
}
