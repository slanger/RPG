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
	
	private SpriteBatch batch;
	
	@Override
	public void create()
	{
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch = new SpriteBatch();
		
		Texture spritesheet = new Texture(Gdx.files.internal("hero.png"));
		hero = new PlayableCharacter("Hero", spritesheet, 32, 32, 16, 16, (int)(camera.viewportWidth / 3), (int)(camera.viewportHeight / 2), 0.15f);
		spritesheet = new Texture(Gdx.files.internal("villain.png"));
		villain = new NonplayableCharacter("Villain", spritesheet, 32, 32, 16, 16, (int)(camera.viewportWidth * 2 / 3), (int)(camera.viewportHeight / 2), 0.15f);
	}
	
	@Override
	public void dispose()
	{
		batch.dispose();
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
		hero.render(batch);
		villain.render(batch);
		
		batch.end();
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
