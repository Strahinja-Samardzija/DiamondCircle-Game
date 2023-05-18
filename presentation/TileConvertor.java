package presentation;

public class TileConvertor {

	private final int dimension;
	private final double offsetX;
	private final double offsetY;
	private final double tileSize;

	public TileConvertor(int dimension, double tileSize) {
		this.dimension = dimension;
		this.tileSize = tileSize;
		offsetX = offsetY = tileSize / 2;
	}

	public double getX(int tile) {
		return ((tile % dimension - 1) >= 0 ? (tile % dimension - 1) % dimension
				: dimension - -(tile % dimension - 1) % dimension) * tileSize + offsetX;
	}

	public double getY(int tile) {
		// integer division
		return (tile % dimension == 0 ? tile / dimension - 1 : tile / dimension) * tileSize + offsetY;
	}
}
