package com.jantarox.woodanalyzer.viewmodel;

import com.jantarox.woodanalyzer.model.LoadedImagesModel;
import javafx.beans.property.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoaderViewModel {

    private final StringProperty sourceImagePath = new SimpleStringProperty();
    private final StringProperty filename = new SimpleStringProperty();
    private final LoadedImagesModel loadedImagesModel;
    private final BooleanProperty imageSelectedProperty = new SimpleBooleanProperty();
    private final SimpleObjectProperty<File> imageFileProperty = new SimpleObjectProperty<>();

    public LoaderViewModel(LoadedImagesModel loadedImagesModel) {
        this.loadedImagesModel = loadedImagesModel;

        this.imageFileProperty.addListener((observable, oldValue, newValue) ->
                imageSelectedProperty.setValue(newValue != null)
        );
    }

    public void chooseFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.tif"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            this.imageFileProperty.setValue(file);
            this.sourceImagePath.setValue(this.imageFileProperty.getValue().getAbsolutePath());
            String filename = this.imageFileProperty.getValue().getName();
            this.filename.setValue(filename.substring(0, filename.lastIndexOf('.')));
        }
    }

    public boolean loadImage() throws IOException {
        this.filename.setValue(this.filename.getValue().strip());
        if (!validFilename(this.filename.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Enter a valid filename!", ButtonType.OK);
            alert.showAndWait();
            return false;
        }

        BufferedImage image = ImageIO.read(this.imageFileProperty.getValue());
        String savePath = Paths.get(this.loadedImagesModel.getWoodImagesPath().toString(), this.filename.getValue().concat(".png")).toString();
        ImageIO.write(image, "png", new File(savePath));
        this.loadedImagesModel.updateLoadedImages();
        this.imageFileProperty.setValue(null);
        this.sourceImagePath.setValue("");
        this.filename.setValue("");
        return true;
    }

    private boolean validFilename(String filename) {
        Pattern pattern = Pattern.compile("^\\w+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filename);
        return matcher.find();
    }

    public StringProperty sourceImagePathProperty() {
        return sourceImagePath;
    }

    public StringProperty filenameProperty() {
        return filename;
    }

    public BooleanProperty imageSelectedProperty() {
        return imageSelectedProperty;
    }
}
