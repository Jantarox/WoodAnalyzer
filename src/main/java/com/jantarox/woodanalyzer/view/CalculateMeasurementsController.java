package com.jantarox.woodanalyzer.view;

import com.jantarox.woodanalyzer.handler.ViewHandler;
import com.jantarox.woodanalyzer.viewmodel.CalculateMeasurementsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class CalculateMeasurementsController {
    CalculateMeasurementsViewModel calculateMeasurementsViewModel;
    ViewHandler viewHandler;

    @FXML
    TextArea outputTextArea;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;

    public void init(ViewHandler viewHandler, CalculateMeasurementsViewModel calculateMeasurementsViewModel) {
        this.calculateMeasurementsViewModel = calculateMeasurementsViewModel;
        this.viewHandler = viewHandler;

        outputTextArea.textProperty().bind(calculateMeasurementsViewModel.commandOutputProperty());
        okButton.disableProperty().bind(calculateMeasurementsViewModel.okButtonDisabledProperty());
        cancelButton.disableProperty().bind(calculateMeasurementsViewModel.cancelButtonDisabledProperty());
        calculateMeasurementsViewModel.calculateMeasurements();
        viewHandler.setOnCloseHandler(ViewHandler.CALCULATE_MEASUREMENTS_VIEW_NAME, (event) -> {
            onCancel();
        });
    }

    public void onOk() {
        calculateMeasurementsViewModel.reloadSegmentations();
        this.viewHandler.closeView(ViewHandler.CALCULATE_MEASUREMENTS_VIEW_NAME);
    }

    public void onCancel() {
        if (calculateMeasurementsViewModel.terminateProcess())
            this.viewHandler.closeView(ViewHandler.CALCULATE_MEASUREMENTS_VIEW_NAME);
    }
}
