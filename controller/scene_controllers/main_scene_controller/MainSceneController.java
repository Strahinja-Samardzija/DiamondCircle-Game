package controller.scene_controllers.main_scene_controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import controller.scene_controllers.main_scene_controller.my_animation_timer.GameClock;
import controller.scene_controllers.main_scene_controller.my_animation_timer.MyAnimationTimer;
import controller.scene_controllers.main_scene_controller.my_animation_timer.StepAnimation;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logging.LoggerRegistration;
import model.FiguresWalk;
import model.PlayersHand;
import model.path.Tile;
import presentation.Diamond;
import presentation.FigureVector;
import presentation.GridResizer;
import presentation.Hole;
import presentation.TileConvertor;

public class MainSceneController implements Initializable {

	{
		try {
			new LoggerRegistration<>(MainSceneController.class).register("MainScene.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public static final long STEP_INTERVAL = 1000;

	static final int DIMENSION_UNINITIALIZED = -1;

	AtomicBoolean isRunning;
	public AtomicBoolean getIsRunning() { return isRunning; }

	TileConvertor convertor;
	int dimension = DIMENSION_UNINITIALIZED;
	double tileSize = GridResizer.TILE_SIZE_AT_10;

	model.path.Path path;
	public model.path.Path getPath() { return path; }

	private GameClock clock = new GameClock(this, Long.MAX_VALUE);
	private boolean startedClock = false;

	public Spawns spawns = new Spawns();
	public Removers removers = new Removers();
	public Layouts layouts = new Layouts();
	public Cards cards = new Cards();
	public Images images = new Images();
	public Prompts prompts = new Prompts();
	public Walks walks = new Walks();

	@FXML BorderPane borderPane;
	@FXML Pane gridCarrier;
	@FXML GridPane matrixGrid;
	@FXML Button playPauseButton;
	@FXML Label gamesCountLabel;
	@FXML Label timeElapsedLabel;
	@FXML HBox playerLabelsHBox;
	@FXML ScrollPane figuresScroll;
	@FXML ListView<Label> figuresListView;
	@FXML ImageView cardImageView;
	@FXML Label cardDescriptionContentLabel;

	public BorderPane getBorderPane() { return borderPane; }
	public Pane getGridCarrier() { return gridCarrier; }
	public GridPane getMatrixGrid() { return matrixGrid; }
	public Button getPlayPauseButton() { return playPauseButton; }
	public Label getGamesCountLabel() { return gamesCountLabel; }
	public Label getTimeElapsedLabel() { return timeElapsedLabel; }
	public HBox getPlayerLabelsHBox() { return playerLabelsHBox; }
	public ScrollPane getFiguresScroll() { return figuresScroll; }
	public ListView<Label> getFiguresListView() { return figuresListView; }
	public ImageView getCardImageView() { return cardImageView; }
	public Label getCardDescriptionContentLabel() { return cardDescriptionContentLabel; }

	HashMap<Integer, FigureVector> figures = new HashMap<>(16, 1.0f);
	HashMap<Integer, Diamond> diamonds = new HashMap<>(model.path.Path.MAX_DIMENSION, 1.0f);
	ArrayList<Hole> holes = new ArrayList<>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		borderPane.getStylesheets().add(getClass().getResource("/presentation/MainScene.css").toExternalForm());
	}

	public void setIsRunningReference(AtomicBoolean isRunning) {
		this.isRunning = isRunning;
	}

	public void updateScene(model.path.Path path, PlayersHand[] hands) throws IOException {
		this.path = path;
		layouts.setDimension(this, path.getDimension());
		layouts.loadPlayersWithFigures(this, hands);
		layouts.updateRecordedGamesCount(this);
		resetClock();
	}

	MyAnimationTimer getStepTransition(FigureVector vector, FiguresWalk walk, Tile previousTile, Tile nextTile) {
		return new StepAnimation(this, vector, walk, previousTile, nextTile);
	}

	void waitToUpdateScene(Runnable task) {
		FutureTask<Void> future = new FutureTask<>(task, null);
		Platform.runLater(future);
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			Logger.getLogger(getClass().getName()).info(e.fillInStackTrace().toString());
			Thread.currentThread().interrupt();
		}
	}

	public void playPause(ActionEvent e) {
		synchronized (isRunning) {
			if (isRunning != null) {
				if (!isRunning.get()) {
					startClock();
					playPauseButton.setStyle("-fx-background-color: \"D7BDE2\"; -fx-text-fill: \"black\"");
					isRunning.set(true);
				} else {
					playPauseButton.setStyle("-fx-background-color: \"800E74\"; -fx-text-fill: \"EFF30C\"");
					isRunning.set(false);
				}
				isRunning.notifyAll();
			}
		}
	}

	public void showResults(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(
					getClass().getResource("/presentation/ResultsScene.fxml"));
			Scene scene = new Scene(root);

			Platform.runLater(() -> {
				Stage resultsSceneStage = new Stage();
				resultsSceneStage.setScene(scene);
				resultsSceneStage.show();
			});
		} catch (IOException exception) {
			Logger.getLogger(getClass().getName()).severe(exception.fillInStackTrace().toString());
		}
	}

	private void startClock() {
		if (!startedClock)
			clock.start();
	}

	private void resetClock() {
		clock.stop();
		startedClock = false;
	}
}
