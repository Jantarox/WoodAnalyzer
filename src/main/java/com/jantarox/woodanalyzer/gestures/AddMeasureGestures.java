package com.jantarox.woodanalyzer.gestures;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class AddMeasureGestures extends MeasureGestures {

    Point2D mouseCoordinates;
    private boolean measureDragged = false;
    private boolean measureCancelled = false;
    private boolean browseDragged = false;
    private Point2D measureTempAnchor;
    private final EventHandler<MouseEvent> mouseClickedFilter = getOnMouseClickedEventHandler();

    public AddMeasureGestures(ZoomStackPane zoomStackPane, GraphicsContext context, ImageSegmentationModel imageSegmentationModel) {
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
            if (event.getButton().equals(MouseButton.PRIMARY) && imageSegmentationModel.isSegmentationLoaded()) {
                mouseCoordinates = updateCoordinates(event);
                if (!measureDragged && measureTempAnchor == null && !measureCancelled) {
                    measureTempAnchor = mouseCoordinates;
                }
                measureDragged = true;
            } else if (event.getButton().equals(MouseButton.SECONDARY)) {
                dragView(event);
                browseDragged = true;
            }
            event.consume();
            renderSegmentation();
            if (measureTempAnchor != null) {
                renderMeasureSegment(measureTempAnchor, mouseCoordinates, Color.ORANGE);
            }
        };
    }

    public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
        return event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && imageSegmentationModel.isSegmentationLoaded()) {
                if (measureCancelled) {
                    measureCancelled = false;
                } else if (measureTempAnchor == null) {
                    measureTempAnchor = mouseCoordinates;
                    renderMeasureSegment(measureTempAnchor, mouseCoordinates, Color.ORANGE);
                } else {
                    imageSegmentationModel.addMeasureSegment(measureTempAnchor, mouseCoordinates);
                    measureTempAnchor = null;
                }
                measureDragged = false;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                if (!browseDragged) {
                    measureTempAnchor = null;
                    if (measureDragged)
                        measureCancelled = true;
                }
                browseDragged = false;
            }
            event.consume();
            renderSegmentation();
        };
    }

    @Override
    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return event -> {
            scaleView(event);
            renderSegmentation();
            if (this.measureTempAnchor != null) {
                renderMeasureSegment(this.measureTempAnchor, mouseCoordinates, Color.ORANGE);
            }
        };
    }

    @Override
    public EventHandler<MouseEvent> getOnMouseMovedEventHandler() {
        return event -> {
            mouseCoordinates = updateCoordinates(event);
            renderSegmentation();
            if (this.measureTempAnchor != null) {
                renderMeasureSegment(this.measureTempAnchor, mouseCoordinates, Color.ORANGE);
            }
        };
    }
}
