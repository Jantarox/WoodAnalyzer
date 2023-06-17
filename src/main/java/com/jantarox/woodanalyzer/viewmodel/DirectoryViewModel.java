package com.jantarox.woodanalyzer.viewmodel;

import com.jantarox.woodanalyzer.model.LoadedImagesModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class DirectoryViewModel {

    private final StringProperty directoryPath = new SimpleStringProperty();

    private final LoadedImagesModel loadedImagesModel;

    public DirectoryViewModel(LoadedImagesModel loadedImagesModel) {
        this.loadedImagesModel = loadedImagesModel;
        this.directoryPath.setValue(loadedImagesModel.getImagesBasePath().toString());
    }

    public void chooseDirectoryDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(new Stage());
        if (file != null)
            this.directoryPath.setValue(file.getAbsolutePath());
    }

    public void setDirectoryPath() {
        try {
            this.loadedImagesModel.setImagesBasePath(this.directoryPath.get());

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Directory does not contain org and seg directories!", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public StringProperty directoryPathProperty() {
        return directoryPath;
    }
}
