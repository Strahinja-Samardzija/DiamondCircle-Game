package model.spawner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import model.path.Tile;

public abstract class AbstractSpawner {

	Random rand = new Random();

	protected final model.path.Path path;
	protected final int pathLength;

	protected AbstractSpawner(model.path.Path path) {
		this.path = path;
		this.pathLength = this.path.getLength();
	}

	protected List<Tile> getSpawnTilesWhenCountIsValid(int count) {
		ArrayList<Tile> spawnTiles = new ArrayList<>();

		List<Integer> distinctIndices = rand.ints(0, pathLength).distinct().limit(count).sorted().boxed().toList();

		var pathIterator = path.getIterator();
		int spawnedCount = 0;
		for (int i = 0; pathIterator.hasNext() && spawnedCount < distinctIndices.size(); i++) {
			if (i == distinctIndices.get(spawnedCount)) {
				spawnTiles.add(pathIterator.next());
				spawnedCount++;
			} else {
				pathIterator.next();
			}
		}
		return spawnTiles;
	}
}
