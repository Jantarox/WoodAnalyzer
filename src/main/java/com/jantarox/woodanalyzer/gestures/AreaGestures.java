package com.jantarox.woodanalyzer.gestures;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.datamodel.Bounds;
import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;

public class AreaGestures extends BrowseGestures {

    private final IntegerProperty label = new SimpleIntegerProperty(0);
    protected final EventHandler<MouseEvent> mouseExitedFilter = getOnMouseExitedEventHandler();

    public AreaGestures(ZoomStackPane zoomStackPane, GraphicsContext context, ImageSegmentationModel imageSegmentationModel, IntegerProperty hoveredRingLabel) {
        super(zoomStackPane, context, imageSegmentationModel);
        hoveredRingLabel.bind(label);
    }

    @Override
    public void addGestures(Node node) {
        super.addGestures(node);
        node.addEventFilter(MouseEvent.MOUSE_EXITED, mouseExitedFilter);
    }

    @Override
    public void removeGestures(Node node) {
        super.removeGestures(node);
        node.removeEventFilter(MouseEvent.MOUSE_EXITED, mouseExitedFilter);
    }

    @Override
    public EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
        return event -> {
            try {
                Point2D coords = browsePane.inverseTransformPoint(event.getX(), event.getY());
                int xx = (int) Math.ceil(coords.getX());
                int yy = (int) Math.ceil(coords.getY());
                label.setValue(imageSegmentationModel.getRingsArrayPoint(xx, yy));
                coordinatesStringProperty.setValue(String.format("X: %.0f Y: %.0f Label: %d", coords.getX(), coords.getY(), label.get()));
            } catch (NonInvertibleTransformException e) {
                e.printStackTrace();
            }
            renderSegmentation();
        };
    }

    private EventHandler<MouseEvent> getOnMouseExitedEventHandler() {
        return event -> {
            label.setValue(0);
            renderSegmentation();
        };
    }

    @Override
    protected void renderSegmentation() {
        super.renderSegmentation();

        if (!imageSegmentationModel.isAreasCalculated() || this.label == null || this.label.get() == 0)
            return;

        Bounds bounds = calculateBounds();

        double pixelSize = browsePane.getPixelSize();
        this.context.setFill(Color.YELLOW);
        for (int x = bounds.getMinX(); x <= bounds.getMaxX(); x++) {
            for (int y = bounds.getMinY(); y <= bounds.getMaxY(); y++) {
                short label = imageSegmentationModel.getRingsArrayPoint(x, y);
                if (label == 0) continue;
                if (label == this.label.get()) {
                    Point2D coords = browsePane.transformPoint(x, y);
                    int xx = (int) Math.ceil(coords.getX());
                    int yy = (int) Math.ceil(coords.getY());
                    this.context.fillRect(xx, yy, pixelSize, pixelSize);
                }
            }
        }
    }
}
