package model.dispatcher;

import model.FiguresWalk;
import model.PlayersHand;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import exceptions.PlayerOutOfFiguresException;


public class Dispatcher {

	private final Deque<PlayersHand.HandsWalksIterator> circularHandsQueue = new ArrayDeque<>();

	public Dispatcher(PlayersHand[] hands) {
		circularHandsQueue.addAll(Stream.of(hands).map(PlayersHand::getIterator).collect(Collectors.toList()));
	}

	public FiguresWalk nextWalk() throws PlayerOutOfFiguresException {
		FiguresWalk nextWalk = rotate();
		if (nextWalk != null) {
			return nextWalk;
		}
		return null;
	}

	private FiguresWalk rotate() {
		if (!circularHandsQueue.isEmpty()) {
			while (!circularHandsQueue.isEmpty()) {
				PlayersHand.HandsWalksIterator iterator = circularHandsQueue.removeFirst();

				FiguresWalk walk = iterator.getCurrentWalk();

				if (FiguresWalk.State.NOT_STARTED != walk.getState() && FiguresWalk.State.WALKING != walk.getState()) {
					if (iterator.hasNext()) {
						circularHandsQueue.addLast(iterator);
						return iterator.next();
					} else
						continue;
				}

				circularHandsQueue.addLast(iterator);
				return walk;
			}
		}
		return null;
	}
}
