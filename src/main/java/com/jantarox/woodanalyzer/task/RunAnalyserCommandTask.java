package com.jantarox.woodanalyzer.task;

import com.jantarox.woodanalyzer.stream.StreamGobbler;
import javafx.concurrent.Task;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunAnalyserCommandTask extends Task<Void> {
    private final String command;
    private final String filename;

    public RunAnalyserCommandTask(String command, String filename) {
        this.command = command;
        this.filename = filename;
    }

    @Override
    protected Void call() throws Exception {
        Process process = null;
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            String imagesPath = Paths.get(System.getProperty("user.dir"), "images").toString();

//            processBuilder.command("C:\\Users\\jante\\AppData\\Local\\Programs\\ImageAnalyzer\\Python\\python.exe", "-u", "C:\\Users\\jante\\AppData\\Local\\Programs\\ImageAnalyzer\\ImageAnalyzer.launch.py", command, "-p", imagesPath, "-f", filename);
            processBuilder.command(".\\Python\\python.exe", "-u", ".\\ImageAnalyzer.launch.py", command, "-p", imagesPath, "-f", filename);
            processBuilder.directory(new File(System.getProperty("user.dir")));
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), this::updateMessage);

            service.submit(streamGobbler);
            process.waitFor();
        } catch (InterruptedException interruptedException) {
            if (isCancelled())
                process.destroy();
        } finally {
            service.shutdownNow();
        }
        return null;
    }
}
