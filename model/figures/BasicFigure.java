package model.figures;

import model.figures.FigureTypes.FIGURE_TYPES;
import model.player.Player.COLOR;

public class BasicFigure extends AbstractFigure {

	public BasicFigure(COLOR color) {
		super(color, FIGURE_TYPES.BASIC);
	}

}
