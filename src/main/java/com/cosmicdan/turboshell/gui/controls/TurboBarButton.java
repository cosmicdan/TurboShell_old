package com.cosmicdan.turboshell.gui.controls;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TurboBarButton extends Button {
	private final ImageView[] mImageViews;

	public TurboBarButton(String text, String imageResourcePath) {
		this(text, new String[] {imageResourcePath});
	}

	@SuppressWarnings("ObjectAllocationInLoop")
	public TurboBarButton(String text, String[] imageResourcePaths) {
		super(text);
		mImageViews = new ImageView[imageResourcePaths.length];
		for (int i = 0; i < mImageViews.length; i++) {
			final Image newImage = new Image(getClass().getResource(imageResourcePaths[i]).toExternalForm());
			mImageViews[i] = new ImageView(newImage);
		}
		this.setPrefHeight(mImageViews[0].getImage().getHeight());
		this.setGraphic(mImageViews[0]);
	}

	public ImageView getImage(int index) {
		this.setGraphic(mImageViews[index]);
		return mImageViews[index];
	}
}
