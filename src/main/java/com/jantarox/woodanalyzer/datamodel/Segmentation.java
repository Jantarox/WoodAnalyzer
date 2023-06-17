package com.jantarox.woodanalyzer.datamodel;

import com.jantarox.woodanalyzer.stream.CoderRLE;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Segmentation {
    public static final String MILLIMETER = "mm";
    public static final String INCH = "in";
    public static final String MILLIMETER2 = "mm2";
    public static final String INCH2 = "in2";

    public static final byte BACKGROUND = 0;
    public static final byte GROWTH_RING = 1;
    public static final byte RESIN_DUCT = 2;

    public static Color GROWTH_RING_BRUSH_COLOR = Color.BLUE;
    public static Color RESIN_DUCT_BRUSH_COLOR = Color.GREEN;
    public static Color BACKGROUND_BRUSH_COLOR = Color.GREY;

    private final byte[] segmentationArray;
    private final short[] ringsArray;
    private final int width;
    private final int height;
    private final List<MeasureSegment> measureSegments;
    private final Areas areas;
    private int ppi;

    public Segmentation(int width, int height) {
        this.width = width;
        this.height = height;
        this.segmentationArray = new byte[width * height];
        this.ringsArray = null;
        this.areas = null;
        this.measureSegments = new ArrayList<>();
        this.ppi = 400;
    }

    public Segmentation(int width, int height, byte[] segmentationArray, short[] ringsArray, List<MeasureSegment> measureSegments, Areas areas, int ppi) {
        this.width = width;
        this.height = height;
        this.segmentationArray = segmentationArray;
        this.ringsArray = ringsArray;
        this.measureSegments = measureSegments;
        this.areas = areas;
        this.ppi = ppi;
        measureSegments.forEach(measureSegment -> measureSegment.setPPI(this.ppi));
        Collections.sort(this.measureSegments);
        if (areas != null) {
            areas.getAreasPerRingList().forEach(ringDuctArea -> ringDuctArea.setPPI(this.ppi));
        }
    }

    public static Segmentation loadFile(String path) throws IOException, ClassNotFoundException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject segmentationJSON = (JSONObject) parser.parse(new FileReader(path));
        CoderRLE rle = new CoderRLE();
        int width = ((Long) segmentationJSON.get("width")).intValue();
        int height = ((Long) segmentationJSON.get("height")).intValue();
        int ppi = ((Long) segmentationJSON.get("ppi")).intValue();
        byte[] segmentationArray = rle.decodeByteArray((String) segmentationJSON.get("segmentationArray"), width, height);
        String encodedRingsArray = (String) segmentationJSON.get("ringsArray");
        short[] ringsArray = encodedRingsArray != null
                ? rle.decodeShortArray(encodedRingsArray, width, height)
                : null;

        JSONArray measureSegmentsJSON = (JSONArray) segmentationJSON.get("measureSegments");
        Iterator<JSONArray> segmentsIterator = measureSegmentsJSON.iterator();
        List<MeasureSegment> measureSegments = new ArrayList<>();
        while (segmentsIterator.hasNext()) {
            JSONArray measureSegmentJSON = segmentsIterator.next();
            Iterator<Long> segmentIterator = measureSegmentJSON.iterator();
            double x1 = segmentIterator.next().doubleValue();
            double y1 = segmentIterator.next().doubleValue();
            double x2 = segmentIterator.next().doubleValue();
            double y2 = segmentIterator.next().doubleValue();
            measureSegments.add(new MeasureSegment(x1, y1, x2, y2));
        }

        JSONObject areasJSON = (JSONObject) segmentationJSON.get("areas");
        Areas areas = null;
        if (areasJSON != null) {
            Map<Short, RingDuctArea> areasPerRing = new HashMap<>();

            int resinDuctsArea = ((Long) areasJSON.get("resinDuctsArea")).intValue();
            int resinDuctsCount = ((Long) areasJSON.get("resinDuctsCount")).intValue();
            JSONObject areasPerRingJSON = (JSONObject) areasJSON.get("areasPerRing");
            areasPerRingJSON.forEach((k, v) -> {
                Short label = Short.parseShort((String) k);
                JSONObject ringDuctAreaJSON = (JSONObject) v;
                RingDuctArea ringDuctArea = new RingDuctArea(
                        label,
                        ((Long) ringDuctAreaJSON.get("ringArea")).intValue(),
                        ((Long) ringDuctAreaJSON.get("ductArea")).intValue()
                );
                areasPerRing.put(label, ringDuctArea);
            });
            areas = new Areas(resinDuctsArea, resinDuctsCount, areasPerRing);
        }

        return new Segmentation(width, height, segmentationArray, ringsArray, measureSegments, areas, ppi);
    }

    public static Color getColor(byte type) {
        if (type == Segmentation.GROWTH_RING) return GROWTH_RING_BRUSH_COLOR;
        else if (type == Segmentation.RESIN_DUCT) return RESIN_DUCT_BRUSH_COLOR;
        else throw new IllegalArgumentException();
    }

    public static Color getBrushColor(byte type) {
        if (type == Segmentation.GROWTH_RING) return GROWTH_RING_BRUSH_COLOR;
        else if (type == Segmentation.RESIN_DUCT) return RESIN_DUCT_BRUSH_COLOR;
        else return BACKGROUND_BRUSH_COLOR;
    }

    public void saveFile(String path) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("width", width);
        jsonObject.put("height", height);
        jsonObject.put("ppi", ppi);
        CoderRLE rle = new CoderRLE();
        jsonObject.put("segmentationArray", rle.encodeByteArray(segmentationArray, width * height));
        jsonObject.put("ringsArray", ringsArray != null ? rle.encodeShortArray(ringsArray, width * height) : null);
        JSONObject areasObject = null;
        if (areas != null) {
            areasObject = new JSONObject();
            areasObject.put("resinDuctsArea", areas.getResinDuctsArea());
            areasObject.put("resinDuctsCount", areas.getResinDuctsCount());
            JSONObject areasPerRingObject = new JSONObject();
            for (RingDuctArea ringDuctArea : areas.getAreasPerRingList()) {
                JSONObject areaPerRingObject = new JSONObject();
                areaPerRingObject.put("ringArea", ringDuctArea.getRingArea());
                areaPerRingObject.put("ductArea", ringDuctArea.getDuctArea());
                areasPerRingObject.put(String.valueOf(ringDuctArea.getLabel()), areaPerRingObject);
            }
            areasObject.put("areasPerRing", areasPerRingObject);
        }
        jsonObject.put("areas", areasObject);
        JSONArray measureSegmentsJSON = new JSONArray();
        for (MeasureSegment measureSegment : measureSegments) {
            JSONArray measureSegmentJSON = new JSONArray();
            measureSegmentJSON.add(Double.valueOf(measureSegment.getP1().getX()).longValue());
            measureSegmentJSON.add(Double.valueOf(measureSegment.getP1().getY()).longValue());
            measureSegmentJSON.add(Double.valueOf(measureSegment.getP2().getX()).longValue());
            measureSegmentJSON.add(Double.valueOf(measureSegment.getP2().getY()).longValue());
            measureSegmentsJSON.add(measureSegmentJSON);
        }
        jsonObject.put("measureSegments", measureSegmentsJSON);
        Files.write(Paths.get(path), jsonObject.toJSONString().getBytes());
    }

    public boolean isRingsArrayCalculated() {
        return ringsArray != null && areas != null;
    }

    public boolean drawSegmentationPoint(int x, int y, byte type) {
        try {
            this.segmentationArray[x + y * width] = type;
            return true;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return false;
        }
    }

    public byte getSegmentationPoint(int x, int y) {
        if (x >= width || y >= height)
            return (byte) 0;
        try {
            return segmentationArray[x + y * width];
        } catch (ArrayIndexOutOfBoundsException e) {
            return (byte) 0;
        }
    }

    public short getRingsArrayPoint(int x, int y) {
        if (ringsArray == null || x >= width || y >= height)
            return (byte) 0;
        try {
            return ringsArray[x + y * width];
        } catch (ArrayIndexOutOfBoundsException e) {
            return (byte) 0;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPpi() {
        return ppi;
    }

    public boolean setPPI(int ppi) {
        this.ppi = ppi;
        measureSegments.forEach(measureSegment -> measureSegment.setPPI(this.ppi));
        if (areas != null) {
            areas.getAreasPerRingList().forEach(ringDuctArea -> ringDuctArea.setPPI(this.ppi));
        }
        return true;
    }

    public double pixelsToUnit(double pixels, String unit) {
        switch (unit) {
            case MILLIMETER -> {
                return pixels / ppi * 25.4;
            }
            case MILLIMETER2 -> {
                return pixels / (ppi * ppi) * (25.4 * 25.4);
            }
//            case INCH -> {
//                return inches;
//            }
            case INCH2 -> {
                return pixels / (ppi * ppi);
            }
            default -> {
                return pixels / ppi;
            }
        }
    }

    public List<MeasureSegment> getMeasureSegments() {
        return measureSegments;
    }

    public boolean addMeasureSegment(Point2D p1, Point2D p2) {
        MeasureSegment measureSegment = new MeasureSegment(p1, p2);
        measureSegment.setPPI(ppi);
        this.measureSegments.add(measureSegment);
        Collections.sort(this.measureSegments);
        return true;
    }

    public boolean removeMeasureSegment(MeasureSegment measureSegment) {
        this.measureSegments.remove(measureSegment);
        Collections.sort(this.measureSegments);
        return true;
    }

    public Areas getAreas() {
        return areas;
    }
}
