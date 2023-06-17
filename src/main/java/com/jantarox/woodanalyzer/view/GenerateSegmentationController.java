package com.jantarox.woodanalyzer.view;

import com.jantarox.woodanalyzer.handler.ViewHandler;
import com.jantarox.woodanalyzer.viewmodel.GenerateSegmentationViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class GenerateSegmentationController {
    GenerateSegmentationViewModel generateSegmentationViewModel;
    ViewHandler viewHandler;

    @FXML
    TextArea outputTextArea;
    @FXML
    Button okButton;
    @FXML
    Button cancelButton;

    public void init(ViewHandler viewHandler, GenerateSegmentationViewModel generateSegmentationViewModel) {
        this.generateSegmentationViewModel = generateSegmentationViewModel;
        this.viewHandler = viewHandler;

        outputTextArea.textProperty().bind(generateSegmentationViewModel.commandOutputProperty());
        okButton.disableProperty().bind(generateSegmentationViewModel.okButtonDisabledProperty());
        cancelButton.disableProperty().bind(generateSegmentationViewModel.cancelButtonDisabledProperty());
        generateSegmentationViewModel.generateSegmentation();
        viewHandler.setOnCloseHandler(ViewHandler.GENERATE_SEGMENTATION_VIEW_NAME, (event) -> {
            onCancel();
        });
    }

    public void onOk() {
        generateSegmentationViewModel.reloadSegmentations();
        this.viewHandler.closeView(ViewHandler.GENERATE_SEGMENTATION_VIEW_NAME);
    }

    public void onCancel() {
        if (generateSegmentationViewModel.terminateProcess())
            this.viewHandler.closeView(ViewHandler.GENERATE_SEGMENTATION_VIEW_NAME);
    }
}
