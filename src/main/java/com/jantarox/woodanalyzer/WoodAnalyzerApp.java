package com.jantarox.woodanalyzer;

import com.jantarox.woodanalyzer.factory.ModelFactory;
import com.jantarox.woodanalyzer.factory.ViewModelFactory;
import com.jantarox.woodanalyzer.handler.ViewHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class WoodAnalyzerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ModelFactory mf = new ModelFactory();
        ViewModelFactory vmf = new ViewModelFactory(mf);
        ViewHandler vh = new ViewHandler(vmf);
        vh.start();
    }
}