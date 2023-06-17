package com.jantarox.woodanalyzer.task;

import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.concurrent.Task;

public class SaveSegmentationTask extends Task<Void> {

    private final ImageSegmentationModel imageSegmentationModel;

    public SaveSegmentationTask(ImageSegmentationModel imageSegmentationModel) {
        this.imageSegmentationModel = imageSegmentationModel;
    }

    @Override
    protected Void call() throws Exception {
        updateMessage("Saving...");
        imageSegmentationModel.save();
        updateMessage("");
        return null;
    }
}
