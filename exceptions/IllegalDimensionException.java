package exceptions;

public class IllegalDimensionException extends Exception {
	private static final long serialVersionUID = 1L;

	public IllegalDimensionException() {
		super("This dimension for matrix is not supported.");
	}

	public IllegalDimensionException(String message) {
		super(message);
	}
}
