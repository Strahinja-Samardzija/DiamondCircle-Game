package controller.scene_controllers.main_scene_controller.my_animation_timer;

import java.io.IOException;
import java.util.logging.Logger;

import controller.scene_controllers.main_scene_controller.MainSceneController;
import javafx.application.Platform;
import logging.LoggerRegistration;

public class Pause extends MyAnimationTimer {

    {
		try {
			new LoggerRegistration<>(Pause.class).register("Timer.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

    public Pause(MainSceneController mainSceneController, long duration) {
        super(mainSceneController, duration * 1_000_000);
    }

    @Override
    protected void update(long now) {
        // this method is empty because pause only uses MyAnimationTimer's time measuring and does 
        // not update anything the scene's contents
    }

    public static void pause(MainSceneController mainSceneController, long millis) {
        Pause pause = new Pause(mainSceneController, millis);
        Platform.runLater(pause::start);
        try {
            synchronized (pause) {
                while (!pause.isFinished())
                    pause.wait();
            }
        } catch (InterruptedException e) {
            Logger.getLogger(Pause.class.getName()).info(e.fillInStackTrace().toString());
            Thread.currentThread().interrupt();
        }
    }
}
