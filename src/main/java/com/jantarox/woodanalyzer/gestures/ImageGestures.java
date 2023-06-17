package com.jantarox.woodanalyzer.gestures;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.datamodel.Bounds;
import com.jantarox.woodanalyzer.datamodel.Segmentation;
import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;

import java.beans.PropertyChangeEvent;

public class ImageGestures implements AutoCloseable {
    public static final int BROWSE_GESTURES = 1;
    public static final int DRAW_GESTURES = 2;
    public static final int ADD_MEASURE_GESTURES = 3;
    public static final int REMOVE_MEASURE_GESTURES = 4;
    public static final int AREA_GESTURES = 5;
    protected ImageSegmentationModel imageSegmentationModel;
    ZoomStackPane browsePane;
    GraphicsContext context;
    StringProperty coordinatesStringProperty = new SimpleStringProperty();
    protected final EventHandler<MouseEvent> mouseMovedFilter = getOnMouseMovedEventHandler();

    public ImageGestures(ZoomStackPane browsePane, GraphicsContext context, ImageSegmentationModel imageSegmentationModel) {
        this.browsePane = browsePane;
        this.context = context;
        this.imageSegmentationModel = imageSegmentationModel;

        this.imageSegmentationModel.addListener(ImageSegmentationModel.imageSegmentationChanged, this::renderSegmentation);
        renderSegmentation();
    }

    @Override
    public void close() throws Exception {
        this.imageSegmentationModel.removeListener(ImageSegmentationModel.imageSegmentationChanged, this::renderSegmentation);
    }

    public void addGestures(Node node) {
        node.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedFilter);
    }

    public void removeGestures(Node node) {
        node.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedFilter);
    }

    public EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
        return this::updateCoordinates;
    }

    protected Bounds calculateBounds() {
        if (!imageSegmentationModel.isSegmentationLoaded())
            return null;

        int maxX = imageSegmentationModel.getWidth();
        int maxY = imageSegmentationModel.getHeight();
        int minX = 0;
        int minY = 0;

        Point2D maxBounds = null;
        Point2D minBounds = null;
        try {
            maxBounds = browsePane.inverseTransformPoint(this.context.getCanvas().getWidth(), this.context.getCanvas().getWidth());
            minBounds = browsePane.inverseTransformPoint(0, 0);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }


        if (maxBounds.getX() < maxX) maxX = (int) maxBounds.getX();
        if (maxBounds.getY() < maxY) maxY = (int) maxBounds.getY();
        if (minBounds.getX() > minX) minX = (int) minBounds.getX();
        if (minBounds.getY() > minY) minY = (int) minBounds.getY();

        return new Bounds(minX, minY, maxX, maxY);
    }

    protected void renderSegmentation(PropertyChangeEvent event) {
        renderSegmentation();
    }

    protected void renderSegmentation() {
        this.context.clearRect(0, 0, this.context.getCanvas().getWidth(), this.context.getCanvas().getHeight());

        if (!imageSegmentationModel.isSegmentationLoaded())
            return;

        Bounds bounds = calculateBounds();

        double pixelSize = browsePane.getPixelSize();

        for (int x = bounds.getMinX(); x <= bounds.getMaxX(); x++) {
            for (int y = bounds.getMinY(); y <= bounds.getMaxY(); y++) {
                byte type = imageSegmentationModel.getSegmentationPoint(x, y);
                if (type == Segmentation.BACKGROUND) continue;
                this.context.setFill(Segmentation.getColor(type));

                Point2D coords = browsePane.transformPoint(x, y);
                int xx = (int) Math.ceil(coords.getX());
                int yy = (int) Math.ceil(coords.getY());
                this.context.fillRect(xx, yy, pixelSize, pixelSize);
            }
        }
    }

    protected Point2D updateCoordinates(MouseEvent event) {
        try {
            Point2D coords = browsePane.inverseTransformPoint(event.getX(), event.getY());
            coordinatesStringProperty.setValue(String.format("X: %.0f Y: %.0f", coords.getX(), coords.getY()));
            return coords;
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StringProperty coordinatesStringProperty() {
        return coordinatesStringProperty;
    }
}
