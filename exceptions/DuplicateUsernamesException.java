package exceptions;

public class DuplicateUsernamesException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateUsernamesException() {

	}

	public DuplicateUsernamesException(String message) {
		super(message);
	}

}
