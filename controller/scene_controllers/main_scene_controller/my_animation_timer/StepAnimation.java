package controller.scene_controllers.main_scene_controller.my_animation_timer;

import controller.scene_controllers.main_scene_controller.MainSceneController;
import model.FiguresWalk;
import model.path.Tile;
import presentation.FigureVector;

public final class StepAnimation extends MyAnimationTimer {

	private final FigureVector vector;
	private final FiguresWalk walk;
	private final Tile previousTile;
	private final Tile nextTile;

	public StepAnimation(MainSceneController mainSceneController, FigureVector vector, FiguresWalk walk,
			Tile previousTile, Tile nextTile) {
		super(mainSceneController, MainSceneController.STEP_INTERVAL * 1_000_000);
		this.vector = vector;
		this.walk = walk;
		this.previousTile = previousTile;
		this.nextTile = nextTile;
	}

	@Override
	protected void update(long now) {
		walk.startTimer(now);
		if ((double) (elapsed + now - startStamp) / duration < 0.8) {
			synchronized (mainSceneController) {
				if (previousTile != null && previousTile.isHasDiamond()) {
					walk.collectDiamond();
					previousTile.setHasDiamond(false);
					mainSceneController.walks.collectDiamond(mainSceneController, previousTile);
				}
			}
		} else {
			synchronized (mainSceneController) {
				if (nextTile.isHasDiamond()) {
					walk.collectDiamond();
					nextTile.setHasDiamond(false);
					mainSceneController.walks.collectDiamond(mainSceneController, nextTile);
				}
			}
		}

		vector.getTrack().setProgress(
				(vector.getTilesFromStart() - 1
						+ (elapsed + now - startStamp) / (MainSceneController.STEP_INTERVAL * 1e6))
						/ this.mainSceneController.getPath().getLength());
	}

	@Override
	public void handle(long now) {
		if (elapsed + now - startStamp >= duration) {
			stop();
		}

		if (this.mainSceneController.getIsRunning().get()) {
			update(now);
		} else {
			walk.stopTimer(now);
			addToElapsed();
			super.stop();
			waitForPause();
		}
	}
}