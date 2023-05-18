package controller.scene_controllers;

import exceptions.IllegalDimensionException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import presentation.FigureVector;
import presentation.GridResizer;
import presentation.Track;

public class FiguresMatrixSceneController {

	@FXML
	private Pane pane;
	@FXML
	GridPane grid;

	private double tileSize;
	private int dimension;
	private double oldProgress = 0;
	private Track track;
	private Circle brush;

	@FXML
	private void initialize() {
		brush = new Circle(grid.getTranslateX(), grid.getChildren().get(0).getTranslateY(), 6);
	}

	public void setDimension(int dimension) throws IllegalDimensionException {
		this.dimension = dimension;
		GridResizer resizer = new GridResizer(dimension);

		resizer.resizeMatrix(grid);
		tileSize = resizer.getTileSize();

		setTrack(brush);
	}

	public void play(FigureVector vector) {
		if (vector.isDead()) {
			brush.setFill(Color.BLACK);
		} else {
			brush.setFill(Color.RED);
		}

		double progress = getProgress(vector);

		Duration duration = getDuration(progress);
		Timeline walk = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(track.progressProperty(),
				oldProgress)),
				new KeyFrame(duration, new KeyValue(track.progressProperty(),
						progress)));
		walk.play();
		oldProgress = progress;
	}

	private Duration getDuration(double progress) {
		return Duration.seconds(10 * progress).subtract(Duration.seconds(10 * oldProgress));
	}

	private void setTrack(Circle brush) throws IllegalDimensionException {
		track = new Track(dimension, tileSize, pane, brush, true);
	}

	private double getProgress(FigureVector vector) {
		return (double) vector.getTilesFromStart() / track.getTotalTiles();
	}

	public long getSleepDuration(FigureVector vector) {
		return (long) getDuration(getProgress(vector)).toMillis();
	}

}
