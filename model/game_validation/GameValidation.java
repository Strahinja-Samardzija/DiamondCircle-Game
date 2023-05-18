package model.game_validation;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import exceptions.*;

public class GameValidation {

	public static final int MIN_PLAYERS = 2;
	public static final int MAX_PLAYERS = 4;
	
	private final DimensionValidation dimensionValidation = new DimensionValidation();
	private final UsernameValidation usernameValidation = new UsernameValidation();
	private Handler handler;

	{
		try {
			handler = new FileHandler("./log/game_validation.log");
			Logger.getLogger(GameValidation.class.getName()).addHandler(handler);
		} catch (IOException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void validate(int dimension, String[] usernames)
			throws IllegalDimensionException, IllegalPlayersCountException, DuplicateUsernamesException {
		try {
			dimensionValidation.validate(dimension);
		} catch (IllegalDimensionException e) {
			Logger.getLogger(GameValidation.class.getName()).log(Level.INFO, e.fillInStackTrace().toString());
			throw e;
		}

		try {
			usernameValidation.validate(usernames);
		} catch (IllegalPlayersCountException | DuplicateUsernamesException e) {
			Logger.getLogger(GameValidation.class.getName()).log(Level.INFO, e.fillInStackTrace().toString());
			throw e;
		}
	}
}
