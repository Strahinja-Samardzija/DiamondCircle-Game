package controller.scene_controllers.main_scene_controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import controller.scene_controllers.main_scene_controller.my_animation_timer.MyAnimationTimer;
import controller.scene_controllers.main_scene_controller.my_animation_timer.Pause;
import javafx.application.Platform;
import logging.LoggerRegistration;
import model.path.Tile;
import presentation.Diamond;
import presentation.Hole;

public class Spawns {

	{
		try {
			new LoggerRegistration<>(Spawns.class).register("MainScene.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public void spawnHoles(MainSceneController mainSceneController, List<Tile> tilesWithHoles) throws AssertionError {
		for (Tile tile : tilesWithHoles) {
				Hole hole = new Hole();
				hole.setLayoutX(mainSceneController.convertor.getX(tile.getNumber()) - Hole.HOLE_SIZE / 2);
				hole.setLayoutY(mainSceneController.convertor.getY(tile.getNumber()) - Hole.HOLE_SIZE / 2);
				mainSceneController.holes.add(hole);
		}

		Runnable task = () -> mainSceneController.gridCarrier.getChildren().addAll(mainSceneController.holes);
		mainSceneController.waitToUpdateScene(task);
		
		Pause.pause(mainSceneController, 500);
	}

	public void spawnDiamonds(final MainSceneController mainSceneController, List<Tile> tilesWithDiamonds) throws AssertionError {
		for (Tile tile : tilesWithDiamonds) {
			Diamond diamond = new Diamond();
			diamond.setTranslateX(mainSceneController.convertor.getX(tile.getNumber()) - Diamond.DIAMOND_SIZE / 2);
			diamond.setTranslateY(mainSceneController.convertor.getY(tile.getNumber()) - Diamond.DIAMOND_SIZE / 2);
			mainSceneController.diamonds.put(tile.getNumber(), diamond);
		}

		MyAnimationTimer diamondTimer = new MyAnimationTimer(mainSceneController,
				controller.tasks.SpawningDiamondsTask.SPAWN_DIAMONDS_INTERVAL * 1_000_000) {
			private volatile boolean isStarted;

			@Override
			protected void update(long now) {
				if (!isStarted) {
					isStarted = true;
					Platform.runLater(() -> mainSceneController.gridCarrier.getChildren()
							.addAll(mainSceneController.diamonds.values()));
				}
			}
		};

		Platform.runLater(diamondTimer::start);
		synchronized (diamondTimer) {
			try {
				while (!diamondTimer.isFinished())
					diamondTimer.wait();
			} catch (InterruptedException e) {
				Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
				Thread.currentThread().interrupt();
			}
		}
	}
}
