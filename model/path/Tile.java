package model.path;

public class Tile {
	private final int number;
	private volatile boolean hasHole;
	private volatile boolean hasDiamond;
	private volatile boolean isOccupied;
	
	public Tile(int number) {
		this.number = number;
	}
	
	public String toString() {
		return "" + number;
	}
	
	public boolean isHasHole() {
		return hasHole;
	}

	public void setHasHole(boolean hasHole) {
		this.hasHole = hasHole;
	}

	public boolean isHasDiamond() {
		return hasDiamond;
	}

	public void setHasDiamond(boolean hasDiamond) {
		this.hasDiamond = hasDiamond;
	}

	public boolean isOccupied() {
		return isOccupied;
	}

	public void setIsOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	public int getNumber() {
		return number;
	}
}
