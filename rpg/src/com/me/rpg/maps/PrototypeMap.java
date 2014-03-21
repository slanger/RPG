package com.me.rpg.maps;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.me.rpg.RPG;
import com.me.rpg.World;
import com.me.rpg.utils.Timer;

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

		// get music
		startMusic = RPG.manager.get(BACKGROUND_MUSIC_START);
		loopMusic = RPG.manager.get(BACKGROUND_MUSIC_LOOP);
	}

	@Override
	public void open()
	{
		super.open();
		startMusic.play();
		timer.scheduleTask(startLoopMusicTask, 7.0f); // length of intro music
	}

	@Override
	public void close()
	{
		super.close();
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
