package model.figures;

import model.player.Player.COLOR;
import model.figures.FigureTypes.FIGURE_TYPES;
import model.player.Player;

public abstract class AbstractFigure {
	private static int counter = 1;

	protected final int id;
	protected final COLOR color;
	protected final FIGURE_TYPES type;

	protected int readyToSpendDiamondsCount = 0;
	protected int stillCollectingDiamondsCount = 0;

	protected AbstractFigure(COLOR color, FIGURE_TYPES type) {
		this.id = (counter++ % Player.FIGURES_COUNT + 1) + (color.id - 1) * Player.FIGURES_COUNT;
		this.color = color;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public COLOR getColor() {
		return color;
	}

	public synchronized int getCollectedDiamondsCount() {
		return readyToSpendDiamondsCount;
	}

	public FIGURE_TYPES getType() {
		return type;
	}

	public synchronized void collectDiamond() {
		stillCollectingDiamondsCount++;
	}

	public synchronized int calculateSteps(int steps) {
		return steps + readyToSpendDiamondsCount;
	}

	public synchronized void nextRoundDiamonds(){
		readyToSpendDiamondsCount = stillCollectingDiamondsCount;
		stillCollectingDiamondsCount = 0;
	}

	@Override
	public String toString() {
		return "Figure " + id;
	}
	
}
