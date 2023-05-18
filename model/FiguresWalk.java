package model;

import exceptions.EndOfPathException;
import exceptions.FallenException;
import exceptions.TimerWasNotStartedException;
import exceptions.WalkNotStartedException;
import model.figures.AbstractFigure;
import model.figures.ILevitating;
import model.path.Tile;

public class FiguresWalk {
	public static final int TIMER_UNINITIALIZED = -1;

	public enum State {
		NOT_STARTED, WALKING, OBSERVING_FINISH_LINE, FINISHED, FALLEN;
	}

	private State state = State.NOT_STARTED;

	public State getState() {
		return state;
	}

	private final int id;
	private final AbstractFigure figure;

	private final model.path.Path path;
	private final model.path.Path.Iterator pathIterator;
	private Tile currentTile = null;

	private StringBuilder trace = new StringBuilder();

	private long startTimeTemp = TIMER_UNINITIALIZED;
	private long elapsedTime = 0;

	public FiguresWalk(AbstractFigure figure, model.path.Path path) {
		this.id = figure.getId();
		this.figure = figure;
		this.path = path;
		this.pathIterator = this.path.getIterator();
	}

	public Tile getCurrentTile() throws WalkNotStartedException, FallenException, EndOfPathException {
		if (State.WALKING == state || State.OBSERVING_FINISH_LINE == state)
			return currentTile;

		throwIfHasNotStartedWalking();
		throwIfHasFallenBefore();
		throwIfHasReachedFinishBefore();
		return null;
	}

	public synchronized boolean hasNextTile() throws FallenException {
		throwIfHasFallenBefore();
		return pathIterator.hasNext();
	}

	public synchronized Tile nextTile() throws FallenException, EndOfPathException {
		if (hasNextTile()) {
			if (State.NOT_STARTED == state) {
				state = State.WALKING;
			}

			currentTile = pathIterator.next();
			if (!pathIterator.hasNext()) {
				state = State.OBSERVING_FINISH_LINE;
			}

			trace.append(currentTile.getNumber()).append("-");
			return currentTile;
		}
		throwIfHasFallenBefore();
		throwIfHasReachedFinishBefore();
		return null;
	}

	public synchronized void endWalkSuccesfully() {
		state = State.FINISHED;
		figure.nextRoundDiamonds();
		stopTimer(System.nanoTime());
	}

	public synchronized void collectDiamond()
			throws WalkNotStartedException, FallenException, EndOfPathException {
		if ((State.WALKING == state || State.OBSERVING_FINISH_LINE == state)) {
			figure.collectDiamond();
		}
		throwIfHasNotStartedWalking();
		throwIfHasFallenBefore();
	}

	public synchronized boolean fallIfOnHole() throws WalkNotStartedException, FallenException, EndOfPathException {
		if (State.WALKING == state) {
			if (figure instanceof ILevitating)
				return false;

			if (currentTile.isHasHole()) {
				currentTile.setIsOccupied(false);
				currentTile = null;
				state = State.FALLEN;
				return true;
			}
			return false;
		}
		throwIfHasNotStartedWalking();
		throwIfHasFallenBefore();
		throwIfHasReachedFinishBefore();
		return false;
	}

	private void throwIfHasNotStartedWalking() {
		if (State.NOT_STARTED == state) {
			throw new WalkNotStartedException(figure + " has not started its walk yet.");
		}
	}

	private synchronized void throwIfHasFallenBefore() throws FallenException {
		if (State.FALLEN == state) {
			throw new FallenException(figure + " has fallen in a hole.");
		}
	}

	private synchronized void throwIfHasReachedFinishBefore() throws EndOfPathException {
		if (State.OBSERVING_FINISH_LINE == state || State.FINISHED == state) {
			currentTile = null;
			throw new EndOfPathException(figure + " has already finished its walk.");
		}
	}

	public synchronized void startTimer(long now) {
		if (startTimeTemp != TIMER_UNINITIALIZED) {
			stopTimer(now);
		}
		startTimeTemp = now;
	}

	public synchronized long getElapsedTimeInNanoseconds() {
		if (elapsedTime == 0)
			throw new TimerWasNotStartedException();
		return elapsedTime;
	}

	public synchronized void stopTimer(long now) {
		long endTimeTemp = now;
		addToElapsedTime(endTimeTemp - startTimeTemp);
		startTimeTemp = TIMER_UNINITIALIZED;
	}

	private synchronized void addToElapsedTime(long millis) {
		elapsedTime += millis;
	}

	public synchronized String getTrace() {
		// trim trailing "-"
		return trace.substring(0, trace.length() - 1);
	}

	public int getId() {
		return id;
	}

	public AbstractFigure getFigure() {
		return figure;
	}

	public model.path.Path getPath() {
		return path;
	}

}
