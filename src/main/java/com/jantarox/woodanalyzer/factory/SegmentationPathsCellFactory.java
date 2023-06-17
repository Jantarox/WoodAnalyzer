package com.jantarox.woodanalyzer.factory;

import com.jantarox.woodanalyzer.datamodel.SegmentationPaths;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

public class SegmentationPathsCellFactory implements Callback<ListView<SegmentationPaths>, ListCell<SegmentationPaths>> {
    @Override
    public ListCell<SegmentationPaths> call(ListView<SegmentationPaths> param) {
        return new ListCell<>() {
            @Override
            public void updateItem(SegmentationPaths segmentationPaths, boolean empty) {
                super.updateItem(segmentationPaths, empty);

                if (empty || segmentationPaths == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(segmentationPaths.getImageFilename());
                    Circle circle = new Circle(5, segmentationPaths.segmentationExists() ? Color.GREEN : Color.GREY);
                    circle.setStroke(Color.BLACK);
                    circle.setStrokeWidth(1);
                    Tooltip.install(circle, new Tooltip(segmentationPaths.segmentationExists() ? "Segmented" : "Unsegmented"));
                    setGraphic(circle);
                }
            }
        };
    }
}
