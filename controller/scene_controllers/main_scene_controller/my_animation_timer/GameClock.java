package controller.scene_controllers.main_scene_controller.my_animation_timer;

import controller.scene_controllers.main_scene_controller.MainSceneController;
import javafx.application.Platform;

public final class GameClock extends MyAnimationTimer {

    private static final long TO_NANOS = 1_000_000_000;

    public GameClock(MainSceneController mainSceneController, long duration) {
        super(mainSceneController, duration);
    }

    @Override
    protected void update(long now) {
        Platform.runLater(() -> mainSceneController.getTimeElapsedLabel()
                .setText(String.format("%02d:%02d", minutes(now), seconds(now))));
    }

    private long seconds(long now) {
        return ((now - startStamp + elapsed) / TO_NANOS) % (60);
    }

    private long minutes(long now) {
        return (now - startStamp + elapsed) / (60 * TO_NANOS);
    }

}
