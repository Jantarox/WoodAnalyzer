package com.jantarox.woodanalyzer.viewmodel;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.datamodel.MeasureSegment;
import com.jantarox.woodanalyzer.datamodel.RingDuctArea;
import com.jantarox.woodanalyzer.datamodel.Segmentation;
import com.jantarox.woodanalyzer.datamodel.SegmentationPaths;
import com.jantarox.woodanalyzer.gestures.*;
import com.jantarox.woodanalyzer.model.BaseObservableModel;
import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import com.jantarox.woodanalyzer.model.LoadedImagesModel;
import com.jantarox.woodanalyzer.task.SaveSegmentationTask;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import java.beans.PropertyChangeEvent;
import java.io.IOException;

public class ImageViewModel extends BaseObservableModel {

    private final LoadedImagesModel loadedImagesModel;
    private final ImageSegmentationModel imageSegmentationModel;
    private final ObjectProperty<Image> backgroundImage = new SimpleObjectProperty<>();

    private final BooleanProperty imageLoaded = new SimpleBooleanProperty(false);
    private final StringProperty widthPixel = new SimpleStringProperty("-");
    private final StringProperty heightPixel = new SimpleStringProperty("-");
    private final StringProperty widthReal = new SimpleStringProperty("-");
    private final StringProperty heightReal = new SimpleStringProperty("-");
    private final StringProperty ppi = new SimpleStringProperty("-");

    private final StringProperty totalArea = new SimpleStringProperty("-");
    private final StringProperty totalResinDuctsArea = new SimpleStringProperty("-");
    private final StringProperty totalResinDuctsCount = new SimpleStringProperty("-");
    private final StringProperty totalDuctsAreaFraction = new SimpleStringProperty("-");
    private final IntegerProperty hoveredRingLabel = new SimpleIntegerProperty();
    private final StringProperty currentRingLabel = new SimpleStringProperty("-");
    private final StringProperty currentRingArea = new SimpleStringProperty("-");
    private final StringProperty currentDuctsArea = new SimpleStringProperty("-");
    private final StringProperty currentDuctsAreaFraction = new SimpleStringProperty("-");

    private final StringProperty coordinates = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<Byte> brushColor = new SimpleObjectProperty<>();
    private final IntegerProperty brushRadius = new SimpleIntegerProperty();

    private final ListProperty<SegmentationPaths> imageList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<MeasureSegment> measureSegmentsList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<RingDuctArea> measureAreasList = new SimpleListProperty<>(FXCollections.observableArrayList());

    private SaveSegmentationTask saveSegmentationTask;
    private ImageGestures imageGestures;

    public ImageViewModel(LoadedImagesModel loadedImagesModel, ImageSegmentationModel imageSegmentationModel) {
        this.loadedImagesModel = loadedImagesModel;
        this.imageSegmentationModel = imageSegmentationModel;

        this.loadedImagesModel.addListener(LoadedImagesModel.imagesPathsChanged, this::setImageList);
        this.loadedImagesModel.addListener(LoadedImagesModel.segmentationGenerated, this::refreshImagesList);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.segmentationPPIChanged, this::setImageRealPropertiesLabels);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.segmentationPPIChanged, this::setMeasureSegmentsList);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.segmentationPPIChanged, this::setMeasureAreasList);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.segmentationPPIChanged, this::setTotalAreaStatistics);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.imageSegmentationChanged, this::setImagePixelPropertiesLabels);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.imageSegmentationChanged, this::setImageRealPropertiesLabels);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.measureSegmentsChanged, this::setMeasureSegmentsList);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.measureAreasChanged, this::setMeasureAreasList);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.measureAreasChanged, this::setTotalAreaStatistics);
        this.backgroundImage.addListener((observable, oldValue, newValue) -> imageLoaded.setValue(backgroundImage.getValue() != null));
        this.hoveredRingLabel.addListener((observable, oldValue, newValue) -> {
            setSelectedAreaStatistics(newValue);
        });
        setImageList(null);
    }

    private void setSelectedAreaStatistics(Number newValue) {
        if (imageSegmentationModel.isAreasCalculated()) {
            short label = newValue.shortValue();
            if (label != 0) {
                RingDuctArea ringDuctArea = imageSegmentationModel.getRingDuctArea(label);
                currentRingLabel.setValue(String.format("%d", label));
                currentRingArea.setValue(String.format("%.2f", ringDuctArea.getRealRingArea(RingDuctArea.MILLIMETER2)));
                currentDuctsArea.setValue(String.format("%.2f", ringDuctArea.getRealDuctArea(RingDuctArea.MILLIMETER2)));
                currentDuctsAreaFraction.setValue(String.format("%.7f", ringDuctArea.getRealDuctArea(RingDuctArea.MILLIMETER2) / ringDuctArea.getRealRingArea(RingDuctArea.MILLIMETER2)));

            } else {
                currentRingLabel.setValue("-");
                currentRingArea.setValue("-");
                currentDuctsArea.setValue("-");
                currentDuctsAreaFraction.setValue("-");
            }
        }
    }

    private void setImageList(PropertyChangeEvent event) {
        imageList.getValue().setAll(loadedImagesModel.getLoadedImages());
    }

    public void refreshImagesList(PropertyChangeEvent event) {
        propertyChangeSupport.firePropertyChange("refreshImagesList", null, null);
    }

    private void setMeasureSegmentsList(PropertyChangeEvent event) {
        measureSegmentsList.getValue().setAll(imageSegmentationModel.getMeasureSegments());
    }

    private void setMeasureAreasList(PropertyChangeEvent event) {
        measureAreasList.getValue().setAll(imageSegmentationModel.getMeasureAreas());
    }

    public void setImages(SegmentationPaths segmentationPaths) {
        imageSegmentationModel.load(segmentationPaths);
        loadedImagesModel.setSelectedImage(segmentationPaths);
        this.backgroundImage.setValue(imageSegmentationModel.getBackgroundImage());
    }

    private void setImagePixelPropertiesLabels(PropertyChangeEvent event) {
        widthPixel.setValue(String.format("%d", imageSegmentationModel.getWidth()));
        heightPixel.setValue(String.format("%d", imageSegmentationModel.getHeight()));
    }

    private void setImageRealPropertiesLabels(PropertyChangeEvent event) {
        widthReal.setValue(String.format("%.2f", imageSegmentationModel.pixelsToUnit(imageSegmentationModel.getWidth(), Segmentation.MILLIMETER)));
        heightReal.setValue(String.format("%.2f", imageSegmentationModel.pixelsToUnit(imageSegmentationModel.getHeight(), Segmentation.MILLIMETER)));
        totalArea.setValue(String.format("%.2f", imageSegmentationModel.pixelsToUnit(imageSegmentationModel.getTotalArea(), Segmentation.MILLIMETER2)));
        ppi.setValue(Integer.toString(imageSegmentationModel.getPPI()));
    }

    private void setTotalAreaStatistics(PropertyChangeEvent event) {
        if (imageSegmentationModel.isAreasCalculated()) {
            double ductsArea = imageSegmentationModel.pixelsToUnit(imageSegmentationModel.getTotalResinDuctsArea(), Segmentation.MILLIMETER2);
            totalResinDuctsArea.setValue(String.format("%.2f", ductsArea));
            totalResinDuctsCount.setValue(String.format("%d", imageSegmentationModel.getTotalResinDuctsCount()));
            totalDuctsAreaFraction.setValue(String.format("%.7f", ductsArea / imageSegmentationModel.pixelsToUnit(imageSegmentationModel.getTotalArea(), Segmentation.MILLIMETER2)));
        } else {
            totalResinDuctsArea.setValue("Not calculated");
            totalResinDuctsCount.setValue("Not calculated");
            totalDuctsAreaFraction.setValue("Not calculated");
        }
    }

    public boolean segmentationWasModified() {
        return imageSegmentationModel.wasModified();
    }

    public void saveSegmentation(boolean inBackground) throws IOException {
        if (loadedImagesModel.getSelectedImage() == null)
            return;
        if (!inBackground) {
            status.setValue("Saving...");
            imageSegmentationModel.save();
            status.setValue("");
        } else if (saveSegmentationTask == null || !saveSegmentationTask.isRunning()) {
            SaveSegmentationTask saveSegmentationTask = new SaveSegmentationTask(imageSegmentationModel);
            saveSegmentationTask.messageProperty().addListener((observable, oldValue, newValue) -> {
                status.setValue(newValue);
            });
            saveSegmentationTask.runningProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    refreshImagesList(null);
                }
            });
            Thread thread = new Thread(saveSegmentationTask);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void setImageGestures(int mode, StackPane renderPane, ZoomStackPane browsePane, GraphicsContext graphicsContext) {
        if (this.imageGestures != null) {
            this.imageGestures.removeGestures(renderPane);
        }
        switch (mode) {
            case ImageGestures.BROWSE_GESTURES -> {
                this.imageGestures = new BrowseGestures(browsePane, graphicsContext, imageSegmentationModel);
            }
            case ImageGestures.DRAW_GESTURES -> {
                this.imageGestures = new DrawGestures(browsePane, graphicsContext, imageSegmentationModel, brushColor, brushRadius);
            }
            case ImageGestures.ADD_MEASURE_GESTURES -> {
                this.imageGestures = new AddMeasureGestures(browsePane, graphicsContext, imageSegmentationModel);
            }
            case ImageGestures.REMOVE_MEASURE_GESTURES -> {
                this.imageGestures = new RemoveMeasureGestures(browsePane, graphicsContext, imageSegmentationModel);
            }
            case ImageGestures.AREA_GESTURES -> {
                this.imageGestures = new AreaGestures(browsePane, graphicsContext, imageSegmentationModel, hoveredRingLabel);
            }
            default -> {
                this.imageGestures = new ImageGestures(browsePane, graphicsContext, imageSegmentationModel);
            }
        }
        this.imageGestures.addGestures(renderPane);
        coordinates.bind(imageGestures.coordinatesStringProperty());
    }

    public void setBrushColor(byte type) {
        brushColor.setValue(type);
    }

    public ObservableValue<? extends ObservableList<SegmentationPaths>> imageListProperty() {
        return imageList;
    }

    public ObservableValue<? extends ObservableList<MeasureSegment>> measureSegmentsListProperty() {
        return measureSegmentsList;
    }

    public ObservableValue<? extends ObservableList<RingDuctArea>> measureAreasListProperty() {
        return measureAreasList;
    }

    public ObservableValue<? extends Image> backgroundImageProperty() {
        return backgroundImage;
    }

    public StringProperty widthPixelProperty() {
        return widthPixel;
    }

    public StringProperty heightPixelProperty() {
        return heightPixel;
    }

    public StringProperty widthRealProperty() {
        return widthReal;
    }

    public StringProperty heightRealProperty() {
        return heightReal;
    }

    public StringProperty ppiProperty() {
        return ppi;
    }

    public StringProperty coordinatesProperty() {
        return coordinates;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public IntegerProperty brushRadiusProperty() {
        return brushRadius;
    }

    public BooleanProperty imageLoadedProperty() {
        return imageLoaded;
    }

    public StringProperty totalAreaProperty() {
        return totalArea;
    }

    public StringProperty totalResinDuctsAreaProperty() {
        return totalResinDuctsArea;
    }

    public StringProperty totalResinDuctsCountProperty() {
        return totalResinDuctsCount;
    }

    public StringProperty totalDuctsAreaFractionProperty() {
        return totalDuctsAreaFraction;
    }

    public StringProperty currentRingLabelProperty() {
        return currentRingLabel;
    }

    public StringProperty currentRingAreaProperty() {
        return currentRingArea;
    }

    public StringProperty currentDuctsAreaProperty() {
        return currentDuctsArea;
    }

    public StringProperty currentDuctsAreaFractionProperty() {
        return currentDuctsAreaFraction;
    }
}
