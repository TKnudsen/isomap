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

package terrain;

import static terrain.TerrainType.UNDEFINED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tiles.TileSet;

import com.google.common.base.Optional;

import common.GridData;
import common.OctDirection;

/**
 * TODO Type description
 * 
 * @author Martin Steiger
 */
public class IsoTerrainModel implements TerrainModel {
	private final GridData<Tile> tiles;

	private final TileSet tileSet;

	private int mapWidth;
	private int mapHeight;

	/**
	 * @param data
	 *            the terrain data
	 */
	public IsoTerrainModel(GridData<TerrainType> terrainData, TileSet tileSet) {
		mapWidth = terrainData.getWidth();
		mapHeight = terrainData.getHeight();

		this.tiles = new GridData<Tile>(mapWidth, mapHeight, null);
		this.tileSet = tileSet;

		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				TerrainType terrain = terrainData.getData(x, y);
				Tile tile = new Tile(x, y, terrain);
				tiles.setData(x, y, tile);
			}
		}
	}

	private static boolean isOdd(int v) {
		return v % 2 == 1;
	}

	@Override
	public int getMapHeight() {
		return mapHeight;
	}

	@Override
	public int getMapWidth() {
		return mapWidth;
	}

	@Override
	public Tile getTile(int mapX, int mapY) {
		return tiles.getData(mapX, mapY);
	}

	@Override
	public int getWorldX(int mapX, int mapY) {
		return mapX * tileSet.getTileWidth() + (mapY % 2) * tileSet.getTileWidth() / 2;
	}

	@Override
	public int getWorldY(int x, int y) {
		return y * tileSet.getTileHeight() / 2;
	}

	@Override
	public Optional<Tile> getTileAtWorldPos(int worldX, int worldY) {
		double w = tileSet.getTileWidth();
		double h = tileSet.getTileHeight();

		// Origin is at (0, h/2)
		worldY -= h / 2;

		// ( w/2 ) ( w/2 )
		// r = ( ) c = ( )
		// ( h/2 ) (-h/2 )

		// x = r * w + c * w
		// y = r * h - c * h

		// solving for r gives r = y/h + c

		// Math.floor() rounds negative values down whereas casting to int
		// rounds them up
		int r = (int) Math.floor(worldX / w + worldY / h);
		int c = (int) Math.floor(worldX / w - worldY / h);

		// r and c are only one tile edge long
		int x = (int) Math.floor((r + c) / 2.0);

		// r - c always >= 0
		int y = r - c;

		if (x >= 0 && x < mapWidth && y >= 0 && y < mapHeight)

		{
			return Optional.of(getTile(x, y));
		}

		return Optional.absent();
	}

	@Override
	public List<Tile> getTilesInRect(int worldX0, int worldY0, int worldX1, int worldY1) {
		int tileWidth = tileSet.getTileWidth();
		int tileHeight = tileSet.getTileHeight();

		// this computes the map-y based on rectangular shapes
		// it is then independent of x - basically the inverse of getWorldY()
		int y0 = (worldY0 * 2) / tileHeight - 1;
		int y1 = (worldY1 * 2) / tileHeight;

		// Restrict to map bounds
		int minY = Math.max(y0, 0);
		int maxY = Math.min(y1, mapHeight - 1);

		List<Tile> result = new ArrayList<Tile>();

		for (int y = minY; y <= maxY; y++) {
			int x0 = (worldX0 - (y % 2) * tileWidth / 2) / tileWidth;
			int x1 = (worldX1 - (y % 2) * tileWidth / 2) / tileWidth;

			int minX = Math.max(x0, 0);
			int maxX = Math.min(x1, mapWidth - 1);

			for (int x = minX; x <= maxX; x++) {
				result.add(getTile(x, y));
			}
		}

		return result;
	}

	@Override
	public Map<OctDirection, TerrainType> getNeighbors(int mapX, int mapY) {
		Map<OctDirection, TerrainType> pattern = new HashMap<OctDirection, TerrainType>();

		for (OctDirection dir : OctDirection.values()) {
			TerrainType neigh = getNeighborFor(mapX, mapY, dir);

			if (neigh != UNDEFINED)
				pattern.put(dir, neigh);
		}

		return pattern;
	}

	/**
	 * @param x
	 *            the x coord
	 * @param y
	 *            the y coord
	 * @return the type or UNDEFINED if x or y are invalid
	 */
	private TerrainType getTerrain(int x, int y) {
		// x and y are often invalid map coords.
		if (x < 0 || x >= getMapWidth())
			return UNDEFINED;

		if (y < 0 || y >= getMapHeight())
			return UNDEFINED;

		return getTile(x, y).getTerrain();
	}

	@Override
	public TerrainType getNeighborFor(int x, int y, OctDirection dir) {
		switch (dir) {
		case EAST:
			return getTerrain(x + 1, y);

		case NORTH:
			return getTerrain(x, y - 2);

		case SOUTH:
			return getTerrain(x, y + 2);

		case WEST:
			return getTerrain(x - 1, y);

		case NORTH_EAST:
			if (isOdd(y))
				return getTerrain(x + 1, y - 1);
			else
				return getTerrain(x, y - 1);

		case NORTH_WEST:
			if (isOdd(y))
				return getTerrain(x, y - 1);
			else
				return getTerrain(x - 1, y - 1);
		case SOUTH_EAST:
			if (isOdd(y))
				return getTerrain(x + 1, y + 1);
			else
				return getTerrain(x, y + 1);
		case SOUTH_WEST:
			if (isOdd(y))
				return getTerrain(x, y + 1);
			else
				return getTerrain(x - 1, y + 1);
		}

		return UNDEFINED;
	}

	@Override
	public Integer[] getNeighborIndex(int x, int y, OctDirection dir) {
		switch (dir) {
		case EAST:
			return new Integer[] { x + 1, y };

		case NORTH:
			return new Integer[] { x, y - 2 };

		case SOUTH:
			return new Integer[] { x, y + 2 };

		case WEST:
			return new Integer[] { x - 1, y };

		case NORTH_EAST:
			if (isOdd(y))
				return new Integer[] { x + 1, y - 1 };
			else
				return new Integer[] { x, y - 1 };

		case NORTH_WEST:
			if (isOdd(y))
				return new Integer[] { x, y - 1 };
			else
				return new Integer[] { x - 1, y - 1 };
		case SOUTH_EAST:
			if (isOdd(y))
				return new Integer[] { x + 1, y + 1 };
			else
				return new Integer[] { x, y + 1 };
		case SOUTH_WEST:
			if (isOdd(y))
				return new Integer[] { x, y + 1 };
			else
				return new Integer[] { x - 1, y + 1 };
		}

		return null;
	}

}
