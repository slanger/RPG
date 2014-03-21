package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOverScreen implements Screen
{

	// private ScreenHandler screenHandler;
	private SpriteBatch spriteBatch;

	private BitmapFont debugFont;

	// CONSTRUCTOR
	public GameOverScreen(ScreenHandler screenHandler, SpriteBatch spriteBatch)
	{
		// this.screenHandler = screenHandler;
		this.spriteBatch = spriteBatch;
		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red
	}

	@Override
	public void show()
	{

	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();

		// render HUD and overlays
		debugFont.draw(spriteBatch, "GAME OVER", 150, 250);

		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height)
	{

	}

	@Override
	public void hide()
	{
		dispose();
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

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

}
