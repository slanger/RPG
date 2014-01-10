package com.me.rpg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RPG implements ApplicationListener
{
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private float x, y;
	
	@Override
	public void create()
	{
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("megaman.png"));
		
		x = camera.viewportWidth / 2;
		y = camera.viewportHeight / 2;
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		texture.dispose();
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
		batch.draw(texture, x, y);
		
		batch.end();
		
		update();
	}

	public void update()
	{
		// update x
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			x -= 100 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			x += 100 * Gdx.graphics.getDeltaTime();
		if (x < 0)
			x = 0;
		if (x > camera.viewportWidth)
			x = camera.viewportWidth;
		
		// update y
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			y -= 100 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.UP))
			y += 100 * Gdx.graphics.getDeltaTime();
		if (y < 0)
			y = 0;
		if (y > camera.viewportHeight)
			y = camera.viewportHeight;
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
