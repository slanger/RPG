package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;
import com.me.rpg.ai.Dialogue;

public class World implements Disposable
{

	public static String WARP_SOUND_PATH = "music/ALTTP_warp_sound.mp3";
	public static String WHITE_DOT_PATH = "white_dot.png";
	public static String FADED_RED_DOT_PATH = "faded_red_dot.png";

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private BitmapFont debugFont;
	private Map map;
	private boolean updateEnable = true;

	private Dialogue dialogue;

	private boolean warping = false;
	private float warpingAlpha;
	private Sound warpSound;
	private Sprite whiteScreen;

	public Map getMap()
	{
		return map;
	}

	public void setMap(Map map)
	{
		if (this.map != null)
		{
			this.map.dispose();
		}
		this.map = map;
	}

	public void warpToAnotherMap(Map map)
	{
		this.map.close();
		updateEnable = false;
		warping = true;
		warpingAlpha = 0f;
		warpSound.play();
		map.getTimer().scheduleTask(new Timer.Task()
		{

			@Override
			public void run()
			{
				warpingAlpha += 0.1f;
				if (warpingAlpha > 1f)
				{
					warpingAlpha = 1f;
					this.cancel();
				}
			}

		}, 0f, 0.1f);
		map.getTimer().scheduleTask(new WarpToAnotherMapTask(map), 3.0f);
	}

	private class WarpToAnotherMapTask extends Timer.Task
	{

		private Map newMap;

		WarpToAnotherMapTask(Map newMap)
		{
			this.newMap = newMap;
		}

		@Override
		public void run()
		{
			setMap(newMap);

			newMap.getTimer().scheduleTask(new Timer.Task()
			{

				@Override
				public void run()
				{
					warpingAlpha -= 0.1f;
				}

			}, 0f, 0.1f, 10);

			newMap.getTimer().scheduleTask(new Timer.Task()
			{

				@Override
				public void run()
				{
					updateEnable = true;
					warping = false;
					newMap.open();
				}

			}, 1.0f);
		}

	}

	public Dialogue getDialogue()
	{
		return dialogue;
	}

	public boolean isUpdating()
	{
		return updateEnable;
	}

	public void setUpdateEnable(boolean updateEnable)
	{
		this.updateEnable = updateEnable;

		// turn on/off Map Timer
		if (updateEnable)
		{
			map.getTimer().start();
		}
		else
		{
			map.getTimer().stop();
		}
	}

	public World(SpriteBatch batch, OrthographicCamera camera)
	{
		this.batch = batch;
		this.camera = camera;

		// create map
		dialogue = new Dialogue(this, batch, camera);
		map = new ExampleMap(this, batch, camera);

		// create debug font
		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red

		// warp resources
		warpSound = RPG.manager.get(WARP_SOUND_PATH, Sound.class);
		whiteScreen = new Sprite(RPG.manager.get(WHITE_DOT_PATH, Texture.class));
	}

	public void render()
	{
		map.render();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// temporary dialogue stuff
		if (dialogue.getInDialogue())
		{
			dialogue.render();
		}

		if (warping)
		{
			whiteScreen.setSize(camera.viewportWidth, camera.viewportHeight);
			whiteScreen.setPosition(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2);
			whiteScreen.draw(batch, warpingAlpha);
		}

		// render HUD and overlays
		float fpsX = camera.position.x - camera.viewportWidth / 2 + 15;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

		batch.end();
	}
	
	public boolean isGameOver() {
		return map.isGameOver();
	}

	public void update(float deltaTime)
	{
		if (updateEnable)
		{
			map.update(deltaTime);
		}
	}

	@Override
	public void dispose()
	{
		map.dispose();
		debugFont.dispose();
	}

}
