package model.game_validation;

import java.util.HashSet;

import exceptions.DuplicateUsernamesException;
import exceptions.IllegalPlayersCountException;

import static model.game_validation.GameValidation.*;

public class UsernameValidation {

	public boolean validate(String[] usernames) throws IllegalPlayersCountException, DuplicateUsernamesException {
		if (usernames == null || usernames.length < MIN_PLAYERS || usernames.length > MAX_PLAYERS)
			throw new IllegalPlayersCountException(
					"At least " + MIN_PLAYERS + " and at most " + MAX_PLAYERS + " can play a game.");

		if (hasDuplicates(usernames))
			throw new DuplicateUsernamesException("Usernames must be unique.");
		return true;
	}

	private boolean hasDuplicates(String[] usernames) {
		HashSet<String> uniqueSet = new HashSet<>(usernames.length);

		for (String username : usernames) {
			if (!uniqueSet.add(username))
				return true;
		}
		return false;
	}
}
