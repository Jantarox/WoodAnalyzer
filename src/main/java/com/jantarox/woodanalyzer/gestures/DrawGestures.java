package com.jantarox.woodanalyzer.gestures;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.datamodel.Segmentation;
import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DrawGestures extends BrowseGestures {

    private final ObjectProperty<Byte> brushColorProperty;
    private final IntegerProperty brushRadiusProperty;
    private Point2D brushCoordinates;
    private final EventHandler<MouseEvent> mouseClickedFilter = getOnMouseClickedEventHandler();

    public DrawGestures(ZoomStackPane zoomStackPane, GraphicsContext context, ImageSegmentationModel imageSegmentationModel, ObjectProperty<Byte> brushColorProperty, IntegerProperty brushRadiusProperty) {
        super(zoomStackPane, context, imageSegmentationModel);
        this.brushColorProperty = brushColorProperty;
        this.brushRadiusProperty = brushRadiusProperty;
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
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                draw(event);
            } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                dragView(event);
            }
            event.consume();
            brushCoordinates = updateCoordinates(event);
            renderSegmentation();
        };
    }

    public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
        return event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
                return;
            }
            draw(event);
            renderSegmentation();
        };
    }


    @Override
    public EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
        return event -> {
            brushCoordinates = updateCoordinates(event);
            renderSegmentation();
        };
    }

    @Override
    protected void renderSegmentation() {
        super.renderSegmentation();
        renderBrush(brushCoordinates);
    }

    private void renderBrush(Point2D brushCoordinates) {
        if (!imageSegmentationModel.isSegmentationLoaded() || brushCoordinates == null)
            return;

        this.context.setFill(Segmentation.getBrushColor(brushColorProperty.getValue()));
        Set<Point2D> points = getCirclePoints(brushCoordinates.getX(), brushCoordinates.getY(), brushRadiusProperty.getValue());
        for (Point2D point : points) {
            Point2D coords = browsePane.transformPoint(point.getX(), point.getY());
            int xx = (int) Math.ceil(coords.getX());
            int yy = (int) Math.ceil(coords.getY());
            double pixelSize = browsePane.getPixelSize();
            this.context.fillRect(xx, yy, pixelSize, pixelSize);
        }
    }

    private void draw(MouseEvent event) {
        try {
            if (!imageSegmentationModel.isSegmentationLoaded()) {
                return;
            }
            Point2D coords = browsePane.inverseTransformPoint(event.getX(), event.getY());
            Set<Point2D> points = getCirclePoints(coords.getX(), coords.getY(), brushRadiusProperty.getValue());
            points.forEach(point -> imageSegmentationModel.drawSegmentationPoint((int) point.getX(), (int) point.getY(), brushColorProperty.getValue()));
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    private Set<Point2D> getCirclePoints(double x, double y, int radius) {
        if (radius == 0)
            return Collections.singleton(new Point2D(x, y));
        double x1, y1;
        Set<Point2D> points = new HashSet<>();

        double minAngle = Math.acos(1 - 1.0 / radius) * 0.9;

        for (double angle = 0; angle <= 360; angle += minAngle) {
            x1 = Math.round(radius * Math.cos(angle));
            y1 = Math.round(radius * Math.sin(angle));
            points.add(new Point2D(x + x1, y + y1));
        }

        fillCircle(x, y, points);

        return points;
    }

    private void fillCircle(double x, double y, Set<Point2D> points) {
        if (points.add(new Point2D(x, y))) {
            fillCircle(x + 1, y, points);
            fillCircle(x - 1, y, points);
            fillCircle(x, y + 1, points);
            fillCircle(x, y - 1, points);
        }
    }
}
