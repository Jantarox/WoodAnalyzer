package com.jantarox.woodanalyzer.model;

import com.jantarox.woodanalyzer.datamodel.MeasureSegment;
import com.jantarox.woodanalyzer.datamodel.RingDuctArea;
import com.jantarox.woodanalyzer.datamodel.SegmentationPaths;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.List;

public interface ImageSegmentationModel extends ObservableModel {

    String imageSegmentationChanged = "imageSegmentationChanged";
    String segmentationPPIChanged = "segmentationPPIChanged";
    String measureSegmentsChanged = "measureSegmentsChanged";
    String measureAreasChanged = "measureAreasChanged";

    void load(SegmentationPaths paths);

    boolean isSegmentationLoaded();

    boolean isAreasCalculated();

    boolean wasModified();

    void reload();

    void save() throws IOException;

    Image getBackgroundImage();

    void drawSegmentationPoint(int x, int y, byte label);

    byte getSegmentationPoint(int x, int y);

    short getRingsArrayPoint(int x, int y);

    int getWidth();

    int getHeight();

    long getTotalArea();

    int getPPI();

    void setPPI(int ppi);

    double pixelsToUnit(double pixels, String unit);

    List<MeasureSegment> getMeasureSegments();

    List<RingDuctArea> getMeasureAreas();

    int getTotalResinDuctsArea();

    int getTotalResinDuctsCount();

    RingDuctArea getRingDuctArea(short label);

    void removeMeasureSegment(MeasureSegment measureSegment);

    void addMeasureSegment(Point2D p1, Point2D p2);
}
