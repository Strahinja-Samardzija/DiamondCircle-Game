package controller.tasks;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import controller.scene_controllers.main_scene_controller.MainSceneController;
import model.FiguresWalk;
import model.PlayersHand;
import model.PlayersHand.HandsWalksIterator;
import model.path.Path;
import model.path.Tile;
import model.spawner.HoleSpawner;

public class HolesSpawningTask implements Runnable {

	private final model.path.Path path;
	private final PlayersHand[] hands;
	private final MainSceneController mainSceneController;
	private AtomicBoolean isRunning;

	public HolesSpawningTask(Path path, PlayersHand[] hands, MainSceneController mainSceneController,
			AtomicBoolean isRunning) {
		this.path = path;
		this.hands = hands;
		this.mainSceneController = mainSceneController;
		this.isRunning = isRunning;
	}

	@Override
	public void run() {
		try {
			List<Tile> tilesWithHoles = new HoleSpawner(path).spawn();
			checkForPause();
			mainSceneController.spawns.spawnHoles(mainSceneController, tilesWithHoles);

			checkForPause();
			fallIfOnHole();

			checkForPause();
			removeHoles(tilesWithHoles);
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
			Thread.currentThread().interrupt();
		}
		catch (AssertionError e){
			Logger.getLogger(getClass().getName()).severe(e.fillInStackTrace().toString());
			System.err.println(e.getMessage());
		}
	}

	private void fallIfOnHole() {
		List<FiguresWalk> walksToFall = Stream.of(hands).map(PlayersHand::getIterator)
				.map(HandsWalksIterator::getCurrentWalk).filter(this::canFall).filter(FiguresWalk::fallIfOnHole)
				.collect(Collectors.toList());
		mainSceneController.removers.removeWalks(mainSceneController, walksToFall);
	}

	private boolean canFall(FiguresWalk walk) {
		return FiguresWalk.State.WALKING == walk.getState();
	}

	private void removeHoles(List<Tile> tilesWithHoles) {
		tilesWithHoles.forEach(tile -> tile.setHasHole(false));
		mainSceneController.removers.removeHoles(mainSceneController);
	}

	private void checkForPause() throws InterruptedException {
		synchronized (isRunning) {
			while (!isRunning.get())
				isRunning.wait();
		}
	}
}
