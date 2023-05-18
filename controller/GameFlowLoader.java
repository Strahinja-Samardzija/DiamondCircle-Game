package controller;

import java.io.IOException;

import controller.scene_controllers.main_scene_controller.MainSceneController;
import exceptions.FigureOfWrongColorException;
import exceptions.IllegalDimensionException;
import model.FiguresWalk;
import model.PlayersHand;
import model.figures.AbstractFigure;
import model.player.Player;
import model.player.Player.COLOR;

public class GameFlowLoader {

	private final model.path.Path path;
	private final String[] players;
	private final MainSceneController mainSceneController;

	public GameFlowLoader(int dimension, String[] players, MainSceneController mainSceneController)
			throws IllegalDimensionException {
		this.players = players;
		this.mainSceneController = mainSceneController;
		this.path = new model.path.Path(dimension);
	}

	public GameFlow load() throws FigureOfWrongColorException, IOException {

		Player[] playersWithFigures = new Player[players.length];
		COLOR[] colorPool = COLOR.values();

		for (int i = 0; i < players.length; i++) {
			playersWithFigures[i] = new Player(players[i], colorPool[i]);
		}

		PlayersHand[] hands = initializeHands(playersWithFigures);

		return new GameFlow(path, hands, mainSceneController);
	}

	private PlayersHand[] initializeHands(Player[] playersWithFigures) throws FigureOfWrongColorException {
		PlayersHand[] hands = new PlayersHand[players.length];
		for (int i = 0; i < playersWithFigures.length; i++) {
		
			FiguresWalk[] walksForThisPlayer = new FiguresWalk[Player.FIGURES_COUNT];
			for (int j = 0; j < Player.FIGURES_COUNT; j++) {
				AbstractFigure figure = playersWithFigures[i].getFigures()[j];
				if (figure.getColor() != playersWithFigures[i].getColor()) {
					throw new FigureOfWrongColorException();
				}
				walksForThisPlayer[j] = new FiguresWalk(figure, path);
			}
			hands[i] = new PlayersHand(playersWithFigures[i], walksForThisPlayer);
		}
		return hands;
	}
}
