package com.me.rpg.maps;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.me.rpg.RPG;
import com.me.rpg.World;

public class ExampleMap extends Map
{

	public static final String MAP_TMX_PATH = "maps/example/example.tmx";

	public ExampleMap(World world, SpriteBatch batch, OrthographicCamera camera)
	{
		super(world, batch, camera);

		mapType = MapType.EXAMPLE;

		// get Tiled map
		tiledMap = RPG.manager.get(MAP_TMX_PATH, TiledMap.class);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);

		// set layers
		backgroundLayers = new int[] { 0, 1 };
		foregroundLayers = new int[] { 2 };

		// map setup
		setup();
	}

}
