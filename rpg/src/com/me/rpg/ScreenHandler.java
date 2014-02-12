package com.me.rpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenHandler extends Game
{

	private SpriteBatch spriteBatch;
	public RPG rpgScreen;
	public StartScreen startScreen;
	public GameOverScreen endScreen;

	@Override
	public void create()
	{
		Texture.setEnforcePotImages(false);
		spriteBatch = new SpriteBatch();
		startScreen = new StartScreen(this, spriteBatch);
		rpgScreen = new RPG(this);
		endScreen = new GameOverScreen(this, spriteBatch);
		setScreen(startScreen);
	}

	public ScreenHandler()
	{

	}

	@Override
	public void dispose()
	{

	}

	@Override
	public void render()
	{
		super.render();
	}

	@Override
	public void resize(int width, int height)
	{
		super.getScreen().resize(width, height);
	}

	@Override
	public void pause()
	{

	}

	@Override
	public void resume()
	{

	}

}
