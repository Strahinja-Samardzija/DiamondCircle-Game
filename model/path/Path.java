package model.path;

import java.util.ArrayList;

import exceptions.IllegalDimensionException;

public final class Path {
	public static final int MIN_DIMENSION = 7;
	public static final int MAX_DIMENSION = 10;

	private final int dimension;
	private ArrayList<Tile> tiles = new ArrayList<>();
	private final int length;

	public Path(int dimension) throws IllegalDimensionException {
		if (dimension < MIN_DIMENSION || dimension > MAX_DIMENSION) {
			throw new IllegalDimensionException();
		}

		this.dimension = dimension;
		calculatePath();
		this.length = this.tiles.size();
	}

	private void calculatePath() {
		int[][] matrix = new int[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				// starting from 1
				matrix[i][j] = i * dimension + j + 1;
			}
		}
		int limitBottom = dimension;
		int limitRight = dimension;
		int limitTop = 0;
		int limitLeft = -1;

		if (dimension % 2 == 0) {
			evenLogic(matrix, limitBottom, limitRight, limitTop, limitLeft);
		} else {
			oddLogic(matrix, limitBottom, limitRight, limitTop, limitLeft);
		}
	}

	private void oddLogic(int[][] matrix, int limitBottom, int limitRight, int limitTop, int limitLeft) {
		int row = 0;
		int col = dimension / 2;

		// stop after reaching the center tile
		while (!(row == dimension / 2 && col == dimension / 2)) {
			// heading south-east
			while (col != limitRight) {
				tiles.add(new Tile(matrix[row++][col++]));
			}
			limitRight--;
			// position on the next tile, but don't add it yet
			col = limitRight - 1;
			// heading south-west
			while (row != limitBottom) {
				tiles.add(new Tile(matrix[row++][col--]));
			}
			limitBottom--;
			// position on the next tile, but don't add it yet
			row = limitBottom - 1;
			// heading north-west
			while (col != limitLeft) {
				tiles.add(new Tile(matrix[row--][col--]));
			}
			limitLeft++;
			// position on the next tile, but don't add it yet
			col = limitLeft + 1;
			// heading north-east
			while (row != limitTop) {
				tiles.add(new Tile(matrix[row--][col++]));
			}
			limitTop++;
			// step right, but it will be added in next iteration
			row = limitTop;
		}
		// add the center tile
		tiles.add(new Tile(matrix[row][col]));
	}

	private void evenLogic(int[][] matrix, int limitBottom, int limitRight, int limitTop, int limitLeft) {
		// logic for even matrix
		int row = 0;
		int col = dimension / 2 - 1;

		// stop after reaching the end of the pattern, then handle the 4 center tiles
		// manually
		while (!(row == dimension / 2 - 1 && col == dimension / 2 - 1)) {
			// add current and step right
			tiles.add(new Tile(matrix[row][col++]));
			// heading south-east
			while (col != limitRight) {
				tiles.add(new Tile(matrix[row++][col++]));
			}
			limitRight--;
			// stepping down, but not adding yet
			col = limitRight;
			// heading south-west
			while (row != limitBottom) {
				tiles.add(new Tile(matrix[row++][col--]));
			}
			limitBottom--;
			// stepping left, but not adding yet
			row = limitBottom;
			// heading north-west
			while (col != limitLeft) {
				tiles.add(new Tile(matrix[row--][col--]));
			}
			limitLeft++;
			// stepping up, but not adding yet
			col = limitLeft;
			// heading north-east
			while (row != limitTop) {
				tiles.add(new Tile(matrix[row--][col++]));
			}
			limitTop++;
			// stepping right, but it will be added in next iteration
			row = limitTop;
		}
		// add the 4 center tiles
		tiles.add(new Tile(matrix[row][col++]));
		tiles.add(new Tile(matrix[row++][col]));
		tiles.add(new Tile(matrix[row][col--]));
		tiles.add(new Tile(matrix[row][col]));
	}

	public Iterator getIterator() {
		return new Iterator();
	}

	public int getLength() {
		return length;
	}

	public class Iterator {
		private int index = 0;

		public boolean hasNext() {
			return index < tiles.size();
		}

		public Tile next() {
			if (hasNext())
				return tiles.get(index++);
			else
				return null;
		}
	}

	public int getDimension() {
		return dimension;
	}
}
