package model.cards;

import static model.cards.CardDeque.STEPS;

public class StepsCard implements ICard, IStepsCard {

	private final STEPS steps;

	public StepsCard(STEPS value) {
		this.steps = value;
	}

	@Override
	public STEPS getSteps() {
		return steps;
	}

}
