/*
 * Copyright (C) 2012-2013 Martin Steiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import common.DefaultValueMap;
import common.GridData;
import common.OctDirection;

import terrain.TerrainModel;
import terrain.TerrainType;
import terrain.Tile;

/**
 * TODO Type description
 * 
 * @author Martin Steiger
 */
public class IndexProvider {
	private TileIndexResolver indexResolver;

	private GridData<TileIndex> currIndices;
	private GridData<TileIndex> nextIndices;
	private GridData<Integer> animSteps;

	private final Random random = new Random(12345);

	private Map<TerrainType, Integer> maxSteps = new DefaultValueMap<>(0);

	private TerrainModel terrainModel;

	/**
	 * @param resolver
	 */
	public IndexProvider(TerrainModel terrainModel, TileSet tileset) {
		this.indexResolver = new TileIndexResolver(tileset);
		this.terrainModel = terrainModel;

		int mapWidth = terrainModel.getMapWidth();
		int mapHeight = terrainModel.getMapHeight();

		// TODO: move somewhere else
		maxSteps.put(TerrainType.WATER, 8);

		TileIndex invalidTile = tileset.getInvalidTileIndex();

		currIndices = new GridData<TileIndex>(mapWidth, mapHeight, invalidTile);
		nextIndices = new GridData<TileIndex>(mapWidth, mapHeight, invalidTile);
		animSteps = new GridData<Integer>(mapWidth, mapHeight, 0);

		for (int y = 0; y < terrainModel.getMapHeight(); y++) {
			for (int x = 0; x < terrainModel.getMapWidth(); x++) {
				Tile tile = terrainModel.getTile(x, y);

				Set<TileIndex> indices = indexResolver.getIndicesFor(tile.getTerrain(), terrainModel.getNeighbors(x, y));

				currIndices.setData(x, y, getRandom(indices));

				Integer steps = maxSteps.get(tile.getTerrain());

				if (steps > 0) {
					nextIndices.setData(x, y, getRandomOtherThan(indices, currIndices.getData(x, y)));
					animSteps.setData(x, y, random.nextInt(steps));
				}
			}
		}

	}

	private void updateIndex(int mapX, int mapY) {
		TileIndex index = nextIndices.getData(mapX, mapY);

		TerrainType terrain = terrainModel.getTile(mapX, mapY).getTerrain();
		Set<TileIndex> indices = indexResolver.getIndicesFor(terrain, terrainModel.getNeighbors(mapX, mapY));

		if (indices.size() <= 1)
			return;

		index = getRandomOtherThan(indices, index);

		nextIndices.setData(mapX, mapY, index);
	}

	private TileIndex getRandom(Set<TileIndex> indices) {
		int rand = random.nextInt(indices.size());

		Iterator<TileIndex> it = indices.iterator();
		for (int i = 0; i < rand; i++) {
			it.next();
		}

		return it.next();
	}

	/**
	 * @param indices
	 * @param index
	 */
	private TileIndex getRandomOtherThan(Set<TileIndex> indices, TileIndex index) {
		List<TileIndex> list = new ArrayList<TileIndex>(indices);

		// exclude old index from the set of possible new indices
		int rand = random.nextInt(list.size() - 1) + 1;

		// oldPos can be -1 if the old index was not set before
		int oldPos = list.indexOf(index);

		// but rand is always in [1..size] so the sum is at least 0
		index = list.get((oldPos + rand) % list.size());

		return index;
	}

	/**
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public TileIndex getCurrentIndex(int mapX, int mapY) {
		return currIndices.getData(mapX, mapY);
	}

	/**
	 * 
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public boolean contains(int mapX, int mapY) {
		return mapX >= 0 && mapY >= 0 && currIndices.getWidth() > mapX && currIndices.getHeight() > mapY;
	}

	/**
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public TileIndex getNextIndex(int mapX, int mapY) {
		return nextIndices.getData(mapX, mapY);
	}

	/**
	 * @param mapX
	 * @param mapY
	 * @return
	 */
	public Integer getAnimStep(int mapX, int mapY) {
		return animSteps.getData(mapX, mapY);
	}

	/**
	 * @param terrain
	 * @return
	 */
	public Integer getAnimSteps(TerrainType terrain) {
		return maxSteps.get(terrain);
	}

	/**
	 * 
	 */
	public void nextFrame() {
		for (int y = 0; y < terrainModel.getMapHeight(); y++) {
			for (int x = 0; x < terrainModel.getMapWidth(); x++) {
				Tile tile = terrainModel.getTile(x, y);
				Integer maxStep = maxSteps.get(tile.getTerrain());

				if (maxStep > 0) {
					Integer step = animSteps.getData(x, y);
					int nextStep = step + 1;

					if (nextStep >= maxStep) {
						nextStep = 0;

						TileIndex index = nextIndices.getData(x, y);
						currIndices.setData(x, y, index);
						updateIndex(x, y);
					}

					animSteps.setData(x, y, Integer.valueOf(nextStep));
				}
			}
		}
	}

	/**
	 * @param terrain
	 * @param neighbors
	 * @return
	 */
	public Set<TileIndex> getOverlaysFor(TerrainType terrain, Map<OctDirection, TerrainType> neighbors) {
		return indexResolver.getOverlaysFor(terrain, neighbors);
	}

}
