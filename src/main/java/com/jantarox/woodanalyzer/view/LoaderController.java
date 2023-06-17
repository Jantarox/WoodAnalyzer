package com.jantarox.woodanalyzer.view;

import com.jantarox.woodanalyzer.handler.ViewHandler;
import com.jantarox.woodanalyzer.viewmodel.LoaderViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoaderController {
    LoaderViewModel loaderViewModel;
    ViewHandler viewHandler;

    @FXML
    private Label sourceFilePathLabel;
    @FXML
    private TextField filenameTextField;
    @FXML
    private Button okButton;

    public void init(ViewHandler viewHandler, LoaderViewModel loaderViewModel) {
        this.loaderViewModel = loaderViewModel;
        this.viewHandler = viewHandler;

        sourceFilePathLabel.textProperty().bind(loaderViewModel.sourceImagePathProperty());
        filenameTextField.textProperty().bindBidirectional(loaderViewModel.filenameProperty());

        filenameTextField.disableProperty().bind(loaderViewModel.imageSelectedProperty().not());
        okButton.disableProperty().bind(loaderViewModel.imageSelectedProperty().not());
    }

    public void onChooseFile() throws IOException {
        loaderViewModel.chooseFileDialog();
    }

    public void onOk() throws IOException {
        if (loaderViewModel.loadImage())
            this.viewHandler.closeView(ViewHandler.LOADER_VIEW_NAME);
    }

    public void onCancel() {
        this.viewHandler.closeView(ViewHandler.LOADER_VIEW_NAME);
    }
}
