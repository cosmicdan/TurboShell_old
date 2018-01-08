package com.cosmicdan.turboshell.gui;

import com.cosmicdan.turboshell.gui.controls.TurboBarButton;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class TurboBarView {
	private HBox mView;
	private final Collection<Region> turboBarControls = new ArrayList<>(20);

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

	private final TurboBarController mController ;
	private final TurboBarModel mModel ;

	public TurboBarView(TurboBarModel model, TurboBarController controller) {
		mController = controller ;
		mModel = model ;

		setupPaneAndControls();
		updateControllerFromListeners();
		observeModelAndUpdateControls();
	}

	public Parent asParent() {
		return mView ;
	}

	private void setupPaneAndControls() {
		mView = new HBox();
		mView.setId("turbobar");

		// main turbomenu button
		final TurboBarButton mCtrlTurboMenuButton = new TurboBarButton("", "TurboBar_turbomenu_button.png");
		turboBarControls.add(mCtrlTurboMenuButton);

		// middle spacing
		final Region centerPadding = new Region();
		HBox.setHgrow(centerPadding, Priority.ALWAYS);
		turboBarControls.add(centerPadding);

		// sysmenu button - minimize
		final TurboBarButton mCtrlSysButtonMinimize = new TurboBarButton("", "TurboBar_sysbtn_minimize.png");
		turboBarControls.add(mCtrlSysButtonMinimize);

		// sysmenu button - restore/maximize
		final TurboBarButton mCtrlSysButtonResize = new TurboBarButton("", new String[] {"TurboBar_sysbtn_resize_restore.png", "TurboBar_sysbtn_resize_maximize.png"});
		turboBarControls.add(mCtrlSysButtonResize);

		// sysmenu button - close
		final TurboBarButton mCtrlSysButtonClose = new TurboBarButton("", "TurboBar_sysbtn_close.png");
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
		/*
		xField.textProperty().addListener((obs, oldText, newText) -> controller.updateX(newText));
		yField.textProperty().addListener((obs, oldText, newText) -> controller.updateY(newText));
		*/
	}

	private void observeModelAndUpdateControls() {
		/*
		model.xProperty().addListener((obs, oldX, newX) ->
				updateIfNeeded(newX, xField));

		model.yProperty().addListener((obs, oldY, newY) ->
				updateIfNeeded(newY, yField));

		sumLabel.textProperty().bind(model.sumProperty().asString());
		*/
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

