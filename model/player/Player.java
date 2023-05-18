package model.player;

import java.util.Random;

import model.figures.AbstractFigure;
import model.figures.FigureTypes;

public class Player {
	public static final int FIGURES_COUNT = 4;

	public enum COLOR {
		RED(1), PURPLE(2), GREEN(3), BLUE(4);
		
		public int id;
		
		COLOR(int id){
			this.id = id;
		}
	}
	
	private final COLOR color;

	private final int id;
	private final String username;
	private AbstractFigure[] figures = new AbstractFigure[FIGURES_COUNT];

	public Player(String username, COLOR color) {
		this.id = color.id;
		this.username = username;
		this.color = color;

		Random rand = new Random();
		for (int i = 0; i < FIGURES_COUNT; i++) {
			FigureTypes.FIGURE_TYPES pick = FigureTypes.FIGURE_TYPES.values()[rand
					.nextInt(FigureTypes.FIGURE_TYPES.values().length)];
			figures[i] = FigureTypes.getFigure(pick, this.color);
		}
	}

	public String getUsername() {
		return username;
	}
	
	public COLOR getColor() {
		return color;
	}

	public AbstractFigure[] getFigures() {  
		return figures;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Player " + username;
	}

}
