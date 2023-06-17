package com.jantarox.woodanalyzer.datamodel;

import java.io.File;

public class SegmentationPaths {
    private String imagePath;
    private String segmentationPath;
    private int dpi;

    public SegmentationPaths(String imagePath, String segmentationPath) {
        this.imagePath = imagePath;
        this.segmentationPath = segmentationPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSegmentationPath() {
        return segmentationPath;
    }

    public void setSegmentationPath(String segmentationPath) {
        this.segmentationPath = segmentationPath;
    }

    public boolean segmentationExists() {
        return new File(getSegmentationPath()).exists();
    }

    public String getImageFilename() {
        return imagePath.substring(imagePath.lastIndexOf("/") + 1);
    }

    public String getSegmentationFilename() {
        return segmentationPath.substring(segmentationPath.lastIndexOf("\\") + 1);
    }

    @Override
    public String toString() {
        return getImageFilename();
    }
}
