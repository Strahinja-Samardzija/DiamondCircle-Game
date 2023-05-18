package controller.scene_controllers.main_scene_controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.scene_controllers.FiguresMatrixTask;
import exceptions.IllegalDimensionException;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import logging.LoggerRegistration;
import model.FiguresWalk;
import model.PlayersHand;
import model.figures.AbstractFigure;
import model.game_validation.GameValidation;
import model.results.ResultsListing;
import presentation.FigureVector;
import presentation.GridResizer;
import presentation.PlayerLabel;
import presentation.TileConvertor;
import presentation.Track;

class Layouts {

	{
		try {
			new LoggerRegistration<>(Layouts.class).register("MainScene.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	void setDimension(MainSceneController mainSceneController, int dimension) {
		mainSceneController.dimension = dimension;
		GridResizer gridResizer = new GridResizer(dimension);
		gridResizer.resizeMatrix(mainSceneController.matrixGrid);
		mainSceneController.tileSize = gridResizer.getTileSize();
		mainSceneController.convertor = new TileConvertor(dimension, mainSceneController.tileSize);
	}

	void loadPlayersWithFigures(MainSceneController mainSceneController, PlayersHand[] hands) throws IOException {
		final double margin = (50.0 - 10 * hands.length) * GameValidation.MAX_PLAYERS / hands.length;

		mainSceneController.playerLabelsHBox.getChildren()
				.addAll(mainSceneController.layouts.createPlayerLabels(hands));
		mainSceneController.playerLabelsHBox.getChildren().stream()
				.forEach(label -> HBox.setMargin(label, new Insets(0, margin, 0, margin)));

		ArrayList<Label> figuresLabels = mainSceneController.layouts.getFiguresLabels(mainSceneController, hands);

		mainSceneController.figuresListView.getItems().addAll(figuresLabels);
		mainSceneController.figuresListView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldVal, newVal) -> {
					try {
						int figuresId = Integer.parseInt(observable.getValue().getText().split(" ")[1]);
						FigureVector vector = mainSceneController.figures.get(figuresId);
						if (vector == null) {
							throw new IllegalArgumentException(
									"Label's id was not linked to an existing FiguresVector.");
						}

						Thread deamon = new Thread(new FiguresMatrixTask(mainSceneController.dimension, vector),
								"Name: FiguresMatrixThread");
						deamon.setDaemon(true);
						deamon.start();

					} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
						throw new IllegalArgumentException("Label for a figure had improperly formatted text.");
					}
				});
	}

	ArrayList<Label> getFiguresLabels(MainSceneController mainSceneController, PlayersHand[] hands) throws IOException {
		ArrayList<Label> figuresLabels = new ArrayList<>();
		for (PlayersHand playersHand : hands) {
			for (FiguresWalk walk : playersHand.getWalks()) {
				AbstractFigure figure = walk.getFigure();
				FigureVector figureVector = new FigureVector(mainSceneController.images.getImage(figure),
						figure.getId());
				try {
					figureVector.setTrack(new Track(mainSceneController.dimension, mainSceneController.tileSize,
							mainSceneController.gridCarrier, figureVector));
				} catch (IllegalDimensionException e) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.fillInStackTrace().toString());
				}
				mainSceneController.figures.put(figureVector.getFiguresId(), figureVector);

				Label figureLabel = new Label("Figure " + figure.getId());
				figureLabel.setTextFill(Paint.valueOf(figure.getColor().toString()));
				figuresLabels.add(figureLabel);
			}
		}
		return figuresLabels;
	}

	List<PlayerLabel> createPlayerLabels(PlayersHand[] hands) {
		ArrayList<PlayerLabel> list = new ArrayList<>();
		for (PlayersHand hand : hands) {
			list.add(new PlayerLabel(hand.getPlayer().getUsername(), hand.getPlayer().getColor()));
		}
		return list;
	}

	public void updateRecordedGamesCount(MainSceneController mainSceneController) {
		mainSceneController.getGamesCountLabel()
				.setText(String.format("Trenutni broj odigranih igara: %d", new ResultsListing().recordedGamesCount()));
	}

}
