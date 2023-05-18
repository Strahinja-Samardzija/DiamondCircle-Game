package controller.tasks;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import controller.scene_controllers.main_scene_controller.MainSceneController;
import controller.signals.DrawCardSignal;
import controller.signals.WalkSignal;
import exceptions.EndOfPathException;
import exceptions.FallenException;
import logging.LoggerRegistration;
import model.FiguresWalk;
import model.path.Path;
import model.path.Tile;

public class WalkingTask implements Runnable {

	{
		try {
			new LoggerRegistration<>(WalkingTask.class).register("WalkingTask.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private final model.path.Path path;
	private final SynchronousQueue<WalkSignal> walkQueue;
	private final SynchronousQueue<DrawCardSignal> drawCardQueue;
	private final MainSceneController mainSceneController;
	private AtomicBoolean shouldStartSpawningDiamonds;
	private AtomicBoolean isRunning;

	public WalkingTask(Path path, SynchronousQueue<WalkSignal> walkQueue,
			SynchronousQueue<DrawCardSignal> drawCardQueue, AtomicBoolean shouldStartSpawningDiamonds,
			MainSceneController mainSceneController, AtomicBoolean isRunning) {
		this.path = path;
		this.walkQueue = walkQueue;
		this.drawCardQueue = drawCardQueue;
		this.shouldStartSpawningDiamonds = shouldStartSpawningDiamonds;
		this.mainSceneController = mainSceneController;
		this.isRunning = isRunning;
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				WalkSignal signalInfo = walkQueue.take();
				FiguresWalk walk = signalInfo.getWalk();

				doTheWalking(signalInfo, walk);

				drawCardQueue.put(new DrawCardSignal());
			}
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
			Thread.currentThread().interrupt();
		}
	}

	private void doTheWalking(WalkSignal signalInfo, FiguresWalk walk)
			throws InterruptedException, IllegalArgumentException {
		// start spawning diamonds
		if (!shouldStartSpawningDiamonds.get()) {
			synchronized (shouldStartSpawningDiamonds) {
				shouldStartSpawningDiamonds.set(true);
				shouldStartSpawningDiamonds.notifyAll();
			}
		}

		if (FiguresWalk.State.NOT_STARTED == walk.getState() || FiguresWalk.State.WALKING == walk.getState()) {
			if (FiguresWalk.State.WALKING == walk.getState()) {
				walk.getCurrentTile().setIsOccupied(false);
			}

			Tile lastTile = walkCalculatedSteps(walk, signalInfo.getCalculatedSteps());

			if (lastTile.isOccupied()) {
				lastTile = moveOnIfOccupied(walk);
			}

			if (FiguresWalk.State.OBSERVING_FINISH_LINE == walk.getState()) {
				// removes the figure from the scene AND
				// sets the walk's state to State.FINISHED and stops its timer
				endWalkSuccesfully(walk);
			} else {
				finishRound(walk, lastTile);
			}
		} else {
			throw new IllegalArgumentException("Invalid state of the signalled walk.");
		}
	}

	private void finishRound(FiguresWalk walk, Tile lastTile) {
		walk.getFigure().nextRoundDiamonds();
		lastTile.setIsOccupied(true);
	}

	private Tile walkCalculatedSteps(FiguresWalk walk, int steps) throws InterruptedException {
		Tile previousTile = null;
		if (FiguresWalk.State.WALKING == walk.getState())
			previousTile = walk.getCurrentTile();

		Tile nextTile = walk.nextTile();
		int counter = 1;
		while (walk.hasNextTile() || FiguresWalk.State.OBSERVING_FINISH_LINE == walk.getState()) {
			checkForPause();
			processStep(walk, previousTile, nextTile);
			if (walk.hasNextTile() && counter++ < steps) {
				previousTile = walk.getCurrentTile();
				nextTile = walk.nextTile();
			} else
				break;
		}
		return nextTile;
	}

	private Tile moveOnIfOccupied(FiguresWalk walk) throws InterruptedException {
		Tile previousTile = null;
		if (FiguresWalk.State.WALKING == walk.getState())
			previousTile = walk.getCurrentTile();

		Tile nextTile = walk.nextTile();
		while (walk.hasNextTile() || FiguresWalk.State.OBSERVING_FINISH_LINE == walk.getState()) {
			checkForPause();
			mainSceneController.walks.resolveConflict(mainSceneController);
			if (nextTile.isOccupied()) {
				processStep(walk, previousTile, nextTile);
			} else {
				processStep(walk, previousTile, nextTile);
				break;
			}

			if (walk.hasNextTile()) {
				previousTile = walk.getCurrentTile();
				nextTile = walk.nextTile();
			} else
				break;
		}
		return nextTile;
	}

	private void processStep(FiguresWalk walk, Tile previousTile, Tile nextTile)
			throws FallenException, EndOfPathException {
		mainSceneController.walks.stepAndSeekDiamonds(mainSceneController, walk, previousTile, nextTile);
	}

	private void endWalkSuccesfully(FiguresWalk walk) {
		mainSceneController.walks.endWalkSuccesssfuly(mainSceneController, walk);
		walk.endWalkSuccesfully();
	}

	private void checkForPause() throws InterruptedException {
		synchronized (isRunning) {
			while (!isRunning.get())
				isRunning.wait();
		}
	}

}
