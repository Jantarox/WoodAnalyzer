package com.jantarox.woodanalyzer.gestures;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.datamodel.MeasureSegment;
import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.List;

public class RemoveMeasureGestures extends MeasureGestures {

    private MeasureSegment highlightedMeasureSegment;
    private final EventHandler<MouseEvent> mouseClickedFilter = getOnMouseClickedEventHandler();

    public RemoveMeasureGestures(ZoomStackPane zoomStackPane, GraphicsContext context, ImageSegmentationModel imageSegmentationModel) {
        super(zoomStackPane, context, imageSegmentationModel);
    }

    @Override
    public void addGestures(Node node) {
        super.addGestures(node);
        node.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedFilter);
    }

    @Override
    public void removeGestures(Node node) {
        super.removeGestures(node);
        node.removeEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedFilter);
    }

    @Override
    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                dragView(event);
            }
            event.consume();
            renderSegmentation();
        };
    }

    public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
        return event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                tryRemovingMeasureSegment(event.getX(), event.getY());
            }
            event.consume();
            renderSegmentation();
        };
    }

    @Override
    public EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
        return event -> {
            updateCoordinates(event);
            highlightedMeasureSegment = findNearMeasureSegment(new Point2D(event.getX(), event.getY()), 10);
            renderSegmentation();
        };
    }

    private void tryRemovingMeasureSegment(double x, double y) {
        if (highlightedMeasureSegment != null) {
            imageSegmentationModel.removeMeasureSegment(highlightedMeasureSegment);
            highlightedMeasureSegment = null;
        }
    }

    private MeasureSegment findNearMeasureSegment(Point2D mousePoint, double distance) {
        double halfPixelSize = browsePane.getPixelSize() / 2;
        mousePoint = mousePoint.subtract(halfPixelSize, halfPixelSize);
        List<MeasureSegment> measureSegments = imageSegmentationModel.getMeasureSegments();
        for (MeasureSegment measureSegment : measureSegments) {
            Point2D p1 = browsePane.transformPoint(measureSegment.getP1());
            Point2D p2 = browsePane.transformPoint(measureSegment.getP2());

            if (p1.distance(mousePoint) <= distance || p2.distance(mousePoint) <= distance)
                return measureSegment;
        }
        return null;
    }

    @Override
    protected void renderMeasureSegments() {
        if (!imageSegmentationModel.isSegmentationLoaded())
            return;

        List<MeasureSegment> measureSegments = imageSegmentationModel.getMeasureSegments();
        for (MeasureSegment measureSegment : measureSegments) {
            if (measureSegment.equals(highlightedMeasureSegment))
                renderMeasureSegment(measureSegment.getP2(), measureSegment.getP1(), Color.RED);
            else
                renderMeasureSegment(measureSegment.getP2(), measureSegment.getP1(), Color.BLACK);
        }
    }
}
