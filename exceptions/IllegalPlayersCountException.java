package exceptions;

public class IllegalPlayersCountException extends Exception {

	private static final long serialVersionUID = 1L;

	public IllegalPlayersCountException() {
	}

	public IllegalPlayersCountException(String message) {
		super(message);
	}

}
