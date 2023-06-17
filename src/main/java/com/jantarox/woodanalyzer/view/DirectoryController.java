package com.jantarox.woodanalyzer.view;

import com.jantarox.woodanalyzer.handler.ViewHandler;
import com.jantarox.woodanalyzer.viewmodel.DirectoryViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class DirectoryController {
    DirectoryViewModel directoryViewModel;
    ViewHandler viewHandler;

    @FXML
    private TextField directoryTextField;

    public void init(ViewHandler viewHandler, DirectoryViewModel directoryViewModel) {
        this.directoryViewModel = directoryViewModel;
        this.viewHandler = viewHandler;

        directoryTextField.textProperty().bind(directoryViewModel.directoryPathProperty());
    }

    public void onChooseDirectory() throws IOException {
        directoryViewModel.chooseDirectoryDialog();
    }

    public void onOk() {
        this.directoryViewModel.setDirectoryPath();
        this.viewHandler.closeView(ViewHandler.DIRECTORY_VIEW_NAME);
    }

    public void onCancel() {
        this.viewHandler.closeView(ViewHandler.DIRECTORY_VIEW_NAME);
    }
}
