package model.figures;

import model.figures.FigureTypes.FIGURE_TYPES;
import model.player.Player.COLOR;

public class LevitatingFigure extends AbstractFigure implements ILevitating {

	public LevitatingFigure(COLOR color) {
		super(color, FIGURE_TYPES.LEVITATING);
	}
	
}
