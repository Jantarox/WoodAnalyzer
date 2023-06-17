package com.jantarox.woodanalyzer.controls;

import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

public class ZoomStackPane extends StackPane {

    public static final double DEFAULT_DELTA = 1.3d;
    private double delta = DEFAULT_DELTA;

    public ZoomStackPane() {
        super();
        this.getTransforms().setAll(new Translate());
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public void resetTransforms() {
        this.getTransforms().setAll(new Translate());
    }

    public void translate(double dx, double dy) throws NonInvertibleTransformException {
        Point2D delta = this.getTransforms().get(0).inverseDeltaTransform(dx, dy);
        Translate translate = new Translate(delta.getX(), delta.getY());
        Transform transform = this.getTransforms().get(0).createConcatenation(translate);
        this.getTransforms().setAll(transform);
    }

    public void scale(double direction, double x, double y) throws NonInvertibleTransformException {
        double scaleFactor = 1;
        if (direction < 0) {
            scaleFactor /= this.delta;
        } else {
            scaleFactor *= this.delta;
        }
        Point2D anchor = this.getTransforms().get(0).inverseTransform(x, y);

        Translate translateCenter = new Translate(-anchor.getX(), -anchor.getY());
        Scale scale = new Scale(scaleFactor, scaleFactor);
        Translate translateOrigin = new Translate(anchor.getX(), anchor.getY());

        Transform transform = translateOrigin.createConcatenation(scale.createConcatenation(translateCenter));
        transform = this.getTransforms().get(0).createConcatenation(transform);
        this.getTransforms().setAll(transform);
    }

    public Point2D transformPoint(double x, double y) {
        return this.getTransforms().get(0).transform(x, y);
    }

    public Point2D transformPoint(Point2D point2D) {
        return transformPoint(point2D.getX(), point2D.getY());
    }

    public Point2D inverseTransformPoint(double x, double y) throws NonInvertibleTransformException {
        Point2D point = this.getTransforms().get(0).inverseTransform(x, y);
        double roundedToPixelX = Math.floor(point.getX());
        double roundedToPixelY = Math.floor(point.getY());
        return new Point2D(roundedToPixelX, roundedToPixelY);
    }

    public Point2D inverseTransformPoint(Point2D point2D) throws NonInvertibleTransformException {
        return inverseTransformPoint(point2D.getX(), point2D.getY());
    }

    public Point2D transformDelta(double x, double y) {
        return this.getTransforms().get(0).deltaTransform(x, y);
    }

    public Point2D transformDelta(Point2D point2D) {
        return transformDelta(point2D.getX(), point2D.getY());
    }

    public Point2D inverseTransformDelta(double x, double y) throws NonInvertibleTransformException {
        return this.getTransforms().get(0).inverseDeltaTransform(x, y);
    }

    public Point2D inverseTransformDelta(Point2D point2D) throws NonInvertibleTransformException {
        return inverseTransformDelta(point2D.getX(), point2D.getY());
    }


    public double getPixelSize() {
        return Math.ceil(transformDelta(1, 0).magnitude());
    }
}
