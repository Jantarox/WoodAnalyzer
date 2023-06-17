package com.jantarox.woodanalyzer.handler;

import com.jantarox.woodanalyzer.WoodAnalyzerApp;
import com.jantarox.woodanalyzer.factory.ViewModelFactory;
import com.jantarox.woodanalyzer.view.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;

public class ViewHandler {
    public static final String IMAGE_VIEW_NAME = "Image";
    public static final String LOADER_VIEW_NAME = "Loader";
    public static final String DIRECTORY_VIEW_NAME = "Directory";
    public static final String GENERATE_SEGMENTATION_VIEW_NAME = "GenerateSegmentation";
    public static final String PPI_CHANGER_VIEW_NAME = "PPIChanger";
    public static final String CALCULATE_MEASUREMENTS_VIEW_NAME = "CalculateMeasurements";
    private final HashMap<String, Stage> stageHashMap;
    private final ViewModelFactory viewModelFactory;

    public ViewHandler(ViewModelFactory viewModelFactory) {
        this.stageHashMap = new HashMap<>();
        this.viewModelFactory = viewModelFactory;
    }

    public void start() throws IOException {
        openView(IMAGE_VIEW_NAME);
    }

    public void openView(String viewName) throws IOException {
        if (!this.stageHashMap.containsKey(viewName))
            this.stageHashMap.put(viewName, new Stage());

        openView(viewName, this.stageHashMap.get(viewName));
    }

    public void openView(String viewName, Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        Parent root = null;

        if (viewName.equals(IMAGE_VIEW_NAME)) {
            loader.setLocation(WoodAnalyzerApp.class.getResource("image-view.fxml"));
            root = loader.load();
            ImageController view = loader.getController();
            view.init(this, viewModelFactory.getImageViewModel());
            stage.getIcons().add(new Image(WoodAnalyzerApp.class.getResourceAsStream("icon.png")));
            stage.setTitle("WoodAnalyzer");
            stage.setMaximized(true);
            stage.setMinHeight(1020);
            stage.setMinWidth(1200);
            stage.setScene(new Scene(root));
            stage.show();
            return;
        }

        switch (viewName) {
            case LOADER_VIEW_NAME -> {
                loader.setLocation(WoodAnalyzerApp.class.getResource("loader-view.fxml"));
                root = loader.load();
                LoaderController view = loader.getController();
                view.init(this, viewModelFactory.getLoaderViewModel());
                stage.setTitle("Load file");
            }
            case DIRECTORY_VIEW_NAME -> {
                loader.setLocation(WoodAnalyzerApp.class.getResource("directory-view.fxml"));
                root = loader.load();
                DirectoryController view = loader.getController();
                view.init(this, viewModelFactory.getDirectoryViewModel());
                stage.setTitle("Choose image directory");
            }
            case GENERATE_SEGMENTATION_VIEW_NAME -> {
                loader.setLocation(WoodAnalyzerApp.class.getResource("generate-segmentation-view.fxml"));
                root = loader.load();
                GenerateSegmentationController view = loader.getController();
                view.init(this, viewModelFactory.getGenerateSegmentationViewModel());
                stage.setTitle("Generate segmentation");
            }
            case PPI_CHANGER_VIEW_NAME -> {
                loader.setLocation(WoodAnalyzerApp.class.getResource("change-ppi-view.fxml"));
                root = loader.load();
                PPIChangerController view = loader.getController();
                view.init(this, viewModelFactory.getPPIChangerViewModel());
                stage.setTitle("Change PPI");
            }
            case CALCULATE_MEASUREMENTS_VIEW_NAME -> {
                loader.setLocation(WoodAnalyzerApp.class.getResource("calculate-measurements-view.fxml"));
                root = loader.load();
                CalculateMeasurementsController view = loader.getController();
                view.init(this, viewModelFactory.getCalculateMeasurementsViewModel());
                stage.setTitle("Calculate measurements");
            }
        }
        stage.getIcons().add(new Image(WoodAnalyzerApp.class.getResourceAsStream("icon.png")));
        stage.setScene(new Scene(root));

        try {
            stage.initOwner(stageHashMap.get(IMAGE_VIEW_NAME));
            stage.initModality(Modality.WINDOW_MODAL);
        } catch (IllegalStateException ignored) {
        }
        stage.showAndWait();
    }

    public void closeView(String viewName) {
        if (this.stageHashMap.containsKey(viewName)) {
            this.stageHashMap.remove(viewName).close();
        }
    }

    public void setOnCloseHandler(String viewName, EventHandler<WindowEvent> onCloseEvent) {
        if (this.stageHashMap.containsKey(viewName)) {
            this.stageHashMap.get(viewName).setOnCloseRequest(onCloseEvent);
        }
    }
}
