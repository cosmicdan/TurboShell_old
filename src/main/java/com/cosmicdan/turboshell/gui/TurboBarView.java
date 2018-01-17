package com.cosmicdan.turboshell.gui;

import com.cosmicdan.turboshell.gui.animations.KillCountdownProgress;
import com.cosmicdan.turboshell.gui.controls.TurboBarButton;
import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;

@Log4j2
public class TurboBarView {
	private HBox mView;
	private final Collection<Region> turboBarControls = new ArrayList<>(20);

	private final Object mLock = new Object();

	/*
	// main turbomenu button
	private TurboBarButton mCtrlTurboMenuButton;
	//private ImageView mImgTurboMenuButton;

	// sysmenu buttons
	private Button mCtrlSysMenuMinimize;
	private Button mCtrlSysMenuResize;
	private Button mCtrlSysMenuClose;
	private ImageView mImgSysMenuMinimize;
	private ImageView mImgSysMenuResize;
	private ImageView mImgSysMenuClose;
	*/

	/*
	private TextField xField;
	private TextField yField;
	private Label sumLabel;
	*/

	private final TurboBarController mController;
	private final TurboBarModel mModel;

	private final TurboBarButton mCtrlTurboMenuButton = new TurboBarButton("", "TurboBar_turbomenu_button.png");
	private final TurboBarButton mCtrlSysButtonMinimize = new TurboBarButton("", "TurboBar_sysbtn_minimize.png");
	private final TurboBarButton mCtrlSysButtonResize = new TurboBarButton("", new String[] {"TurboBar_sysbtn_resize_restore.png", "TurboBar_sysbtn_resize_maximize.png"});
	private final TurboBarButton mCtrlSysButtonClose = new TurboBarButton("", "TurboBar_sysbtn_close.png");

	private boolean wasCloseHeld = false; // used to prevent a held close event's "click" carrying through to the next window

	public TurboBarView(TurboBarModel model, TurboBarController controller) {
		mController = controller ;
		mModel = model ;

		setupPaneAndControls();
		updateControllerFromListeners();
		observeModelAndUpdateControls();
	}

	Parent asParent() {
		return mView ;
	}

	/*
	public void updateResizeButton(boolean isMaximized) {
		Platform.runLater(() -> mCtrlSysButtonResize.switchImage(isMaximized ? 0 : 1));
	}
	*/

	private void setupPaneAndControls() {
		mView = new HBox();
		mView.setId("turbobar");

		// main turbomenu button
		turboBarControls.add(mCtrlTurboMenuButton);

		// middle spacing
		final Region centerPadding = new Region();
		HBox.setHgrow(centerPadding, Priority.ALWAYS);
		turboBarControls.add(centerPadding);

		// sysmenu buttons
		turboBarControls.add(mCtrlSysButtonMinimize);
		turboBarControls.add(mCtrlSysButtonResize);
		mCtrlSysButtonClose.setId("close");
		turboBarControls.add(mCtrlSysButtonClose);

		// all controls done
		mView.getChildren().addAll(turboBarControls);

		/*
		ColumnConstraints leftCol = new ColumnConstraints();
		leftCol.setHalignment(HPos.RIGHT);
		leftCol.setHgrow(Priority.NEVER);

		ColumnConstraints rightCol = new ColumnConstraints();
		rightCol.setHgrow(Priority.SOMETIMES);

		view.getColumnConstraints().addAll(leftCol, rightCol);

		view.setAlignment(Pos.CENTER);
		view.setHgap(5);
		view.setVgap(10);

		xField = new TextField();
		//configTextFieldForInts(xField);

		yField = new TextField();
		//configTextFieldForInts(yField);

		sumLabel = new Label();

		view.addRow(0, new Label("X:"), xField);
		view.addRow(1, new Label("Y:"), yField);
		view.addRow(2, new Label("Sum:"), sumLabel);
		*/
	}

	private void updateControllerFromListeners() {
		// close button
		mCtrlSysButtonClose.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (wasCloseHeld)
				wasCloseHeld = false;
			else
				mController.eventCloseButtonClick(event);
			});

		// close button primary-click-and-hold
		addHoldButtonHandler(MouseButton.PRIMARY, mCtrlSysButtonClose, Duration.seconds(1), event -> {
			wasCloseHeld = true;
			mController.eventCloseButtonPrimaryHold();
		});

		// close button secondary-click-and-hold
		addHoldButtonHandler(MouseButton.SECONDARY, mCtrlSysButtonClose, Duration.seconds(1), event -> {
			mController.eventCloseButtonSecondaryHold();
		});

		// resize button
		mCtrlSysButtonResize.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->
				mController.eventResizeButtonClick(event));

		// minimize button
		mCtrlSysButtonMinimize.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->
				mController.eventMinimizeButtonClick());

		/*
		xField.textProperty().addListener((obs, oldText, newText) -> controller.updateX(newText));
		yField.textProperty().addListener((obs, oldText, newText) -> controller.updateY(newText));
		*/
	}

	private void observeModelAndUpdateControls() {
		// resize graphic listener
		mModel.getCtrlResizeGraphicIndex().addListener((observable, oldIndex, newIndex) ->
				refreshResizeButton(newIndex, mCtrlSysButtonResize));

		// sysbtn enabled state listeners
		mModel.getCtrlSysbtnEnabled()[0].addListener((observable, oldValue, newValue) ->
				refreshSysbtnEnabledState(newValue, mCtrlSysButtonClose));
		mModel.getCtrlSysbtnEnabled()[1].addListener((observable, oldValue, newValue) ->
				refreshSysbtnEnabledState(newValue, mCtrlSysButtonResize));
		mModel.getCtrlSysbtnEnabled()[2].addListener((observable, oldValue, newValue) ->
				refreshSysbtnEnabledState(newValue, mCtrlSysButtonMinimize));


		/*
		model.xProperty().addListener((obs, oldX, newX) ->
				updateIfNeeded(newX, xField));

		model.yProperty().addListener((obs, oldY, newY) ->
				updateIfNeeded(newY, yField));

		sumLabel.textProperty().bind(model.sumProperty().asString());
		*/
	}

	private void refreshResizeButton(Number newIndex, TurboBarButton mCtrlSysButtonResize) {
		mCtrlSysButtonResize.setGraphic(mCtrlSysButtonResize.getImage(newIndex.intValue()));
	}

	private void refreshSysbtnEnabledState(Boolean newValue, TurboBarButton mCtrlSysButton) {
		mCtrlSysButton.setDisable(!newValue.booleanValue());
	}

	/**
	 * Thanks to James_D @ StackOverflow for the basis of this
	 * https://stackoverflow.com/a/25610190/1767892
	 */
	private void addHoldButtonHandler(MouseButton mouseButton, TurboBarButton node, Duration holdTime, EventHandler<MouseEvent> handler) {

		class Wrapper<T> { T content ; }
		Wrapper<MouseEvent> eventWrapper = new Wrapper<>();

		KillCountdownProgress holdTimer = new KillCountdownProgress(holdTime, mCtrlSysButtonClose);
		holdTimer.setOnFinished(event -> handler.handle(eventWrapper.content));

		node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			if (mouseButton == event.getButton()) {
				eventWrapper.content = event ;
				holdTimer.playFromStart();
			}
		});
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
			holdTimer.stop();
		});
		node.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, event -> {
			if (wasCloseHeld)
				wasCloseHeld = false;
			holdTimer.stop();
		});
	}




 /*
	private void updateIfNeeded(Number value, TextField field) {
		String s = value.toString() ;
		if (! field.getText().equals(s)) {
			field.setText(s);
		}
	}


	private void configTextFieldForInts(TextField field) {
		field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
			if (c.getControlNewText().matches("-?\\d*")) {
				return c ;
			}
			return null ;
		}));
	}
	*/
}

