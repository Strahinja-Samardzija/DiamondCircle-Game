package controller.scene_controllers.main_scene_controller;

import java.util.List;
import java.util.stream.Collectors;

import controller.scene_controllers.main_scene_controller.my_animation_timer.Pause;
import model.FiguresWalk;
import presentation.FigureVector;

public class Removers {

	public void removeWalks(MainSceneController mainSceneController, List<FiguresWalk> walks) {
		List<FigureVector> vectors = walks.stream().map(FiguresWalk::getId)
				.map(mainSceneController.figures::get).collect(Collectors.toList());

		vectors.stream().forEach(FigureVector::die);
		Runnable task = () -> mainSceneController.gridCarrier.getChildren().removeAll(vectors);
		mainSceneController.waitToUpdateScene(task);
	}

	public void removeDiamonds(MainSceneController mainSceneController) {
		Runnable task = () -> mainSceneController.gridCarrier.getChildren()
				.removeAll(mainSceneController.diamonds.values());
		mainSceneController.waitToUpdateScene(task);

		mainSceneController.diamonds.clear();
	}

	public void removeHoles(MainSceneController mainSceneController) {
		Pause.pause(mainSceneController, 500);

		Runnable task = () -> mainSceneController.gridCarrier.getChildren().removeAll(mainSceneController.holes);
		mainSceneController.waitToUpdateScene(task);

		mainSceneController.holes.clear();
	}

}
