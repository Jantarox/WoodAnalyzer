package com.jantarox.woodanalyzer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BaseObservableModel implements ObservableModel {
    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Override
    public void addListener(String eventName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(eventName, listener);
    }

    @Override
    public void removeListener(String eventName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(eventName, listener);
    }
}
