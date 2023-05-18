package exceptions;

public class WalkNotStartedException extends RuntimeException {

    public WalkNotStartedException() {
        super("Player has not moved this figure yet.");
    }

    public WalkNotStartedException(String message) {
        super(message);
    }

}
