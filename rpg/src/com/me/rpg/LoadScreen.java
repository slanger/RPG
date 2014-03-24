package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.me.rpg.utils.LoadBar;

public class LoadScreen implements Screen
{

	public static final String WHITE_DOT_PATH = "white_dot.png";
	public static final String FONT_PATH = "font/Microsoft_Uighur_white.fnt";

	private final ScreenHandler screenHandler;
	private final SpriteBatch batch = new SpriteBatch();
	private final LoadBar loadBar;
	private final BitmapFont font;

	public LoadScreen(ScreenHandler screenHandler)
	{
		this.screenHandler = screenHandler;

		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		float offset = 10;
		float width = screenWidth - 2 * offset;
		float height = screenHeight / 16;
		float x = offset;
		float y = screenHeight / 2 - height / 2;
		Texture loadTexture = ScreenHandler.manager.get(WHITE_DOT_PATH, Texture.class);
		loadBar = new LoadBar(x, y, width, height, loadTexture);

		font = ScreenHandler.manager.get(FONT_PATH, BitmapFont.class);
	}

	@Override
	public void render(float deltaTime)
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// if AssetManager is done loading assets, move to start screen
		if (ScreenHandler.manager.update())
		{
			screenHandler.setScreen(new StartScreen(screenHandler));
		}

		// update load bar
		loadBar.update(ScreenHandler.manager.getProgress());

		// draw load bar
		batch.begin();
		loadBar.render(batch);
		font.draw(batch, "Loading", loadBar.x, loadBar.y);
		batch.end();
	}

	@Override
	public void resize(int width, int height)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide()
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

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		
	}

}
