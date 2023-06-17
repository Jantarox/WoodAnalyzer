module com.jantarox.woodanalyzer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires json.simple;

    opens com.jantarox.woodanalyzer to javafx.fxml;
    exports com.jantarox.woodanalyzer;
    exports com.jantarox.woodanalyzer.view;
    exports com.jantarox.woodanalyzer.controls;
    opens com.jantarox.woodanalyzer.view to javafx.fxml;
    exports com.jantarox.woodanalyzer.gestures;
    opens com.jantarox.woodanalyzer.gestures to javafx.fxml;
    exports com.jantarox.woodanalyzer.datamodel;
    opens com.jantarox.woodanalyzer.datamodel to javafx.fxml;
}