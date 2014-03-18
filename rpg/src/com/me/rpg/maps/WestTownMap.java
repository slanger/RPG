package com.me.rpg.maps;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.me.rpg.RPG;
import com.me.rpg.World;
import com.me.rpg.characters.PlayableCharacter;

public class WestTownMap extends Map
{

	public static final String MAP_TMX_PATH = "maps/west_town/west_town.tmx";

	public WestTownMap(World world, SpriteBatch batch, OrthographicCamera camera)
	{
		super(world, batch, camera);

		mapType = MapType.WEST_TOWN;

		// get Tiled map
		tiledMap = RPG.manager.get(MAP_TMX_PATH, TiledMap.class);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);

		// set layers
		backgroundLayers = new int[] { 0, 1, 2 };
		foregroundLayers = new int[] { 3 };

		// map setup
		setup();

		// CHARACTER SETUP

		PlayableCharacter player;
		final String PLAYER_NAME = "Player";
		final int width = 32;
		final int height = 32;

		// get spawn points and walking boundaries from .tmx
		MapObjects spawnPoints = getSpawnPoints();

		// create characters
		Texture spritesheet1 = RPG.manager.get(RPG.PLAYER_TEXTURE_PATH);
		player = new PlayableCharacter(PLAYER_NAME, spritesheet1, width,
				height, 16, 16, 0.15f, world);
		player.setSpeed(200f);

		// add characters to map
		RectangleMapObject playerSpawn = (RectangleMapObject) spawnPoints
				.get(PLAYER_NAME);
		addFocusedCharacterToMap(player, playerSpawn.getRectangle().x,
				playerSpawn.getRectangle().y);
	}

}
