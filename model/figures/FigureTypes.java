package model.figures;

import model.player.Player.COLOR;

public class FigureTypes {
	public enum FIGURE_TYPES {
		BASIC, LEVITATING, FAST
	}

	public static AbstractFigure getFigure(FIGURE_TYPES pick, COLOR color) {
		switch (pick) {
			case LEVITATING:
				return new LevitatingFigure(color);
			case FAST:
				return new FastFigure(color);
			case BASIC:
			default:
				return new BasicFigure(color);
		}
	};
}
