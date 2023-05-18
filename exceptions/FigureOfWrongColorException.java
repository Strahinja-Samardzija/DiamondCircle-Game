package exceptions;

public class FigureOfWrongColorException extends Exception {

	private static final long serialVersionUID = 1L;

	public FigureOfWrongColorException() {
		super("A player's color does not match its figure's color.");
	}

	public FigureOfWrongColorException(String message) {
		super(message);
	}
}
