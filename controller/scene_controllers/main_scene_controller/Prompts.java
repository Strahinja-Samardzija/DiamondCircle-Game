package controller.scene_controllers.main_scene_controller;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Prompts {
    void prompt(MainSceneController mainSceneController, String message) {
    	Label errorMessage = new Label(message);
    	errorMessage.setLayoutX(mainSceneController.convertor.getX(mainSceneController.dimension / 2));
    	errorMessage.setLayoutY(mainSceneController.convertor.getY(mainSceneController.dimension * mainSceneController.dimension - 1) + 30);
    	errorMessage.setBackground(new Background(new BackgroundFill(Color.WHEAT, null, null)));
    	FadeTransition fade = new FadeTransition(Duration.millis(3000), errorMessage);
    	fade.setFromValue(1);
    	fade.setToValue(0);
    
    	mainSceneController.gridCarrier.getChildren().add(errorMessage);
    	errorMessage.toFront();
    	fade.play();
    }
    
}
