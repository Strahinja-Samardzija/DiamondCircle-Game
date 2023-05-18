package controller.scene_controllers.main_scene_controller.my_animation_timer;

import java.util.logging.Logger;
import controller.scene_controllers.main_scene_controller.MainSceneController;
import javafx.animation.AnimationTimer;

public abstract class MyAnimationTimer extends AnimationTimer {

	protected final MainSceneController mainSceneController;
	protected long startStamp;
    protected final long duration;
	protected long elapsed = 0;
    private boolean isFinished = false;

	protected MyAnimationTimer(MainSceneController mainSceneController, long duration) {
		this.mainSceneController = mainSceneController;
        this.duration = duration;
	}

    public boolean isFinished(){
        return isFinished;
    }

	@Override
	public void handle(long now) {
		if (elapsed + now - startStamp >= duration) {
			stop();
		}

		if (this.mainSceneController.getIsRunning().get()) {
			update(now);
		} else {
			addToElapsed();
			super.stop();
			waitForPause();
		}
	}
	
	protected abstract void update(long now);

    protected final void addToElapsed() {
		elapsed += System.nanoTime() - startStamp;
	}
	
	@Override
	public void start() {
		startStamp = System.nanoTime();
		super.start();
	}

	@Override
	public void stop() {
		synchronized (this) {
			super.stop();
            isFinished = true;
			notifyAll();
		}
	}

	protected final void waitForPause() {
		Runnable task = () -> {
			synchronized (this.mainSceneController.getIsRunning()) {
				while (!this.mainSceneController.getIsRunning().get()) {
					try {
						this.mainSceneController.getIsRunning().wait();
					} catch (InterruptedException e) {
						Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
						Thread.currentThread().interrupt();
					}
				}
			}
			start();
		};
		waitForPause(task);
	}

	private void waitForPause(Runnable task) {
		Thread deamon = new Thread(task);
		deamon.setDaemon(true);
		deamon.start();
	}

}
