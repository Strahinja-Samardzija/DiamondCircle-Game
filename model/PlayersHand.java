package model;

import exceptions.PlayerOutOfFiguresException;
import model.player.Player;

public class PlayersHand {

	private final Player player;
	private final FiguresWalk[] walks;
	private final HandsWalksIterator handsWalksIterator;

	public class HandsWalksIterator {
		private int index = 1;
		private FiguresWalk currentWalk = walks[0];

		public boolean hasNext() {
			return index < walks.length;
		}

		public FiguresWalk next() throws PlayerOutOfFiguresException {
			if (hasNext()) {
				currentWalk = walks[index++];
				return currentWalk;
			} else
				throw new PlayerOutOfFiguresException(player + " is out of figures.");
		}

		public FiguresWalk getCurrentWalk() {
			return currentWalk;
		}

	}

	public PlayersHand(Player player, FiguresWalk[] walks) {
		this.player = player;
		this.walks = walks;
		this.handsWalksIterator = new HandsWalksIterator();
	}

	public HandsWalksIterator getIterator() {
		return handsWalksIterator;
	}

	public Player getPlayer() {
		return player;
	}

	public FiguresWalk[] getWalks() {
		return walks;
	}

}
