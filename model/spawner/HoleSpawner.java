package model.spawner;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import logging.LoggerRegistration;
import model.path.Tile;
import properties.PropertiesSingleton;
import property_keys_and_defaults.PropertyKeys;

public class HoleSpawner extends AbstractSpawner implements ISpawner {

	{
		try {
			new LoggerRegistration<>(HoleSpawner.class).register("HoleSpawner.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	private static final String MUST_BE_INT_MESSAGE = "Invalid value for \"" + PropertyKeys.N_HOLES_KEY
			+ "\" configuration property.\n" + "It must be an integer.\n";
	private static final String TOO_MANY_HOLES_MESSAGE = "Invalid value for \"N_Holes\" configuration property.\n"
			+ "It can't be more than a third of the path's length for chosen dimension of the matrix.\n";

	public HoleSpawner(model.path.Path path) {
		super(path);
	}

	@Override
	public List<Tile> spawn() {
		int holesCount = 0;
		try {
			holesCount = Integer
					.parseInt(PropertiesSingleton.getInstance().getProperties().getProperty(PropertyKeys.N_HOLES_KEY));
		} catch (NumberFormatException e) {
			Logger.getLogger(HoleSpawner.class.getName()).log(Level.SEVERE,
					MUST_BE_INT_MESSAGE);
			throw new AssertionError(MUST_BE_INT_MESSAGE);
		}

		if (holesCount > (int) (Math.ceil((double) pathLength / 3))) {
			Logger.getLogger(HoleSpawner.class.getName()).log(Level.SEVERE, TOO_MANY_HOLES_MESSAGE);
			throw new AssertionError(TOO_MANY_HOLES_MESSAGE);
		}

		List<Tile> spawnTiles = getSpawnTilesWhenCountIsValid(holesCount);
		for (Tile tile : spawnTiles) {
			tile.setHasHole(true);
		}
		return spawnTiles;
	}
}
