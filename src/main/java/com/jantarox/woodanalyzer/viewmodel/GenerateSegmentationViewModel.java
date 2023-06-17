package com.jantarox.woodanalyzer.viewmodel;

import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import com.jantarox.woodanalyzer.model.LoadedImagesModel;
import com.jantarox.woodanalyzer.task.RunAnalyserCommandTask;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class GenerateSegmentationViewModel {

    private final LoadedImagesModel loadedImagesModel;
    private final ImageSegmentationModel imageSegmentationModel;
    private final StringProperty commandOutput = new SimpleStringProperty("");
    private final BooleanProperty okButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty cancelButtonDisabled = new SimpleBooleanProperty(false);
    private RunAnalyserCommandTask runAnalyserCommandTask;

    public GenerateSegmentationViewModel(LoadedImagesModel loadedImagesModel, ImageSegmentationModel imageSegmentationModel) {
        this.loadedImagesModel = loadedImagesModel;
        this.imageSegmentationModel = imageSegmentationModel;
    }

    public void generateSegmentation() {
        commandOutput.setValue("");
        okButtonDisabled.setValue(true);
        cancelButtonDisabled.setValue(false);

        runAnalyserCommandTask = new RunAnalyserCommandTask("generate-segmentation", loadedImagesModel.getSelectedImage().getImageFilename());
        runAnalyserCommandTask.messageProperty().addListener((observable, oldValue, newValue) -> addOutputLogs(newValue));
        runAnalyserCommandTask.runningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                okButtonDisabled.setValue(true);
                cancelButtonDisabled.setValue(false);
            } else {
                okButtonDisabled.setValue(false);
                cancelButtonDisabled.setValue(true);
            }
        });

        Thread thread = new Thread(runAnalyserCommandTask);
        thread.setDaemon(true);
        thread.start();
    }

    public boolean terminateProcess() {
        boolean result;
        if (runAnalyserCommandTask != null && runAnalyserCommandTask.isRunning()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "The segmentation generation is still in process!\nAre you sure you want to cancel?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> alertResult = alert.showAndWait();
            if (alertResult.isPresent() && alertResult.get() == ButtonType.YES) {
                runAnalyserCommandTask.cancel();
                result = true;
            } else
                result = false;
        } else
            result = true;
        if (result) {
            reloadSegmentations();
        }
        return result;
    }

    public void reloadSegmentations() {
        imageSegmentationModel.reload();
        loadedImagesModel.notifySegmentationGenerated();
    }

    private void addOutputLogs(String logs) {
        commandOutput.setValue(logs + "\n" + commandOutput.get());
    }

    public StringProperty commandOutputProperty() {
        return commandOutput;
    }

    public BooleanProperty okButtonDisabledProperty() {
        return okButtonDisabled;
    }

    public BooleanProperty cancelButtonDisabledProperty() {
        return cancelButtonDisabled;
    }


}
