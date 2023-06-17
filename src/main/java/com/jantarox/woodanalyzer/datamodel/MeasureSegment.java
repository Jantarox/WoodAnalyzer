package com.jantarox.woodanalyzer.datamodel;

import javafx.geometry.Point2D;

public class MeasureSegment implements Comparable<MeasureSegment> {

    public static final String MILIMETER = "mm";
    public static final String INCH = "in";

    private final Point2D p1;
    private final Point2D p2;
    private int ppi = 400;

    public MeasureSegment(Point2D p1, Point2D p2) {
        Point2D vector = p2.subtract(p1);
        if (vector.getX() > 0) {
            this.p1 = p1;
            this.p2 = p2;
            return;
        } else if (vector.getX() == 0) {
            if (vector.getY() > 0) {
                this.p1 = p1;
                this.p2 = p2;
                return;
            }
        }
        this.p1 = p2;
        this.p2 = p1;
    }

    public MeasureSegment(double x1, double y1, double x2, double y2) {
        this(new Point2D(x1, y1), new Point2D(x2, y2));
    }

    public Point2D getP1() {
        return p1;
    }

    public Point2D getP2() {
        return p2;
    }

    public void setPPI(int ppi) {
        this.ppi = ppi;
    }

    public double getLength() {
        return getLength(MILIMETER);
    }

    public double getLength(String unit) {
        double inches = p1.distance(p2) / ppi;
        switch (unit) {
            case MILIMETER -> {
                return inches * 25.4;
            }
//            case INCH -> {
//                return inches;
//            }
            default -> {
                return inches;
            }
        }
    }

    @Override
    public int compareTo(MeasureSegment o) {
        Point2D vector = this.p1.subtract(o.p1);
        if (vector.getX() > 0) {
            return 1;
        } else if (vector.getX() == 0) {
            if (vector.getY() > 0) {
                return 1;
            } else if (vector.getY() == 0) {
                return 0;
            }
        }
        return -1;
    }
}
