package com.jantarox.woodanalyzer.gestures;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.NonInvertibleTransformException;

public class BrowseGestures extends ImageGestures {

    protected final EventHandler<ScrollEvent> scrollFilter = getOnScrollEventHandler();
    protected Point2D mouseAnchor;
    protected final EventHandler<MouseEvent> mousePressedFilter = getOnMousePressedEventHandler();
    protected final EventHandler<MouseEvent> mouseDraggedFilter = getOnMouseDraggedEventHandler();

    public BrowseGestures(ZoomStackPane zoomStackPane, GraphicsContext context, ImageSegmentationModel imageSegmentationModel) {
        super(zoomStackPane, context, imageSegmentationModel);
    }

    @Override
    public void addGestures(Node node) {
        super.addGestures(node);
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedFilter);
        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedFilter);
        node.addEventFilter(ScrollEvent.ANY, scrollFilter);
    }

    @Override
    public void removeGestures(Node node) {
        super.removeGestures(node);
        node.removeEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedFilter);
        node.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedFilter);
        node.removeEventFilter(ScrollEvent.ANY, scrollFilter);
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return event -> mouseAnchor = new Point2D(event.getX(), event.getY());
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return event -> {
            dragView(event);
            event.consume();
            renderSegmentation();
        };
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return event -> {
            scaleView(event);
            event.consume();
            renderSegmentation();
        };
    }

    protected void dragView(MouseEvent event) {
        try {
            double dx = event.getX() - mouseAnchor.getX();
            double dy = event.getY() - mouseAnchor.getY();
            browsePane.translate(dx, dy);
            mouseAnchor = new Point2D(event.getX(), event.getY());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    protected void scaleView(ScrollEvent event) {
        try {
            browsePane.scale(event.getDeltaY(), event.getX(), event.getY());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
    }
}
