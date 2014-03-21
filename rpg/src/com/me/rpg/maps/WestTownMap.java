package com.me.rpg.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.me.rpg.RPG;

public class WestTownMap extends Map
{

	public static final String MAP_TMX_PATH = "maps/west_town/west_town.tmx";

	public WestTownMap()
	{
		super();

		mapType = MapType.WEST_TOWN;

		// get Tiled map
		tiledMap = RPG.manager.get(MAP_TMX_PATH, TiledMap.class);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);

		// set layers
		backgroundLayers = new int[] { 0, 1, 2 };
		foregroundLayers = new int[] { 3 };

		// map setup
		setup();
	}

}
