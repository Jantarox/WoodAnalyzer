package com.jantarox.woodanalyzer.factory;

import com.jantarox.woodanalyzer.datamodel.MeasureSegment;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class MeasureSegmentCellFactory implements Callback<ListView<MeasureSegment>, ListCell<MeasureSegment>> {
    @Override
    public ListCell<MeasureSegment> call(ListView<MeasureSegment> param) {
        return new ListCell<>() {
            @Override
            public void updateItem(MeasureSegment measureSegment, boolean empty) {
                super.updateItem(measureSegment, empty);

                if (empty || measureSegment == null) {
                    setText(null);
                } else {
                    setText(String.format(
                            "%d. [(%d, %d), (%d, %d)] - %.2fmm",
                            getIndex() + 1,
                            (int) measureSegment.getP1().getX(),
                            (int) measureSegment.getP1().getY(),
                            (int) measureSegment.getP2().getX(),
                            (int) measureSegment.getP2().getY(),
                            measureSegment.getLength(MeasureSegment.MILIMETER)
                    ));
                }
            }
        };
    }
}
