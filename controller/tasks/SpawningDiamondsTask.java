package controller.tasks;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import controller.scene_controllers.main_scene_controller.MainSceneController;
import logging.LoggerRegistration;
import model.path.Path;
import model.path.Tile;
import model.spawner.DiamondSpawner;

public class SpawningDiamondsTask implements Runnable {

	{
		try {
			new LoggerRegistration<>(SpawningDiamondsTask.class).register("SpawningDiamondsTask.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public static final long SPAWN_DIAMONDS_INTERVAL = 5000;

	private final model.path.Path path;
	private final AtomicBoolean shouldStartSpawningDiamonds;
	private final MainSceneController mainSceneController;

	private AtomicBoolean isRunning;

	public SpawningDiamondsTask(Path path, AtomicBoolean shouldStartSpawningDiamonds,
			MainSceneController mainSceneController, AtomicBoolean isRunning) {
		this.path = path;
		this.shouldStartSpawningDiamonds = shouldStartSpawningDiamonds;
		this.mainSceneController = mainSceneController;
		this.isRunning = isRunning;
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				synchronized (shouldStartSpawningDiamonds) {
					while (!shouldStartSpawningDiamonds.get())
						shouldStartSpawningDiamonds.wait();
				}

				checkForPause();

				final List<Tile> tilesWithDiamonds;
				synchronized (mainSceneController) {
					tilesWithDiamonds = new DiamondSpawner(path).spawn();
				}
				mainSceneController.spawns.spawnDiamonds(mainSceneController, tilesWithDiamonds);

				checkForPause();
				synchronized (mainSceneController) {
					removeOldDiamonds(tilesWithDiamonds);
				}
			}
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
			Thread.currentThread().interrupt();
		} catch (AssertionError e) {
			Logger.getLogger(getClass().getName()).severe(e.fillInStackTrace().toString());
			System.err.print(e.getMessage());
		}
	}

	private void checkForPause() throws InterruptedException {
		synchronized (isRunning) {
			while (!isRunning.get())
				isRunning.wait();
		}
	}

	private void removeOldDiamonds(List<Tile> tilesWithDiamonds) {
		mainSceneController.removers.removeDiamonds(mainSceneController);
		tilesWithDiamonds.stream().forEach(tile -> tile.setHasDiamond(false));
	}
}
