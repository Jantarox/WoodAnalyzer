package com.jantarox.woodanalyzer.model;

import com.jantarox.woodanalyzer.datamodel.SegmentationPaths;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;

public interface LoadedImagesModel extends ObservableModel {

    String imagesPathsChanged = "loadedImagesChanged";
    String segmentationGenerated = "segmentationGenerated";

    void updateLoadedImages();

    ArrayList<SegmentationPaths> getLoadedImages();

    Path getImagesBasePath();

    void setImagesBasePath(String imagesBasePath) throws FileNotFoundException;

    Path getWoodImagesPath();

    Path getSegmentationImagesPath();

    void notifySegmentationGenerated();

    SegmentationPaths getSelectedImage();

    void setSelectedImage(SegmentationPaths selectedImage);
}
