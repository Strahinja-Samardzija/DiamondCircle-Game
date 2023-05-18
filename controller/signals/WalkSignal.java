package controller.signals;

import model.FiguresWalk;

public class WalkSignal {

	private final FiguresWalk newWalk;
	private final int calculatedSteps;

	public WalkSignal(FiguresWalk walk, int calculatedSteps) {
		if (FiguresWalk.State.NOT_STARTED != walk.getState() && FiguresWalk.State.WALKING != walk.getState()) {
			throw new IllegalArgumentException("The walk must be in a valid state for movement signalling");
		}
		this.newWalk = walk;
		this.calculatedSteps = calculatedSteps;
	}

	public FiguresWalk getWalk() {
		return newWalk;
	}

	public int getCalculatedSteps() {
		return calculatedSteps;
	}

}
