package presentation;

import exceptions.IllegalDimensionException;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

public class Track extends Pane {

	private static final int FACTOR = 1000;
	private SequentialTransition walk;
	private InnerDoubleCoordinates startOfPath;

	private int totalTiles;

	public int getTotalTiles() {
		return totalTiles;
	}

	class InnerDoubleCoordinates {
		public double x;
		public double y;

		public InnerDoubleCoordinates(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	private DoubleProperty progress = new SimpleDoubleProperty() {
		@Override
		protected void invalidated() {
			final double currentProgress = get();
			if (currentProgress >= 0) {
				walk.jumpTo(walk.getTotalDuration().multiply(currentProgress));
			}
		}
	};

	private DoubleProperty changeProperty = new SimpleDoubleProperty();

	public Track(int dimension, double tileSize, Pane background, Node node)
			throws IllegalDimensionException {
		this(dimension, tileSize, background, node, false);
	}

	public Track(int dimension, double tileSize, Pane background, Node node, boolean leavesTrace)
			throws IllegalDimensionException {

		TileConvertor convertor = new TileConvertor(dimension, tileSize);

		model.path.Path pathWithIterator = new model.path.Path(dimension);
		totalTiles = pathWithIterator.getLength();

		Path animationPath = getAnimationPath(convertor, pathWithIterator);

		Platform.runLater(() -> background.getChildren().add(animationPath));

		node.setTranslateX(animationPath.getTranslateX());
		node.setTranslateY(animationPath.getTranslateY());
		Platform.runLater(() -> background.getChildren().add(node));
	
		FadeTransition fadeIn = getFadeIn();
		PathTransition race = new PathTransition(Duration.seconds((double) pathWithIterator.getLength() - 1),
				animationPath,
				node);
		race.setInterpolator(Interpolator.LINEAR);
		if (leavesTrace) {
			setPaintTraceHandler(background, node, race);
		}
		walk = new SequentialTransition(node, fadeIn, race);
		walk.play();
		walk.pause();
	}

	private void setPaintTraceHandler(Pane background, Node node, PathTransition race) {

		changeProperty.bind(node.translateXProperty().multiply(node.translateYProperty().multiply(FACTOR)));

		//create path transition
		changeProperty
				.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {

					// avoid starting too early
					if (node.getTranslateY() <= startOfPath.y + 5)
						return;

					Circle brush = new Circle(node.getTranslateX(), node.getTranslateY(), 3);
					brush.setFill(Color.RED);
					background.getChildren().add(brush);
				});
	}

	private Path getAnimationPath(TileConvertor convertor, model.path.Path pathWithIterator) {
		Path animationPath = new Path();
		animationPath.setStroke(Color.RED);
		var iterator = pathWithIterator.getIterator();
		int index = iterator.next().getNumber();
		startOfPath = new InnerDoubleCoordinates(convertor.getX(index), convertor.getY(index));
		animationPath.getElements().add(new MoveTo(startOfPath.x, startOfPath.y));
		while (iterator.hasNext()) {
			index = iterator.next().getNumber();
			animationPath.getElements().add(new LineTo(convertor.getX(index), convertor.getY(index)));
		}

		return animationPath;
	}

	private FadeTransition getFadeIn() {
		FadeTransition fadeIn = new FadeTransition(Duration.seconds(1));
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		fadeIn.setInterpolator(Interpolator.LINEAR);
		return fadeIn;
	}

	public double getProgress() {
		return progress.get();
	}

	public void setProgress(double value) {
		progress.set(value);
	}

	public DoubleProperty progressProperty() {
		return progress;
	}
}
