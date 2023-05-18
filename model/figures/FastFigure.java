package model.figures;

import model.figures.FigureTypes.FIGURE_TYPES;
import model.player.Player.COLOR;

public class FastFigure extends AbstractFigure implements IFast {

	public FastFigure(COLOR color) {
		super(color, FIGURE_TYPES.FAST);
	}

	@Override
	public synchronized int calculateSteps(int steps) {
		return 2 * steps + readyToSpendDiamondsCount;
	}
	
}
