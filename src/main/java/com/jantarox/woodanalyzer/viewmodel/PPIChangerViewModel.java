package com.jantarox.woodanalyzer.viewmodel;

import com.jantarox.woodanalyzer.model.ImageSegmentationModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PPIChangerViewModel {

    private final StringProperty ppi = new SimpleStringProperty();
    private final BooleanProperty okButtonDisabled = new SimpleBooleanProperty();
    private final ImageSegmentationModel imageSegmentationModel;

    public PPIChangerViewModel(ImageSegmentationModel imageSegmentationModel) {
        this.imageSegmentationModel = imageSegmentationModel;
        this.ppi.setValue(String.valueOf(imageSegmentationModel.getPPI()));
        this.ppi.addListener((observable, oldValue, newValue) -> {
            okButtonDisabled.setValue(newValue.isEmpty());
        });
    }

    public void setPPI() {
        this.imageSegmentationModel.setPPI(Integer.parseInt(ppi.getValue()));
    }

    public BooleanProperty okButtonDisabledProperty() {
        return okButtonDisabled;
    }

    public StringProperty ppiProperty() {
        return ppi;
    }
}
