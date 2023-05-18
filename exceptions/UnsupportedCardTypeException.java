package exceptions;

public class UnsupportedCardTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedCardTypeException() {
		super("A card class must implement one of the supported card interfaces.");
	}
	
	UnsupportedCardTypeException(String message){
		super(message);
	}
}
