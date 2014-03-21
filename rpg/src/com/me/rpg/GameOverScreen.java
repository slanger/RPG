package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.me.rpg.utils.Timer;

public class GameOverScreen implements Screen
{

	private ScreenHandler screenHandler;
	private SpriteBatch spriteBatch = new SpriteBatch();
	private BitmapFont debugFont = new BitmapFont();
	private Timer timer = new Timer();
	private boolean enableControls = false;

	public GameOverScreen(ScreenHandler screenHandler)
	{
		this.screenHandler = screenHandler;
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red
	}

	@Override
	public void render(float deltaTime)
	{
		update(deltaTime);

		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();

		debugFont.draw(spriteBatch, "GAME OVER", 150, 250);

		spriteBatch.end();
	}

	private void update(float deltaTime)
	{
		timer.update(deltaTime);

		if (enableControls)
		{
			if (Gdx.input.isTouched())
			{
				screenHandler.moveToOtherScreen(screenHandler.startScreen);
			}
		}
	}

	@Override
	public void dispose()
	{
		spriteBatch.dispose();
		debugFont.dispose();
	}

	@Override
	public void show()
	{
		enableControls = false;
		timer.scheduleTask(new Timer.Task()
		{

			@Override
			public void run()
			{
				enableControls = true;
			}

		}, 2);
	}

	@Override
	public void hide()
	{
		timer.clear();
	}

	@Override
	public void resize(int width, int height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

}
