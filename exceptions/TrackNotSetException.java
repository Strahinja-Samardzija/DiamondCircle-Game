package exceptions;

public class TrackNotSetException extends RuntimeException {

    public TrackNotSetException() {
        super("Track was not set for a FigureVector.");
    }

    public TrackNotSetException(String message) {
        super(message);
    }

}
