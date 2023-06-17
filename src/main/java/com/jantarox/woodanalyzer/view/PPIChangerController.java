package com.jantarox.woodanalyzer.view;

import com.jantarox.woodanalyzer.handler.ViewHandler;
import com.jantarox.woodanalyzer.viewmodel.PPIChangerViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class PPIChangerController {

    PPIChangerViewModel ppiChangerViewModel;
    ViewHandler viewHandler;

    @FXML
    private TextField ppiTextField;
    @FXML
    private Button okButton;

    public void init(ViewHandler viewHandler, PPIChangerViewModel ppiChangerViewModel) {
        this.ppiChangerViewModel = ppiChangerViewModel;
        this.viewHandler = viewHandler;

        ppiTextField.textProperty().bindBidirectional(ppiChangerViewModel.ppiProperty());
        ppiTextField.setTextFormatter(new TextFormatter<>(this::filter));

        okButton.disableProperty().bind(ppiChangerViewModel.okButtonDisabledProperty());
    }

    public void onOk() {
        this.ppiChangerViewModel.setPPI();
        this.viewHandler.closeView(ViewHandler.PPI_CHANGER_VIEW_NAME);
    }

    public void onCancel() {
        this.viewHandler.closeView(ViewHandler.PPI_CHANGER_VIEW_NAME);
    }

    private TextFormatter.Change filter(TextFormatter.Change change) {
        if (!change.getControlNewText().matches("([1-9]|[1-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9][0-9][0-9])")) {
            change.setText("");
        }
        return change;
    }
}
