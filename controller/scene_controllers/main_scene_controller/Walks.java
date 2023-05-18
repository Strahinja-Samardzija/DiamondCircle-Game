package controller.scene_controllers.main_scene_controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import controller.scene_controllers.main_scene_controller.my_animation_timer.MyAnimationTimer;
import javafx.application.Platform;
import logging.LoggerRegistration;
import model.FiguresWalk;
import model.path.Tile;
import presentation.Diamond;
import presentation.FigureVector;

public class Walks {

	{
		try {
			new LoggerRegistration<>(Walks.class).register("MainScene.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

    public void collectDiamond(MainSceneController mainSceneController, Tile tile) {
    	Diamond diamond = mainSceneController.diamonds.remove(tile.getNumber());
    	if (diamond != null) {
    		mainSceneController.gridCarrier.getChildren().remove(diamond);
    	}
    
    }

    public void stepAndSeekDiamonds(MainSceneController mainSceneController, FiguresWalk walk, Tile previousTile, Tile nextTile) {
    	final FigureVector vector = mainSceneController.figures.get(walk.getId());
    	MyAnimationTimer stepForward = mainSceneController.getStepTransition(vector, walk, previousTile, nextTile);
		
    	Platform.runLater(vector::stepForward);
    	Platform.runLater(stepForward::start);
    	try {
    		synchronized (stepForward) {
    			while (!stepForward.isFinished())
    				stepForward.wait();
    		}
    	} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
			Thread.currentThread().interrupt();
		}
    }

    public void endWalkSuccesssfuly(MainSceneController mainSceneController, FiguresWalk walk) {
    	mainSceneController.removers.removeWalks(mainSceneController, List.of(walk));
    }

    public void resolveConflict(MainSceneController mainSceneController) {
    	Platform.runLater(() -> mainSceneController.prompts.prompt(mainSceneController, "Polje je zauzeto. Korak naprijed."));
    }
    
}
