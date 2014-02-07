package com.me.rpg.maps;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Timer;
import com.me.rpg.NonplayableCharacter;
import com.me.rpg.PlayableCharacter;
import com.me.rpg.RPG;
import com.me.rpg.World;

public class PrototypeMap extends Map
{
	
	public static final String MAP_TMX_PATH = "maps/prototype_map/prototype_map.tmx";
	public static final String BACKGROUND_MUSIC_START = "music/ALTTP_overworld_start.wav";
	public static final String BACKGROUND_MUSIC_LOOP = "music/ALTTP_overworld_loop.wav";

	private Music startMusic, loopMusic;
	private StartLoopMusicTask startLoopMusicTask = new StartLoopMusicTask();

	public PrototypeMap(World world, SpriteBatch batch, OrthographicCamera camera)
	{
		super(world, batch, camera);

		mapType = MapType.PROTOTYPE;

		// get Tiled map
		tiledMap = RPG.manager.get(MAP_TMX_PATH, TiledMap.class);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);

		// set layers
		backgroundLayers = new int[] { 0, 1 };
		foregroundLayers = new int[] { 2 };

		// map setup
		setup();

		// CHARACTER SETUP

		PlayableCharacter player;
		NonplayableCharacter npc1, npc2, npc3;
		final String PLAYER_NAME = "Player";
		final String NPC1_NAME = "NPC1";
		final String NPC2_NAME = "NPC2";
		final String NPC3_NAME = "NPC3";
		final int width = 32;
		final int height = 32;

		// get spawn points and walking boundaries from .tmx
		MapObjects spawnPoints = getSpawnPoints();
		MapObjects walkingBoundaries = getWalkingBoundaries();

		// create characters
		Texture spritesheet1 = RPG.manager.get(RPG.PLAYER_TEXTURE_PATH);
		player = new PlayableCharacter(PLAYER_NAME, spritesheet1, width, height, 16, 16,
				0.15f);
		player.setSpeed(200f);

		Texture spritesheet2 = RPG.manager.get(RPG.NPC_TEXTURE_PATH);
		RectangleMapObject boundary1 = (RectangleMapObject) walkingBoundaries.get("castle");
		npc1 = new NonplayableCharacter(NPC1_NAME, spritesheet2, width, height, 16, 16,
				0.15f, boundary1.getRectangle());

		RectangleMapObject boundary2 = (RectangleMapObject) walkingBoundaries.get("desert");
		npc2 = new NonplayableCharacter(NPC2_NAME, spritesheet2, width, height, 16, 16,
				0.15f, boundary2.getRectangle());

		RectangleMapObject boundary3 = (RectangleMapObject) walkingBoundaries.get("town");
		npc3 = new NonplayableCharacter(NPC3_NAME, spritesheet2, width, height, 16, 16,
				0.15f, boundary3.getRectangle());

		// add characters to map
		RectangleMapObject playerSpawn = (RectangleMapObject) spawnPoints.get(PLAYER_NAME);
		addFocusedCharacterToMap(player, playerSpawn.getRectangle().x,
				playerSpawn.getRectangle().y);
		RectangleMapObject npc1Spawn = (RectangleMapObject) spawnPoints.get(NPC1_NAME);
		addCharacterToMap(npc1, npc1Spawn.getRectangle().x, npc1Spawn.getRectangle().y);
		RectangleMapObject npc2Spawn = (RectangleMapObject) spawnPoints.get(NPC2_NAME);
		addCharacterToMap(npc2, npc2Spawn.getRectangle().x, npc2Spawn.getRectangle().y);
		RectangleMapObject npc3Spawn = (RectangleMapObject) spawnPoints.get(NPC3_NAME);
		addCharacterToMap(npc3, npc3Spawn.getRectangle().x, npc3Spawn.getRectangle().y);

		// setup weapons
		genericWeaponSetup(player);

		// get music
		startMusic = RPG.manager.get(BACKGROUND_MUSIC_START);
		loopMusic = RPG.manager.get(BACKGROUND_MUSIC_LOOP);
	}

	@Override
	public void open()
	{
		world.setUpdateEnable(true);
		startMusic.play();
		timer.scheduleTask(startLoopMusicTask, 7.192f); // length of intro music
	}

	@Override
	public void close()
	{
		timer.stop();
		startLoopMusicTask.cancel();
		startMusic.stop();
		loopMusic.stop();
	}

	private class StartLoopMusicTask extends Timer.Task
	{

		@Override
		public void run()
		{
			loopMusic.setLooping(true);
			loopMusic.play();
		}

	}

}
