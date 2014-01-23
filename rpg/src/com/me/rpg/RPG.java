package com.me.rpg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class RPG implements ApplicationListener
{
	
	private final String mapTmxPath = "maps/example.tmx";
	private final String playerTexturePath = "hero.png";
	private final String npcTexturePath = "villain.png";
	
	private final int MAP_WIDTH = 640;
	private final int MAP_HEIGHT = 640;
	
	public static AssetManager manager = new AssetManager();
	public static OrthographicCamera camera;
	
	private SpriteBatch batch;
	
	private Map map;
	private Character player, npc;
	
	@Override
	public void create()
	{
		load();
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch = new SpriteBatch();
		
		Texture spritesheet = manager.get(playerTexturePath);
		player = new PlayableCharacter("Player", spritesheet, 32, 32, 16, 16, (int)(camera.viewportWidth / 3), (int)(camera.viewportHeight / 2), 0.15f);
		player.setSpeed(1000f);
		spritesheet = manager.get(npcTexturePath);
		npc = new NonplayableCharacter("NPC", spritesheet, 32, 32, 16, 16, (int)(camera.viewportWidth * 2 / 3), (int)(camera.viewportHeight / 2), 0.15f);
		
		// map setup
		Texture background = new Texture(Gdx.files.internal("ALTTP_bigmap.png"));
		//Coordinate centerLeft = new Coordinate(background.getWidth() / 3, background.getHeight() / 2);
		//Coordinate centerRight = new Coordinate(background.getWidth() * 2 / 3, background.getHeight() / 2);
		Coordinate centerLeft = new Coordinate(camera.viewportWidth / 3, camera.viewportHeight / 2);
		Coordinate centerRight = new Coordinate(camera.viewportWidth * 2 / 3, camera.viewportHeight / 2);
		map = new Map(player, centerLeft, background, MAP_WIDTH, MAP_HEIGHT, mapTmxPath, batch);
		map.addCharacterToMap(npc, centerRight);
	}
	
	@Override
	public void dispose()
	{
		batch.dispose();
		player.sprite.getTexture().dispose();
		npc.sprite.getTexture().dispose();
		// TODO not sure what else we need to dispose of. Maybe all textures, texture regions?
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// update before render
		camera.update();
		update();
		
		//batch.setProjectionMatrix(camera.combined);
		//batch.begin();
		
		// draw stuff here
		map.render(batch, (int)camera.viewportWidth, (int)camera.viewportHeight);
		// overlays would get drawn after the map
		
		//batch.end();
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
	
	private void load()
	{
		// load Tiled map
		manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		manager.load(mapTmxPath, TiledMap.class);
		
		// load textures
		manager.load(playerTexturePath, Texture.class);
		manager.load(npcTexturePath, Texture.class);
		
		manager.update();
		manager.finishLoading();
	}
	
}
