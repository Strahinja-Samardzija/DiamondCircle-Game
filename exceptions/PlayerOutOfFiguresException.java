package exceptions;

public class PlayerOutOfFiguresException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PlayerOutOfFiguresException() {
		super("A player is out of figures.");
	}

	public PlayerOutOfFiguresException(String message) {
		super(message);
	}

}
