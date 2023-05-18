package controller.scene_controllers;

import java.io.IOException;
import java.util.logging.Logger;

import application.Main;
import controller.GameFlow;
import controller.GameFlowLoader;
import controller.scene_controllers.main_scene_controller.MainSceneController;
import exceptions.DuplicateUsernamesException;
import exceptions.FigureOfWrongColorException;
import exceptions.IllegalDimensionException;
import exceptions.IllegalPlayersCountException;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import logging.LoggerRegistration;
import model.game_validation.GameValidation;
import presentation.UsernameInputForm;

public class StartSceneController {

	{
		try {
			new LoggerRegistration<>(StartSceneController.class).register("StartScene.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private AnchorPane root;
	@FXML
	private Button newGameButton;
	@FXML
	private ChoiceBox<String> sizeChoice;
	@FXML
	private ChoiceBox<Integer> playersCountChoice;
	@FXML
	private VBox usernamesVBox;

	private Integer[] playersCountOptions = { 2, 3, 4 };
	private String[] sizeOptions = { "7x7", "8x8", "9x9", "10x10" };

	@FXML
	private void initialize() {
		sizeChoice.getItems().addAll(sizeOptions);

		playersCountChoice.getItems().addAll(playersCountOptions);
		playersCountChoice.setOnAction(event -> {
			int selection = playersCountChoice.getSelectionModel().getSelectedItem();
			int size = usernamesVBox.getChildren().size();
			if (size > selection)
				usernamesVBox.getChildren().remove(selection, size);

			for (int i = 0; i < selection - size; i++)
				usernamesVBox.getChildren().add(new UsernameInputForm(size + i + 1));

		});
	}

	public void loadGame(ActionEvent e) {
		if (playersCountChoice.getSelectionModel().isEmpty() || sizeChoice.getSelectionModel().isEmpty()) {
			prompt("You must select the size and the number of players.");
			return;
		}

		Stage mainStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		String[] usernames = new String[usernamesVBox.getChildren().size()];

		int i = 0;
		for (Node node : usernamesVBox.getChildren()) {
			if (node instanceof UsernameInputForm) {
				String username = ((UsernameInputForm) node).getUsername();
				if (username != null) {
					username = username.trim();
					if (!"".equals(username))
						usernames[i++] = username;
				}
			}
		}
		if (i != usernamesVBox.getChildren().size()) {
			prompt("A player's username is missing.");
			return;
		}
		String selectedSize = sizeChoice.getSelectionModel().getSelectedItem();
		try {
			int dimension = Integer.parseInt(selectedSize.substring(0, selectedSize.indexOf('x')));
			loadGame(dimension, usernames, mainStage);
		} catch (NumberFormatException numberException) {
			Logger.getLogger(getClass().getName()).warning("Parsing dimension from choice failed.");
		} catch (IOException e1) {
			Logger.getLogger(getClass().getName()).severe(e1.fillInStackTrace().toString());
		}
	}

	public void loadGame(int dimension, String[] players, Stage mainStage) throws IOException {
		try {
			new GameValidation().validate(dimension, players);
		} catch (IllegalDimensionException | IllegalPlayersCountException | DuplicateUsernamesException e) {
			Logger.getLogger(getClass().getName()).severe(e.getMessage() + "\n" + e.fillInStackTrace().toString());
		}

		var loader = new FXMLLoader(getClass().getResource("/presentation/MainScene.fxml"));
		try {
			Parent rootForMainScene = loader.load();
			Scene mainScene = new Scene(rootForMainScene);
			MainSceneController mainSceneController = (MainSceneController) loader.getController();
			mainStage.setScene(mainScene);
			mainStage.show();
			GameFlow gameFlow = new GameFlowLoader(dimension, players, mainSceneController).load();
			Thread daemon = new Thread(() -> gameFlow.start(mainStage), "Name: GameFlowThread");
			daemon.setDaemon(true);
			daemon.start();
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).severe(e.fillInStackTrace().toString());
		} catch (FigureOfWrongColorException | IllegalDimensionException e) {
			Logger.getLogger(getClass().getName()).severe(e.getMessage());
		}
	}

	private void prompt(String message) {
		Label errorMessage = new Label(message);
		errorMessage.setMinWidth(root.getWidth());
		errorMessage.setAlignment(Pos.CENTER);
		AnchorPane.setTopAnchor(errorMessage, 20.0);
		root.getChildren().add(errorMessage);
		FadeTransition fade = new FadeTransition(Duration.millis(2000), errorMessage);
		fade.setFromValue(1);
		fade.setToValue(0);
		fade.play();
	}

}
