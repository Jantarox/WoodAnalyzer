package com.jantarox.woodanalyzer.datamodel;

public class RingDuctArea implements Comparable<RingDuctArea> {
    public static final String MILLIMETER2 = "mm2";
    public static final String INCH2 = "in2";

    private short label;
    private int ringArea;
    private int ductArea;
    private int ppi = 400;

    public RingDuctArea(short label, int ringArea, int ductArea) {
        this.label = label;
        this.ringArea = ringArea;
        this.ductArea = ductArea;
    }

    public short getLabel() {
        return label;
    }

    public void setLabel(short label) {
        this.label = label;
    }

    public int getRingArea() {
        return ringArea;
    }

    public void setRingArea(int ringArea) {
        this.ringArea = ringArea;
    }

    public double getRealRingArea() {
        return getRealRingArea(MILLIMETER2);
    }

    public double getRealRingArea(String unit) {
        return getRealArea(ringArea, unit);
    }

    public int getDuctArea() {
        return ductArea;
    }

    public void setDuctArea(int ductArea) {
        this.ductArea = ductArea;
    }

    public double getRealDuctArea() {
        return getRealDuctArea(MILLIMETER2);
    }

    public double getRealDuctArea(String unit) {
        return getRealArea(ductArea, unit);
    }

    public int getPPI() {
        return ppi;
    }

    public void setPPI(int ppi) {
        this.ppi = ppi;
    }

    private double getRealArea(int area, String unit) {
        double inches2 = (double) area / ppi / ppi;
        switch (unit) {
            case MILLIMETER2 -> {
                return inches2 * 25.4 * 25.4;
            }
//            case INCH2 -> {
//                return inches;
//            }
            default -> {
                return inches2;
            }
        }
    }

    @Override
    public int compareTo(RingDuctArea o) {
        return this.label - o.label;
    }
}
