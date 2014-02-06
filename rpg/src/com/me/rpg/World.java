package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;
import com.me.rpg.ai.Dialogue;

public class World implements Disposable
{

	public static String WARP_SOUND_PATH = "music/ALTTP_warp_sound.mp3";

	private BitmapFont debugFont;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	
	private Map map;

	private Dialogue dialogue;

	private boolean warping = false;
	private Sound warpSound;

	public Map getMap()
	{
		return map;
	}
	public void setMap(Map map)
	{
		this.map.dispose();
		this.map.setUpdateEnable(false);
		warping = true;
		warpSound.play();
		Timer.schedule(new ChangeMapTask(map), 3.0f);
	}
	private class ChangeMapTask extends Timer.Task
	{

		private Map newMap;

		ChangeMapTask(Map newMap)
		{
			this.newMap = newMap;
		}

		@Override
		public void run()
		{
			changeMap(newMap);
		}

	}

	private void changeMap(Map newMap)
	{
		map = newMap;
		warping = false;
	}

	public Dialogue getDialogue()
	{
		return dialogue;
	}
	
	public World(SpriteBatch batch, OrthographicCamera camera)
	{
		this.batch = batch;
		this.camera = camera;

		// create map
		dialogue = new Dialogue(this,batch,camera);
		map = new ExampleMap(this, batch, camera);
		// create debug font
		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red

		warpSound = RPG.manager.get(WARP_SOUND_PATH);
	}

	public void render()
	{
		map.render();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		if (warping)
		{
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		}

		// render HUD and overlays
		float fpsX = camera.position.x - camera.viewportWidth / 2 + 15;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

		//temporary dialogue stuff
		if(dialogue.getInDialogue()==true)
		{
			dialogue.render();
		}
		//
		
		batch.end();
	}

	public void update(float deltaTime)
	{
		map.update(deltaTime);
	}

	@Override
	public void dispose()
	{
		map.dispose();
		debugFont.dispose();
	}

}
