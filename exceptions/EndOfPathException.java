package exceptions;

public class EndOfPathException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EndOfPathException() {
	}

	public EndOfPathException(String message) {
		super(message);
	}

}
