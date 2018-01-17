package com.cosmicdan.turboshell.gui.animations;

import com.cosmicdan.turboshell.gui.controls.TurboBarButton;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KillCountdownProgress extends Transition {
	private ObjectProperty<Duration> duration;
	private static final Duration DEFAULT_DURATION = Duration.millis(400);

	private final TurboBarButton mCtrlCloseButton;
	private final String mColorHex;
	private final boolean mDoReverse;

	public final void setDuration(Duration value) {
		if ((duration != null) || (!DEFAULT_DURATION.equals(value))) {
			durationProperty().set(value);
		}
	}

	public final Duration getDuration() {
		return (duration == null)? DEFAULT_DURATION : duration.get();
	}

	public final ObjectProperty<Duration> durationProperty() {
		if (duration == null) {
			duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION) {

				@Override
				public void invalidated() {
					try {
						setCycleDuration(getDuration());
					} catch (IllegalArgumentException e) {
						if (isBound()) {
							unbind();
						}
						set(getCycleDuration());
						throw e;
					}
				}

				@Override
				public Object getBean() {
					return KillCountdownProgress.this;
				}

				@Override
				public String getName() {
					return "duration";
				}
			};
		}
		return duration;
	}

	public KillCountdownProgress(Duration duration, TurboBarButton ctrlCloseButton, String colorHex, boolean reverseAnimation) {
		setDuration(duration);
		setCycleDuration(duration);
		mCtrlCloseButton = ctrlCloseButton;
		mColorHex = colorHex;
		mDoReverse = reverseAnimation;
	}

	@Override
	public void stop() {
		super.stop();
		mCtrlCloseButton.setStyle(null);
	}

	@Override
	public void interpolate(double frac) {
		int progressPercent = (int) (frac * 100);
		if (mDoReverse)
			progressPercent = 100 - progressPercent;
		mCtrlCloseButton.setStyle("-fx-background-color: linear-gradient(" +
				"from 0% 0% to " + mCtrlCloseButton.getWidth() + "px 0px, " +
				"#" + mColorHex + " 0%, #" + mColorHex + " " + progressPercent + "%, " +
				"transparent " + progressPercent + "%, transparent 100%);"
		);
	}
}
