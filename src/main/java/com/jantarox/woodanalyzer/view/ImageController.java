package com.jantarox.woodanalyzer.view;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.datamodel.MeasureSegment;
import com.jantarox.woodanalyzer.datamodel.RingDuctArea;
import com.jantarox.woodanalyzer.datamodel.Segmentation;
import com.jantarox.woodanalyzer.datamodel.SegmentationPaths;
import com.jantarox.woodanalyzer.factory.MeasureSegmentCellFactory;
import com.jantarox.woodanalyzer.factory.RingDuctAreaCellFactory;
import com.jantarox.woodanalyzer.factory.SegmentationPathsCellFactory;
import com.jantarox.woodanalyzer.gestures.ImageGestures;
import com.jantarox.woodanalyzer.handler.ViewHandler;
import com.jantarox.woodanalyzer.viewmodel.ImageViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImageController {

    List<Rectangle> brushSelectors = new ArrayList<>();
    List<Rectangle> measureSelectors = new ArrayList<>();
    private ImageViewModel imageViewModel;
    private ViewHandler viewHandler;
    @FXML
    private ImageView woodImageView;
    @FXML
    private Canvas segmentationCanvas;
    @FXML
    private StackPane renderStackPane;
    @FXML
    private ZoomStackPane browsePane;
    @FXML
    private Label coordinatesLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private ListView<SegmentationPaths> imageListView;
    @FXML
    private ListView<MeasureSegment> measureSegmentsListView;
    @FXML
    private ListView<RingDuctArea> measureAreasListView;
    @FXML
    private Label widthPixelLabel;
    @FXML
    private Label heightPixelLabel;
    @FXML
    private Label widthRealLabel;
    @FXML
    private Label heightRealLabel;
    @FXML
    private Label ppiLabel;
    @FXML
    private Group segmentationButtonsGroup;
    @FXML
    private Button calculateAreasButton;
    @FXML
    private TabPane gesturesTabPane;
    @FXML
    private Group measurePickerGroup;
    @FXML
    private Rectangle drawMeasureSelector;
    @FXML
    private Rectangle eraseMeasureSelector;
    @FXML
    private Group brushPickerGroup;
    @FXML
    private Rectangle growthRingBrushSelector;
    @FXML
    private Rectangle resinDuctBrushSelector;
    @FXML
    private Rectangle eraserBrushSelector;
    @FXML
    private Rectangle browseAreasSelector;
    @FXML
    private Slider brushSizeSlider;
    @FXML
    private TextField brushSizeTextField;
    @FXML
    private Label totalAreaLabel;
    @FXML
    private Label totalResinDuctsAreaLabel;
    @FXML
    private Label totalResinDuctsCountLabel;
    @FXML
    private Label totalDuctsAreaFractionLabel;
    @FXML
    private Label currentRingLabelLabel;
    @FXML
    private Label currentRingAreaLabel;
    @FXML
    private Label currentDuctsAreaLabel;
    @FXML
    private Label currentDuctsAreaFractionLabel;

    public void init(ViewHandler viewHandler, ImageViewModel imageViewModel) {
        this.viewHandler = viewHandler;
        this.imageViewModel = imageViewModel;

        brushPickerGroup.getChildren().forEach(node -> {
            brushSelectors.add((Rectangle) node);
        });
        measurePickerGroup.getChildren().forEach(node -> {
            measureSelectors.add((Rectangle) node);
        });
        measureSelectors.add(browseAreasSelector);

        imageListView.setPlaceholder(new Label("No images loaded."));
        imageListView.setCellFactory(new SegmentationPathsCellFactory());
        imageListView.itemsProperty().bind(imageViewModel.imageListProperty());
        imageListView.getSelectionModel().selectedItemProperty().addListener(onSelectedImageChanged());
        imageViewModel.addListener("refreshImagesList", (event) -> imageListView.refresh());
        measureSegmentsListView.setPlaceholder(new Label("No segments to show."));
        measureSegmentsListView.setCellFactory(new MeasureSegmentCellFactory());
        measureSegmentsListView.itemsProperty().bind(imageViewModel.measureSegmentsListProperty());
        measureAreasListView.setPlaceholder(new Label("No areas to show."));
        measureAreasListView.setCellFactory(new RingDuctAreaCellFactory());
        measureAreasListView.itemsProperty().bind(imageViewModel.measureAreasListProperty());

        woodImageView.imageProperty().bind(imageViewModel.backgroundImageProperty());
        segmentationButtonsGroup.disableProperty().bind(imageViewModel.imageLoadedProperty().not());
        calculateAreasButton.disableProperty().bind(imageViewModel.imageLoadedProperty().not());

        pickDrawMeasurePen();

        coordinatesLabel.textProperty().bind(imageViewModel.coordinatesProperty());
        statusLabel.textProperty().bind(imageViewModel.statusProperty());
        gesturesTabPane.getSelectionModel().selectedItemProperty().addListener(onSelectedGesturesTabChanged());

        segmentationCanvas.widthProperty().bind(renderStackPane.widthProperty());
        segmentationCanvas.heightProperty().bind(renderStackPane.heightProperty());

        // IMAGE PROPERTIES WINDOW
        widthPixelLabel.textProperty().bind(imageViewModel.widthPixelProperty());
        heightPixelLabel.textProperty().bind(imageViewModel.heightPixelProperty());
        widthRealLabel.textProperty().bind(imageViewModel.widthRealProperty());
        heightRealLabel.textProperty().bind(imageViewModel.heightRealProperty());
        ppiLabel.textProperty().bind(imageViewModel.ppiProperty());

        // AREA STATISTICS WINDOW
        totalAreaLabel.textProperty().bind(imageViewModel.totalAreaProperty());
        totalResinDuctsAreaLabel.textProperty().bind(imageViewModel.totalResinDuctsAreaProperty());
        totalResinDuctsCountLabel.textProperty().bind(imageViewModel.totalResinDuctsCountProperty());
        totalDuctsAreaFractionLabel.textProperty().bind(imageViewModel.totalDuctsAreaFractionProperty());
        currentRingLabelLabel.textProperty().bind(imageViewModel.currentRingLabelProperty());
        currentRingAreaLabel.textProperty().bind(imageViewModel.currentRingAreaProperty());
        currentDuctsAreaLabel.textProperty().bind(imageViewModel.currentDuctsAreaProperty());
        currentDuctsAreaFractionLabel.textProperty().bind(imageViewModel.currentDuctsAreaFractionProperty());

        // MEASURE GESTURES WINDOW
        drawMeasureSelector.setFill(Color.BLACK);
        eraseMeasureSelector.setFill(Color.GREY);
        browseAreasSelector.setFill(Color.YELLOW);

        // DRAW GESTURES WINDOW
        growthRingBrushSelector.setFill(Segmentation.GROWTH_RING_BRUSH_COLOR);
        resinDuctBrushSelector.setFill(Segmentation.RESIN_DUCT_BRUSH_COLOR);
        eraserBrushSelector.setFill(Segmentation.BACKGROUND_BRUSH_COLOR);

        brushSizeTextField.setTextFormatter(new TextFormatter<>(this::filter));
        brushSizeTextField.setText(String.valueOf(brushSizeSlider.getValue()));
        Bindings.bindBidirectional(brushSizeTextField.textProperty(), brushSizeSlider.valueProperty(), new NumberStringConverter());
        this.imageViewModel.brushRadiusProperty().bind(brushSizeSlider.valueProperty().subtract(1));

        viewHandler.setOnCloseHandler(ViewHandler.IMAGE_VIEW_NAME, (event) -> {
            saveUnsavedChanges();
        });
    }

    private void setImageGestures(int mode) {
        this.imageViewModel.setImageGestures(mode, renderStackPane, browsePane, segmentationCanvas.getGraphicsContext2D());
    }

    public void saveFile() throws IOException {
        imageViewModel.saveSegmentation(true);
    }

    public void openLoaderView() throws IOException {
        viewHandler.openView(ViewHandler.LOADER_VIEW_NAME);
    }

    public void openDirectoryView() throws IOException {
        viewHandler.openView(ViewHandler.DIRECTORY_VIEW_NAME);
    }

    public void openGenerateSegmentationView() throws IOException {
        viewHandler.openView(ViewHandler.GENERATE_SEGMENTATION_VIEW_NAME);
    }

    public void openCalculateMeasurementsView() throws IOException {
        saveUnsavedChanges();
        viewHandler.openView(ViewHandler.CALCULATE_MEASUREMENTS_VIEW_NAME);
    }

    public void openPPIChangerView() throws IOException {
        viewHandler.openView(ViewHandler.PPI_CHANGER_VIEW_NAME);
    }

    public ChangeListener<SegmentationPaths> onSelectedImageChanged() {
        return (observable, oldValue, newValue) -> {
            if (newValue == null)
                return;
            saveUnsavedChanges();
            this.browsePane.resetTransforms();
            this.imageViewModel.setImages(newValue);
        };
    }

    private void saveUnsavedChanges() {
        if (imageViewModel.segmentationWasModified()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You have unsaved changes\ndo you want to save?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> alertResult = alert.showAndWait();
            if (alertResult.isPresent() && alertResult.get() == ButtonType.YES) {
                try {
                    imageViewModel.saveSegmentation(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ChangeListener<Tab> onSelectedGesturesTabChanged() {
        return (observable, oldValue, newValue) -> {
            switch (newValue.getText()) {
                case "Draw" -> {
                    setImageGestures(ImageGestures.DRAW_GESTURES);
                    pickGrowthRingBrush();
                }
                case "Measure" -> {
                    pickDrawMeasurePen();
                }
                case "Area" -> {
                    setImageGestures(ImageGestures.AREA_GESTURES);
                }
            }
        };
    }

    public void onExit() {
        saveUnsavedChanges();
        viewHandler.closeView(ViewHandler.IMAGE_VIEW_NAME);
    }

    public void pickDrawMeasurePen() {
        setImageGestures(ImageGestures.ADD_MEASURE_GESTURES);
        pickSelector(drawMeasureSelector, measureSelectors);
    }

    public void pickEraseMeasurePen() {
        setImageGestures(ImageGestures.REMOVE_MEASURE_GESTURES);
        pickSelector(eraseMeasureSelector, measureSelectors);
    }

    public void pickBrowseAreas() {
        setImageGestures(ImageGestures.AREA_GESTURES);
        pickSelector(browseAreasSelector, measureSelectors);
    }

    public void pickGrowthRingBrush() {
        imageViewModel.setBrushColor(Segmentation.GROWTH_RING);
        pickSelector(growthRingBrushSelector, brushSelectors);
    }

    public void pickResinDuctBrush() {
        imageViewModel.setBrushColor(Segmentation.RESIN_DUCT);
        pickSelector(resinDuctBrushSelector, brushSelectors);
    }

    public void pickEraserBrush() {
        imageViewModel.setBrushColor(Segmentation.BACKGROUND);
        pickSelector(eraserBrushSelector, brushSelectors);
    }

    public void pickSelector(Rectangle selector, List<Rectangle> selectorList) {
        selectorList.forEach(rectangle -> {
            if (rectangle.equals(selector)) {
                rectangle.setStroke(Color.RED);
                rectangle.setStrokeWidth(3);
            } else {
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(1);
            }
        });
    }

    private TextFormatter.Change filter(TextFormatter.Change change) {
        if (!change.getControlNewText().matches("([1-9]|1[0-1])")) {
            change.setText("");
        }
        return change;
    }
}
