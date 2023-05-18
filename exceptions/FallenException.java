package exceptions;

public class FallenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FallenException() {
		super("This figure has fallen in a hole.");
	}

	public FallenException(String message) {
		super(message);
	}

}
