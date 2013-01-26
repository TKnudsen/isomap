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

import static dirs.DiamondDirection.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import terrain.TerrainType;
import dirs.DiamondDirection;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class Pattern
{
	private Map<DiamondDirection, TerrainType> map = new HashMap<DiamondDirection, TerrainType>();

	/**
	 * @param tuple
	 */
	public Pattern(TerrainType nw, TerrainType ne, TerrainType se, TerrainType sw)
	{
		map.put(NORTH_WEST, nw);
		map.put(SOUTH_WEST, sw);
		map.put(SOUTH_EAST, se);
		map.put(NORTH_EAST, ne);
	}

	public int size()
	{
		return map.size();
	}
	
	public TerrainType get(DiamondDirection dir)
	{
		return map.get(dir);
	}
	
}
