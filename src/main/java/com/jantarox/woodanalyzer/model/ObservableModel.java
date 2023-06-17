package com.jantarox.woodanalyzer.model;

import java.beans.PropertyChangeListener;

public interface ObservableModel {

    void addListener(String eventName, PropertyChangeListener listener);

    void removeListener(String eventName, PropertyChangeListener listener);
}
