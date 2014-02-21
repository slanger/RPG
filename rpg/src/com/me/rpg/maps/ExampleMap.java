package com.me.rpg.maps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.me.rpg.RPG;
import com.me.rpg.characters.NonplayableCharacter;
import com.me.rpg.characters.PlayableCharacter;
import com.me.rpg.state.BasicMoveState;
import com.me.rpg.state.CompositeState;
import com.me.rpg.state.EngageEnemy;
import com.me.rpg.state.State;
import com.me.rpg.state.transition.BooleanTransition;
import com.me.rpg.state.transition.IntTransition;
import com.me.rpg.state.transition.Transition;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.Coordinate;

public class ExampleMap extends Map
{

	public static final String MAP_TMX_PATH = "maps/example/example.tmx";

	public ExampleMap()
	{
		super();

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
