package com.jantarox.woodanalyzer.factory;

import com.jantarox.woodanalyzer.datamodel.RingDuctArea;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class RingDuctAreaCellFactory implements Callback<ListView<RingDuctArea>, ListCell<RingDuctArea>> {
    @Override
    public ListCell<RingDuctArea> call(ListView<RingDuctArea> param) {
        return new ListCell<>() {
            @Override
            public void updateItem(RingDuctArea ringDuctArea, boolean empty) {
                super.updateItem(ringDuctArea, empty);

                if (empty || ringDuctArea == null) {
                    setText(null);
                } else {
                    setText(String.format(
                            "%02d - [ring: %.2fmm2, ducts:%.2fmm2]",
                            ringDuctArea.getLabel(),
                            ringDuctArea.getRealRingArea(RingDuctArea.MILLIMETER2),
                            ringDuctArea.getRealDuctArea(RingDuctArea.MILLIMETER2)
                    ));
                }
            }
        };
    }
}
