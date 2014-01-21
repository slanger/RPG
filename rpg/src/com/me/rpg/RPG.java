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
		Coordinate centerLeft = new Coordinate(background.getWidth() / 3, background.getHeight() / 2);
		Coordinate centerRight = new Coordinate(background.getWidth() * 2 / 3, background.getHeight() / 2);
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
		Gdx.gl.glClearColor(0, 0, 0, 1);
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
<<<<<<< HEAD
		float oldX = hero.sprite.getX();
		float oldY = hero.sprite.getY();
		
		hero.update();
		villain.update();
		
		float x = hero.sprite.getX();
		float y = hero.sprite.getY();
		
		selectDelta += deltaTime;
		
		// enable/disable camera scrolling
		if (Gdx.input.isKeyPressed(Keys.C) && selectDelta > 0.5f)
		{
			cameraScrollEnable = !cameraScrollEnable;
			selectDelta = 0.0f;
		}
		
		// move camera
		if (cameraScrollEnable) {
			cameraX += (x - oldX);
			cameraY -= (y - oldY);
			if (cameraX < 0)
				cameraX = 0;
			else if (cameraX > background.getWidth() - camera.viewportWidth) {
				cameraX = background.getWidth() - camera.viewportWidth;
			}
			if (cameraY < 0) {
				cameraY = 0;
			} else if (cameraY > background.getHeight() - camera.viewportHeight) {
				cameraY = background.getHeight() - camera.viewportHeight;
			}
			
			hero.sprite.setPosition(oldX, oldY);
		}
=======
		map.update(deltaTime);
>>>>>>> 02eef7d1dab2d86ea8e8d652cfa573fc2f820bc3
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
