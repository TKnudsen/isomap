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

package view;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Set;

import terrain.TerrainModel;
import terrain.Tile;
import tiles.IndexProvider;
import tiles.TileIndex;
import tiles.TileSet;

/**
 * TODO Type description
 * 
 * @author Martin Steiger
 */
public class TileRendererBlending extends AbstractTileRenderer {
	// private IndexProvider indexProvider;

	/**
	 * @param terrainModel
	 */
	public TileRendererBlending(TerrainModel terrainModel, TileSet tileset) {
		super(terrainModel, tileset);

		this.indexProvider = new IndexProvider(terrainModel, tileset);
	}

	public void drawTiles(Graphics2D g, List<Tile> visibleTiles) {
		for (Tile tile : visibleTiles) {
			int mapY = tile.getMapY();
			int mapX = tile.getMapX();

			TileIndex currIndex = indexProvider.getCurrentIndex(mapX, mapY);
			TileIndex nextIndex = indexProvider.getNextIndex(mapX, mapY);

			TileIndex invalid = getTileset().getInvalidTileIndex();

			drawTile(g, currIndex, mapX, mapY);

			if (nextIndex != invalid) {
				Integer step = indexProvider.getAnimStep(mapX, mapY);
				Integer maxStep = indexProvider.getAnimSteps(tile.getTerrain());

				float alpha = (float) step / maxStep;

				Composite oldComp = g.getComposite();

				// TODO: Cache alpha composite
				AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

				g.setComposite(ac);
				drawTile(g, nextIndex, mapX, mapY);
				g.setComposite(oldComp);
			}

			Set<TileIndex> indices = indexProvider.getOverlaysFor(tile.getTerrain(), getTerrainModel().getNeighbors(mapX, mapY));

			for (TileIndex overlay : indices) {
				drawTile(g, overlay, mapX, mapY);
			}
		}
	}

	/**
	 * 
	 */
	public void nextFrame() {
		indexProvider.nextFrame();

	}

}
