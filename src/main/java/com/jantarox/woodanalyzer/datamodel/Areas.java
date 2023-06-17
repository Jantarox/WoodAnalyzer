package com.jantarox.woodanalyzer.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Areas {
    private int resinDuctsArea;
    private int resinDuctsCount;
    private Map<Short, RingDuctArea> areasPerRing;

    public Areas(int resinDuctsArea, int resinDuctsCount, Map<Short, RingDuctArea> areasPerRing) {
        this.resinDuctsArea = resinDuctsArea;
        this.resinDuctsCount = resinDuctsCount;
        this.areasPerRing = areasPerRing;
    }

    public int getResinDuctsArea() {
        return resinDuctsArea;
    }

    public void setResinDuctsArea(int resinDuctsArea) {
        this.resinDuctsArea = resinDuctsArea;
    }

    public int getResinDuctsCount() {
        return resinDuctsCount;
    }

    public void setResinDuctsCount(int resinDuctsCount) {
        this.resinDuctsCount = resinDuctsCount;
    }

    public RingDuctArea getRingDuctArea(short label) {
        return areasPerRing.get(label);
    }

    public List<RingDuctArea> getAreasPerRingList() {
        List<RingDuctArea> ringDuctAreaList = new ArrayList<>(areasPerRing.values());
        Collections.sort(ringDuctAreaList);
        return ringDuctAreaList;
    }

    public void setAreasPerRing(Map<Short, RingDuctArea> areasPerRing) {
        this.areasPerRing = areasPerRing;
    }
}
