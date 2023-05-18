package controller;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import controller.scene_controllers.main_scene_controller.MainSceneController;
import controller.signals.DrawCardSignal;
import controller.signals.WalkSignal;
import controller.tasks.CardDrawingTask;
import controller.tasks.SpawningDiamondsTask;
import controller.tasks.WalkingTask;
import javafx.application.Platform;
import javafx.stage.Stage;
import logging.LoggerRegistration;
import model.PlayersHand;
import model.dispatcher.Dispatcher;

public class GameFlow {

	{
		try {
			new LoggerRegistration<>(GameFlow.class).register("GameFlow.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private final SynchronousQueue<DrawCardSignal> drawCardQueue = new SynchronousQueue<>();
	private final SynchronousQueue<WalkSignal> walkQueue = new SynchronousQueue<>();

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private AtomicBoolean shouldStartSpawningDiamonds = new AtomicBoolean(false);

	private final MainSceneController mainSceneController;
	private final Dispatcher dispatcher;

	private final CardDrawingTask cardDrawingTask;
	private final WalkingTask walkingTask;
	private final SpawningDiamondsTask spawningDiamondsTask;

	public GameFlow(model.path.Path path, PlayersHand[] hands, MainSceneController mainSceneController)
			throws IOException {
		this.mainSceneController = mainSceneController;
		this.dispatcher = new Dispatcher(hands);
		this.cardDrawingTask = new CardDrawingTask(path, dispatcher, hands, drawCardQueue, walkQueue,
				mainSceneController, isRunning);
		this.walkingTask = new WalkingTask(path, walkQueue, drawCardQueue, shouldStartSpawningDiamonds,
				mainSceneController, isRunning);
		this.spawningDiamondsTask = new SpawningDiamondsTask(path, shouldStartSpawningDiamonds, mainSceneController,
				isRunning);
		this.mainSceneController.setIsRunningReference(isRunning);
		this.mainSceneController.updateScene(path, hands);
	}

	public void start(Stage mainStage) {
		synchronized (isRunning) {
			while (!isRunning.get())
				try {
					isRunning.wait();
				} catch (InterruptedException e) {
					Logger.getLogger(GameFlow.class.getName()).log(Level.INFO, e.fillInStackTrace().toString());
					Thread.currentThread().interrupt();
				}
		}

		DrawCardSignal startSignal = new DrawCardSignal();
		new Thread(() -> {
			try {
				drawCardQueue.put(startSignal);
			} catch (InterruptedException e) {
				Logger.getLogger(GameFlow.class.getName()).log(Level.INFO, e.fillInStackTrace().toString());
				Thread.currentThread().interrupt();
			}
		}, "Name: HelperPutFirstCardSignal").start();

		// CardDrawingTask will be generating WalkSignal-s
		// It can also generate a DrawCardSignal, if the card is special
		Thread cardDrawingThread = new Thread(cardDrawingTask, "Name: CardDrawingThread");
		cardDrawingThread.start();

		// WalkingTask will be generating DrawCardSignal-s
		Thread walkingThread = new Thread(walkingTask, "Name: WalkingThread");
		walkingThread.start();

		// WalkingTask will generate signal, so that the spawning starts with the first
		// walk
		Thread spawningDiamondsThread = new Thread(spawningDiamondsTask, "Name: SpawningDiamondsThread");
		spawningDiamondsThread.start();

		try {
			cardDrawingThread.join();
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
			Thread.currentThread().interrupt();
		}
		walkingThread.interrupt();
		spawningDiamondsThread.interrupt();
		Platform.runLater(() -> Main.restart(mainStage));
	}
}
