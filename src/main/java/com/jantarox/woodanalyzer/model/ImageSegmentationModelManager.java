package com.jantarox.woodanalyzer.model;

import com.jantarox.woodanalyzer.datamodel.MeasureSegment;
import com.jantarox.woodanalyzer.datamodel.RingDuctArea;
import com.jantarox.woodanalyzer.datamodel.Segmentation;
import com.jantarox.woodanalyzer.datamodel.SegmentationPaths;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageSegmentationModelManager extends BaseObservableModel implements ImageSegmentationModel {

    private Image backgroundImage;
    private Segmentation segmentation;
    private SegmentationPaths paths;
    private boolean wasModified = false;

    public ImageSegmentationModelManager() {
    }

    @Override
    public void load(SegmentationPaths paths) {
        this.paths = paths;
        backgroundImage = new Image(paths.getImagePath());
        try {
            this.segmentation = Segmentation.loadFile(paths.getSegmentationPath());
        } catch (IOException | ClassNotFoundException | ParseException e) {
            this.segmentation = new Segmentation((int) backgroundImage.getWidth(), (int) backgroundImage.getHeight());
        }
        wasModified = false;
        propertyChangeSupport.firePropertyChange(ImageSegmentationModel.imageSegmentationChanged, null, null);
        propertyChangeSupport.firePropertyChange(ImageSegmentationModel.measureSegmentsChanged, null, null);
        propertyChangeSupport.firePropertyChange(ImageSegmentationModel.segmentationPPIChanged, null, null);
        propertyChangeSupport.firePropertyChange(ImageSegmentationModel.measureAreasChanged, null, null);
    }

    @Override
    public boolean isSegmentationLoaded() {
        return segmentation != null && backgroundImage != null;
    }

    @Override
    public boolean isAreasCalculated() {
        return isSegmentationLoaded() && segmentation.isRingsArrayCalculated();
    }

    @Override
    public boolean wasModified() {
        return wasModified;
    }

    @Override
    public void reload() {
        if (isSegmentationLoaded())
            load(paths);
    }

    @Override
    public void save() throws IOException {
        segmentation.saveFile(paths.getSegmentationPath());
        wasModified = false;
    }

    @Override
    public Image getBackgroundImage() {
        return backgroundImage;
    }

    @Override
    public void drawSegmentationPoint(int x, int y, byte label) {
        if (segmentation.drawSegmentationPoint(x, y, label))
            wasModified = true;
    }

    @Override
    public byte getSegmentationPoint(int x, int y) {
        return segmentation.getSegmentationPoint(x, y);
    }

    @Override
    public short getRingsArrayPoint(int x, int y) {
        return segmentation.getRingsArrayPoint(x, y);
    }

    @Override
    public int getWidth() {
        return (int) backgroundImage.getWidth();
    }

    @Override
    public int getHeight() {
        return (int) backgroundImage.getHeight();
    }

    @Override
    public long getTotalArea() {
        return (long) getHeight() * getWidth();
    }

    @Override
    public int getPPI() {
        return segmentation.getPpi();
    }

    @Override
    public void setPPI(int ppi) {
        if (segmentation.setPPI(ppi)) {
            wasModified = true;
            propertyChangeSupport.firePropertyChange(ImageSegmentationModel.segmentationPPIChanged, null, null);
        }
    }

    @Override
    public double pixelsToUnit(double pixels, String unit) {
        return segmentation.pixelsToUnit(pixels, unit);
    }

    @Override
    public List<MeasureSegment> getMeasureSegments() {
        return segmentation.getMeasureSegments();
    }

    @Override
    public List<RingDuctArea> getMeasureAreas() {
        if (segmentation.getAreas() != null)
            return segmentation.getAreas().getAreasPerRingList();
        return new ArrayList<>();
    }

    @Override
    public int getTotalResinDuctsArea() {
        if (segmentation.getAreas() != null)
            return segmentation.getAreas().getResinDuctsArea();
        return 0;
    }

    @Override
    public int getTotalResinDuctsCount() {
        if (segmentation.getAreas() != null)
            return segmentation.getAreas().getResinDuctsCount();
        return 0;
    }

    @Override
    public RingDuctArea getRingDuctArea(short label) {
        if (segmentation.getAreas() != null)
            return segmentation.getAreas().getRingDuctArea(label);
        return null;
    }

    @Override
    public void removeMeasureSegment(MeasureSegment measureSegment) {
        if (segmentation.removeMeasureSegment(measureSegment)) {
            wasModified = true;
            propertyChangeSupport.firePropertyChange(ImageSegmentationModel.measureSegmentsChanged, null, null);
        }

    }

    @Override
    public void addMeasureSegment(Point2D p1, Point2D p2) {
        if (segmentation.addMeasureSegment(p1, p2)) {
            wasModified = true;
            propertyChangeSupport.firePropertyChange(ImageSegmentationModel.measureSegmentsChanged, null, null);
        }
    }
}
