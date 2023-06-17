package com.jantarox.woodanalyzer.gestures;

import com.jantarox.woodanalyzer.controls.ZoomStackPane;
import com.jantarox.woodanalyzer.datamodel.MeasureSegment;
import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class MeasureGestures extends BrowseGestures {

    public MeasureGestures(ZoomStackPane zoomStackPane, GraphicsContext context, ImageSegmentationModel imageSegmentationModel) {
        super(zoomStackPane, context, imageSegmentationModel);
        this.imageSegmentationModel.addListener(ImageSegmentationModel.imageSegmentationChanged, this::renderMeasureSegments);
    }

    @Override
    public void close() throws Exception {
        super.close();
        this.imageSegmentationModel.removeListener(ImageSegmentationModel.imageSegmentationChanged, this::renderMeasureSegments);
    }

    @Override
    protected void renderSegmentation() {
        super.renderSegmentation();
        renderMeasureSegments();
    }

    protected void renderMeasureSegments(PropertyChangeEvent event) {
        renderMeasureSegments();
    }

    protected void renderMeasureSegments() {
        if (!imageSegmentationModel.isSegmentationLoaded())
            return;

        List<MeasureSegment> measureSegments = imageSegmentationModel.getMeasureSegments();
        for (MeasureSegment measureSegment : measureSegments) {
            renderMeasureSegment(measureSegment.getP2(), measureSegment.getP1(), Color.BLACK);
        }
    }

    protected void renderMeasureSegment(Point2D p1, Point2D p2, Color color) {
        this.context.setFill(color);
        this.context.setStroke(color);
        p1 = browsePane.transformPoint(p1);
        p2 = browsePane.transformPoint(p2);
        double halfPixelSize = browsePane.getPixelSize() / 2;
        p1 = p1.add(halfPixelSize, halfPixelSize);
        p2 = p2.add(halfPixelSize, halfPixelSize);

        renderMeasurePoint(p1);
        renderMeasurePoint(p2);
        this.context.setLineWidth(2);
        this.context.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

        double parallelsLength = browsePane.transformDelta(imageSegmentationModel.getHeight(), 0).magnitude() / 4;
        Point2D vector = p2.subtract(p1).normalize().multiply(parallelsLength);
        // Vector perpendicular clockwise
        Point2D vectorPC = new Point2D(vector.getY(), -vector.getX());
        // Vector perpendicular counter clockwise
        Point2D vectorPCC = new Point2D(-vector.getY(), vector.getX());

        this.context.strokeLine(p1.getX(), p1.getY(), p1.getX() + vectorPC.getX(), p1.getY() + vectorPC.getY());
        this.context.strokeLine(p1.getX(), p1.getY(), p1.getX() + vectorPCC.getX(), p1.getY() + vectorPCC.getY());
        this.context.strokeLine(p2.getX(), p2.getY(), p2.getX() + vectorPC.getX(), p2.getY() + vectorPC.getY());
        this.context.strokeLine(p2.getX(), p2.getY(), p2.getX() + vectorPCC.getX(), p2.getY() + vectorPCC.getY());


    }

    protected void renderMeasurePoint(Point2D point2D) {
        int xx = (int) Math.ceil(point2D.getX());
        int yy = (int) Math.ceil(point2D.getY());
        int radius = 5;
        this.context.fillOval(xx - radius, yy - radius, 2 * radius, 2 * radius);
    }
}
