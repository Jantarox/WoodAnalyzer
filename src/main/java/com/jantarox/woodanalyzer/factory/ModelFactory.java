package com.jantarox.woodanalyzer.factory;

import com.jantarox.woodanalyzer.model.*;

public class ModelFactory {
    private LoadedImagesModel loadedImagesModel;
    private ImageSegmentationModel imageSegmentationModel;

    public LoadedImagesModel getImageModel() {
        if (loadedImagesModel == null)
            loadedImagesModel = new LoadedImagesModelManager("org", "seg");

        return loadedImagesModel;
    }

    public ImageSegmentationModel getImageSegmentationModel() {
        if (imageSegmentationModel == null)
            imageSegmentationModel = new ImageSegmentationModelManager();

        return imageSegmentationModel;
    }
}
