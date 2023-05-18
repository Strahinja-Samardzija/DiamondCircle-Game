package exceptions;

public class TimerWasNotStartedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TimerWasNotStartedException() {
		super("model.FiguresWalk.startTimer() was never called for this object.");
	}

	public TimerWasNotStartedException(String message) {
		super(message);
	}

}
