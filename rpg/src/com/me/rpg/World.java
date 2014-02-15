package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.me.rpg.ai.Dialogue;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;
import com.me.rpg.reputation.ReputationSystem;
import com.me.rpg.utils.Task;
import com.me.rpg.utils.Timer;

public class World implements Disposable
{

	public static String WARP_SOUND_PATH = "music/ALTTP_warp_sound.mp3";
	public static String WHITE_DOT_PATH = "white_dot.png";
	public static String FADED_RED_DOT_PATH = "faded_red_dot.png";

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private BitmapFont debugFont;
	private Map map;

	private Dialogue dialogue;
	private ReputationSystem reputationSystem;

	private boolean warping = false;
	private float warpingAlpha;
	private Sound warpSound;
	private Sprite whiteScreen;

	private Timer timer = new Timer();

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
		warping = true;
		warpingAlpha = 0f;
		warpSound.play();
		timer.scheduleTask(new Task()
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
		timer.scheduleTask(new WarpToAnotherMapTask(map), 3.0f);
	}

	private class WarpToAnotherMapTask extends Task
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

			timer.scheduleTask(new Task()
			{

				@Override
				public void run()
				{
					warpingAlpha -= 0.1f;
				}

			}, 0f, 0.1f, 10);

			timer.scheduleTask(new Task()
			{

				@Override
				public void run()
				{
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

	public ReputationSystem getReputationSystem()
	{
		return reputationSystem;
	}

	public World(SpriteBatch batch, OrthographicCamera camera)
	{
		this.batch = batch;
		this.camera = camera;

		// create map
		dialogue = new Dialogue(batch, camera);
		reputationSystem = new ReputationSystem(this);

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
			whiteScreen.setPosition(camera.position.x - camera.viewportWidth
					/ 2, camera.position.y - camera.viewportHeight / 2);
			whiteScreen.draw(batch, warpingAlpha);
		}

		// render HUD and overlays
		float fpsX = camera.position.x - camera.viewportWidth / 2 + 15;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

		batch.end();
	}

	public boolean isGameOver()
	{
		return map.isGameOver();
	}

	public void update(float deltaTime)
	{
		timer.update(deltaTime);
		map.update(deltaTime);
	}

	@Override
	public void dispose()
	{
		map.dispose();
		debugFont.dispose();
	}

}
