package com.me.rpg.maps;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.me.rpg.RPG;
import com.me.rpg.World;

public class WestTownInsideHouse extends Map
{

	public static final String MAP_TMX_PATH = "maps/west_town/inside_house/inside_house.tmx";

	public WestTownInsideHouse(World world, SpriteBatch batch, OrthographicCamera camera)
	{
		super(world, batch, camera);

		mapType = MapType.WEST_TOWN_INSIDE_HOUSE;

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
