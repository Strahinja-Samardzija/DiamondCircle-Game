package controller.tasks;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import controller.scene_controllers.main_scene_controller.MainSceneController;
import controller.signals.DrawCardSignal;
import controller.signals.WalkSignal;
import exceptions.PlayerOutOfFiguresException;
import exceptions.UnsupportedCardTypeException;
import logging.LoggerRegistration;
import model.FiguresWalk;
import model.PlayersHand;
import model.cards.CardDeque;
import model.cards.ICard;
import model.cards.ISpecialCard;
import model.cards.IStepsCard;
import model.dispatcher.Dispatcher;
import model.path.Path;
import model.results.ResultsSaving;

public class CardDrawingTask implements Runnable {

	{
		try {
			new LoggerRegistration<>(CardDrawingTask.class).register("CardDrawingTask.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private final SynchronousQueue<WalkSignal> walkQueue;
	private final SynchronousQueue<DrawCardSignal> drawCardQueue;

	private final model.path.Path path;
	private final Dispatcher dispatcher;
	private final MainSceneController mainSceneController;

	private final CardDeque cardDeque;
	private final PlayersHand[] hands;
	private AtomicBoolean isRunning;

	private ICard currentCard = null;
	private FiguresWalk currentWalk = null;
	private CardDescriptionUpdateThread cardDescriptionUpdateThread;

	private final class CardDescriptionUpdateThread extends Thread {
		public CardDescriptionUpdateThread() {
			super("Name:");
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (!Thread.interrupted()) {
					synchronized (CardDrawingTask.this) {
						if (currentCard != null)
							mainSceneController.cards.updateCardDescription(mainSceneController, currentCard,
									currentWalk);
					}
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
				Thread.currentThread().interrupt();
			}
		}
	}

	public CardDrawingTask(Path path, Dispatcher dispatcher, PlayersHand[] hands,
			SynchronousQueue<DrawCardSignal> drawCardQueue, SynchronousQueue<WalkSignal> walkQueue,
			MainSceneController mainSceneController, AtomicBoolean isRunning) {
		this.path = path;
		this.dispatcher = dispatcher;
		this.hands = hands;
		this.drawCardQueue = drawCardQueue;
		this.walkQueue = walkQueue;
		this.mainSceneController = mainSceneController;
		this.isRunning = isRunning;
		this.cardDeque = new CardDeque();
		this.cardDescriptionUpdateThread = new CardDescriptionUpdateThread();
	}

	@Override
	public void run() {
		cardDescriptionUpdateThread.start();
		try {
			while (!Thread.interrupted()) {
				drawCardQueue.take();

				synchronized (this) {
					currentCard = cardDeque.draw();
					if (currentCard instanceof IStepsCard)
						currentWalk = dispatcher.nextWalk();

					checkForPause();
					paintCard();

					if (currentCard instanceof ISpecialCard) {
						checkForPause();
						spawnHoles();
						signalCardSelf();
					} else if (currentCard instanceof IStepsCard) {

						int steps = ((IStepsCard) currentCard).getSteps().intValue;
						if (currentWalk != null) {
							int calculatedSteps = currentWalk.getFigure().calculateSteps(steps);
							walkQueue.put(new WalkSignal(currentWalk, calculatedSteps));
						} else {
							currentCard = null;
							currentWalk = null;
							
							ResultsSaving save = new ResultsSaving(hands);
							Thread savingThread = new Thread(() -> save.save(), "Name: SavingThread");
							savingThread.setDaemon(false);
							savingThread.start();
							return;
						}
					} else {
						throw new UnsupportedCardTypeException();
					}
				}
			}
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
			Thread.currentThread().interrupt();
		} catch (UnsupportedCardTypeException | PlayerOutOfFiguresException e) {
			Logger.getLogger(getClass().getName()).severe(e.getMessage() + "\n" + e.fillInStackTrace().toString());
		}
	}

	private void signalCardSelf() {
		new Thread(() -> {
			try {
				drawCardQueue.put(new DrawCardSignal());
			} catch (InterruptedException e) {
				Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
				Thread.currentThread().interrupt();
			}
		}, "Name: HelperAvoidCardDrawingWaitingOnItself").start();
	}

	private void spawnHoles() throws InterruptedException {
		Thread holesSpawningThread = new Thread(
				new HolesSpawningTask(path, hands, mainSceneController, isRunning),
				"HolesSpawningThread");
		holesSpawningThread.setDaemon(true);
		holesSpawningThread.start();
		holesSpawningThread.join();
	}

	private void paintCard() {
		try {
			mainSceneController.cards.paintCard(mainSceneController, currentCard);
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).severe(e.fillInStackTrace().toString());
		}
	}

	private void checkForPause() throws InterruptedException {
		synchronized (isRunning) {
			while (!isRunning.get())
				isRunning.wait();
		}
	}
}
