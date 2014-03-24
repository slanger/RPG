package com.me.rpg.maps;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.badlogic.gdx.audio.Music;
import com.me.rpg.ScreenHandler;
import com.me.rpg.World;
import com.me.rpg.utils.Timer;

public class PrototypeMap extends Map
{

	private static final long serialVersionUID = -5562012439124121495L;

	public static final String MAP_TMX_PATH = "maps/prototype_map/prototype_map.tmx";
	public static final String BACKGROUND_MUSIC_START = "music/ALTTP_overworld_start.wav";
	public static final String BACKGROUND_MUSIC_LOOP = "music/ALTTP_overworld_loop.wav";

	private transient Music startMusic, loopMusic;
	private StartLoopMusicTask startLoopMusicTask = new StartLoopMusicTask();

	public PrototypeMap(World world)
	{
		super(world, MapType.PROTOTYPE, MAP_TMX_PATH);

		// map setup
		setup();

		create();
	}

	private void create()
	{
		// get music
		startMusic = ScreenHandler.manager.get(BACKGROUND_MUSIC_START);
		loopMusic = ScreenHandler.manager.get(BACKGROUND_MUSIC_LOOP);
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		setup();
		create();
	}

	@Override
	public void open()
	{
		super.open();
		startMusic.play();
		timer.scheduleTask(startLoopMusicTask, 6.9f); // length of intro music
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

		private static final long serialVersionUID = -165838319556649887L;

		@Override
		public void run()
		{
			loopMusic.setLooping(true);
			loopMusic.play();
		}

	}

}
