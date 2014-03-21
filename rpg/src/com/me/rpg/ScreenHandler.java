package com.me.rpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class ScreenHandler extends Game
{

	public StartScreen startScreen;
	public RPG rpgScreen;
	public GameOverScreen endScreen;

	@Override
	public void create()
	{
		Texture.setEnforcePotImages(false);
		startScreen = new StartScreen(this);
		rpgScreen = new RPG(this);
		endScreen = new GameOverScreen(this);
		setScreen(startScreen);
	}

	public ScreenHandler()
	{

	}

	public void moveToOtherScreen(Screen otherScreen)
	{
		setScreen(otherScreen);
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
