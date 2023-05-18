package presentation;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;

public class GridResizer {

	public static final double TILE_SIZE_AT_10 = 30.0;

	private final int dimension;
	private final double scale;
	private final double tileSize;

	public GridResizer(int dimension) {
		this.dimension = dimension;
		scale = (double) 10 / dimension;
		tileSize = TILE_SIZE_AT_10 * scale;
	}

	public void resizeMatrix(GridPane grid) {
		Platform.runLater(() -> resize(grid));
	}

	private void resize(GridPane grid) {
		// int rows = grid.getRowCount();
		// int cols = grid.getColumnCount();

		// if (rows > dimension)
		// grid.getRowConstraints().remove(dimension, rows);
		// if (cols > dimension)
		// grid.getColumnConstraints().remove(dimension, cols);

		// for (int i = 0; i < dimension - rows; i++)
		// grid.getRowConstraints().add(new RowConstraints(TILE_SIZE_AT_10 * scale,
		// TILE_SIZE_AT_10 * scale, TILE_SIZE_AT_10 * scale));
		// for (int i = 0; i < dimension - cols; i++)
		// grid.getColumnConstraints().add(new ColumnConstraints(TILE_SIZE_AT_10 *
		// scale, TILE_SIZE_AT_10 * scale, TILE_SIZE_AT_10 * scale));

		grid.setPrefSize(300, 300);
		grid.setMaxSize(300, 300);

		ArrayList<RowConstraints> rows = new ArrayList<>();
		ArrayList<ColumnConstraints> cols = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			RowConstraints row = new RowConstraints();
			row.setPercentHeight((double) 100 / dimension);
			rows.add(row);

			ColumnConstraints col = new ColumnConstraints();
			col.setPercentWidth((double) 100 / dimension);
			cols.add(col);
		}
		grid.getRowConstraints().addAll(rows);
		grid.getColumnConstraints().addAll(cols);

		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				String num = "" + (i * dimension + j + 1);
				Label label = new Label(num);
				label.setMaxWidth(Double.MAX_VALUE);
				label.setAlignment(Pos.CENTER);
				grid.add(new StackPane(label), j, i);
			}
		}
	}

	public double getTileSize() {
		return tileSize;
	}
}
