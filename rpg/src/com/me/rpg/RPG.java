package com.me.rpg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RPG implements ApplicationListener
{
	
	public static OrthographicCamera camera;
	public static Character hero, villain;
	private Map map;
	private SpriteBatch batch;
	
	@Override
	public void create()
	{
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch = new SpriteBatch();
		
		Texture spritesheet = new Texture(Gdx.files.internal("hero.png"));
		hero = new PlayableCharacter("Hero", spritesheet, 32, 32, 16, 16, (int)(camera.viewportWidth / 3), (int)(camera.viewportHeight / 2), 0.15f);
		hero.setSpeed(1000f);
		spritesheet = new Texture(Gdx.files.internal("villain.png"));
		villain = new NonplayableCharacter("Villain", spritesheet, 32, 32, 16, 16, (int)(camera.viewportWidth * 2 / 3), (int)(camera.viewportHeight / 2), 0.15f);
		
		// map setup
		Texture background = new Texture(Gdx.files.internal("ALTTP_bigmap.png"));
		Coordinate centerLeft = new Coordinate(background.getWidth() / 2 - camera.viewportWidth/6, background.getHeight() / 3);
		Coordinate centerRight = new Coordinate(background.getWidth() / 2 + camera.viewportWidth/6, background.getHeight() / 3);
		map = new Map(hero, centerLeft, background);
		map.addCharacterToMap(villain, centerRight);
	}
	
	@Override
	public void dispose()
	{
		batch.dispose();
		hero.sprite.getTexture().dispose();
		villain.sprite.getTexture().dispose();
		// TODO not sure what else we need to dispose of. Maybe all textures, texture regions?
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		// draw stuff here
		map.render(batch, (int)camera.viewportWidth, (int)camera.viewportHeight);
		// overlays would get drawn after the map
		
		batch.end();
		
		update();
	}

	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		map.update(deltaTime);
	}
	
	@Override
	public void resize(int width, int height)
	{
		
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
