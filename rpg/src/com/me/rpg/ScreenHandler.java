package com.me.rpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenHandler extends Game {

	private SpriteBatch spriteBatch;
	public RPG rpgScreen;
	public StartScreen startScreen;

	@Override
	public void create() {
		
		Texture.setEnforcePotImages(false);
		spriteBatch = new SpriteBatch();
		startScreen = new StartScreen(this, spriteBatch);
		rpgScreen = new RPG();
		setScreen(startScreen);
	}
	
	public ScreenHandler() {
		
	}
	
	

	@Override
	public void dispose() {

	}

	@Override
	public void render() {		
		super.render();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	
}