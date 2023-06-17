package com.jantarox.woodanalyzer.model;

import com.jantarox.woodanalyzer.datamodel.SegmentationPaths;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class LoadedImagesModelManager extends BaseObservableModel implements LoadedImagesModel {

    private final String woodImagesDirectoryName;
    private final String segmentationsDirectoryName;
    private final ArrayList<SegmentationPaths> loadedImages = new ArrayList<>();
    private Path imagesBasePath;
    private SegmentationPaths selectedImage;

    public LoadedImagesModelManager(String woodImagesDirectoryName, String segmentationsDirectoryName) {
        this.imagesBasePath = Paths.get(System.getProperty("user.dir"), "images"); // "C:\\Users\\jante\\OneDrive\\Pulpit\\praca_dyplomowa" | System.getProperty("user.dir")

        this.woodImagesDirectoryName = woodImagesDirectoryName == null ? "org" : woodImagesDirectoryName;
        this.segmentationsDirectoryName = segmentationsDirectoryName == null ? "seg" : segmentationsDirectoryName;

        updateLoadedImages();
    }

    @Override
    public void updateLoadedImages() {
        Path startDir = this.getWoodImagesPath();

        loadedImages.clear();
        try {
            Files.walk(startDir)
                    .filter(path -> path.toString().endsWith(".png"))
                    .forEach(imagePath -> {
                        String filename = startDir.relativize(imagePath).toString();
                        Path segPath = Paths.get(this.getSegmentationImagesPath().toString(), filename.replace(".png", ".json"));
                        try {
                            loadedImages.add(new SegmentationPaths(imagePath.toUri().toURL().toExternalForm(), segPath.toString()));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (selectedImage != null) {
            for (SegmentationPaths image : loadedImages) {
                if (image.getImagePath().equals(selectedImage.getImagePath())) {
                    selectedImage = image;
                    break;
                }
            }
        }
        propertyChangeSupport.firePropertyChange(LoadedImagesModel.imagesPathsChanged, null, null);
    }

    @Override
    public ArrayList<SegmentationPaths> getLoadedImages() {
        return loadedImages;
    }

    @Override
    public Path getImagesBasePath() {
        return this.imagesBasePath;
    }

    @Override
    public void setImagesBasePath(String imagesBasePath) throws FileNotFoundException {
        if (!Files.exists(Paths.get(imagesBasePath, woodImagesDirectoryName)) || !Files.exists(Paths.get(imagesBasePath, woodImagesDirectoryName))) {
            throw new FileNotFoundException();
        }
        this.imagesBasePath = Paths.get(imagesBasePath);
        updateLoadedImages();
    }

    @Override
    public Path getWoodImagesPath() {
        return Paths.get(this.imagesBasePath.toString(), woodImagesDirectoryName);
    }

    @Override
    public Path getSegmentationImagesPath() {
        return Paths.get(this.imagesBasePath.toString(), segmentationsDirectoryName);
    }

    @Override
    public void notifySegmentationGenerated() {
        propertyChangeSupport.firePropertyChange(LoadedImagesModel.segmentationGenerated, null, null);
    }

    @Override
    public SegmentationPaths getSelectedImage() {
        return selectedImage;
    }

    @Override
    public void setSelectedImage(SegmentationPaths selectedImage) {
        this.selectedImage = selectedImage;
    }
}
