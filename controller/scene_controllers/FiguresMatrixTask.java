package controller.scene_controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import exceptions.IllegalDimensionException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logging.LoggerRegistration;
import presentation.FigureVector;

public final class FiguresMatrixTask implements Runnable {

	{
		try {
			new LoggerRegistration<>(FiguresMatrixSceneController.class)
					.register("FiguresMatrix.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private final int dimension;
	private final FigureVector vector;

	public FiguresMatrixTask(int dimension, FigureVector vector) {
		this.dimension = dimension;
		this.vector = vector;
	}

	@Override
	public void run() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/presentation/FiguresMatrixScene.fxml"));
			Parent root = loader.load();
			FiguresMatrixSceneController controller = (FiguresMatrixSceneController) loader.getController();
			controller.setDimension(dimension);
			Scene scene = new Scene(root);

			Platform.runLater(() -> {
				Stage matrixForFigure = new Stage();
				matrixForFigure.setScene(scene);
				matrixForFigure.show();
			});

			for (int i = 0; i < 3; i++) {
				Platform.runLater(() -> controller.play(vector));
				Thread.sleep(controller.getSleepDuration(vector));
			}
			addHandler(controller);
		} catch (IOException | IllegalDimensionException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getName()).log(Level.INFO, e.fillInStackTrace().toString());
			Thread.currentThread().interrupt();
		}
	}

	private void addHandler(FiguresMatrixSceneController controller) {
		vector.tilesFromStartProperty()
				.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> controller.play(vector));
	}

}