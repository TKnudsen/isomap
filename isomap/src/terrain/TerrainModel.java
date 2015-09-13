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

import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;

import common.OctDirection;


/**
 * Defines a terrain model in a very general form
 * @author Martin Steiger
 */
public interface TerrainModel
{
	/**
	 * @param mapX the map x coordinate
	 * @param mapY the map y coordinate
	 * @return a map that contains all neighbor terrain types
	 */
	public Map<OctDirection, TerrainType> getNeighbors(int mapX, int mapY);
	
	/**
	 * Retrieves the TerrainType of an adjacent Tile
	 * @param x
	 * @param y
	 * @param dir
	 * @return
	 */
	public TerrainType getNeighborFor(int x, int y, OctDirection dir);
	
	/**
	 * retrieves the index of an adjacent Tile
	 * @param x
	 * @param y
	 * @param dir
	 * @return
	 */
	public Integer[] getNeighborIndex(int x, int y, OctDirection dir);

	/**
	 * @param worldX0 the left world coordinate
	 * @param worldY0 the top world coordinate
	 * @param worldX1 the right world coordinate
	 * @param worldY1 the bottom world coordinate
	 * @return a list of all tiles that intersect with the given rect, <b>sorted by y-value</b>
	 */
	public List<Tile> getTilesInRect(int worldX0, int worldY0, int worldX1, int worldY1);

	/**
	 * @param worldX the world x
	 * @param worldY the world y
	 * @return the tile at the given location if available
	 */
	public Optional<Tile> getTileAtWorldPos(int worldX, int worldY);

	/**
	 * @param mapX the map x coordinate
	 * @param mapY the map y coordinate
	 * @return the world x coordinate
	 */
	public int getWorldY(int mapX, int mapY);

	/**
	 * @param mapX the map x coordinate
	 * @param mapY the map y coordinate
	 * @return the world x coordinate
	 */
	public int getWorldX(int mapX, int mapY);

	/**
	 * @param mapX the map x coordinate
	 * @param mapY the map y coordinate
	 * @return the world x coordinate
	 */
	public Tile getTile(int x, int y);

	/**
	 * @return the map width
	 */
	public int getMapWidth();

	/**
	 * @return the map height
	 */
	public int getMapHeight();
}
