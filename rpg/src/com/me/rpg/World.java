package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;

public class World implements Disposable
{

	private BitmapFont debugFont;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Map map;

	public Map getMap()
	{
		return map;
	}

	public void setMap(Map map)
	{
		this.map.dispose();
		this.map.setUpdateEnable(false);
		Timer.schedule(new ChangeMapTask(map), 1.0f);
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
	}

	public World(SpriteBatch batch, OrthographicCamera camera)
	{
		this.batch = batch;
		this.camera = camera;

		// create map
		map = new ExampleMap(this, batch, camera);

		// create debug font
		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red
	}

	public void render()
	{
		map.render();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// render HUD and overlays
		float fpsX = camera.position.x - camera.viewportWidth / 2 + 15;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

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
