package model.cards;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;

public class CardDeque {
	public static final int CARDS_COUNT = 52;
	public static final int SPECIAL_CARDS_COUNT = 12;
 	public static final int OF_SAME_STEPS_CARDS_COUNT = 10;

	public enum STEPS {
		ONE(1), TWO(2), THREE(3), FOUR(4);

		public final int intValue;

		STEPS(int intValue) {
			this.intValue = intValue;
		}
	};

	private Deque<ICard> deque = new ArrayDeque<>(CARDS_COUNT);

	public CardDeque() {
		ArrayList<ICard> pool = new ArrayList<>();

		for (int i = 0; i < SPECIAL_CARDS_COUNT; i++)
			pool.add(new SpecialCard());

		for (var stepsCount : STEPS.values())
			for (int i = 0; i < OF_SAME_STEPS_CARDS_COUNT; i++)
				pool.add(new StepsCard(stepsCount));

		Collections.shuffle(pool);
		pool.forEach(deque::addFirst);
	}

	public ICard draw() {
		ICard temp = deque.removeFirst();
		deque.addLast(temp);
		return temp;
	}
}
