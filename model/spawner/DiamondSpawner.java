package model.spawner;

import java.util.List;
import model.path.Tile;

public class DiamondSpawner extends AbstractSpawner implements ISpawner {
		
	public DiamondSpawner(model.path.Path path) {
		super(path);
	}
	
	@Override
	public List<Tile> spawn() {
		int diamondsCount = super.rand.nextInt(path.getDimension() - 1) + 2;
		List<Tile> spawnTiles = getSpawnTilesWhenCountIsValid(diamondsCount);
		for (Tile tile : spawnTiles) {
			tile.setHasDiamond(true);
		}
		return spawnTiles;
	}
}
