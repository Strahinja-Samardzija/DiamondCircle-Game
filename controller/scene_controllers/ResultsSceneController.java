package controller.scene_controllers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import logging.LoggerRegistration;
import model.results.ResultsListing;
import properties.PropertiesSingleton;
import property_keys_and_defaults.PropertyKeys;

public class ResultsSceneController {

    {
        try {
            new LoggerRegistration<>(ResultsSceneController.class).register("ResultsScene.log");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    ListView<String> resultsList;

    public void initialize() {
        resultsList.getItems().addAll(new ResultsListing().list());
        resultsList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldVal, newVal) -> {
                    String txtReaderExe = PropertiesSingleton.getInstance().getProperties()
                            .getProperty(PropertyKeys.DEFAULT_TXT_READER_KEY);
                    String resultsDir = PropertiesSingleton.getInstance().getProperties()
                            .getProperty(PropertyKeys.RESULTS_DIR_KEY);
                    if (txtReaderExe == null || resultsDir == null) {
                        Logger.getLogger(getClass().getName())
                                .severe("Could not load properties for displaying results.");
                    }

                    new Thread(() -> {
                        try {
                            new ProcessBuilder(txtReaderExe,
                                    resultsDir + File.separator + observable.getValue()).start();
                        } catch (IOException e) {
                            Logger.getLogger(getClass().getName()).severe("Could not open the default .txt editor.");
                        }
                    }, "Name: TxtEditor").start();
                });
    }
}
