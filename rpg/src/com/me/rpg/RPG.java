package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class RPG implements Screen
{

	public static final String MAP_TMX_PATH = "maps/example.tmx";
	public static final String PLAYER_TEXTURE_PATH = "hero.png";
	public static final String NPC_TEXTURE_PATH = "villain.png";

	public static AssetManager manager = new AssetManager();

	private OrthographicCamera camera;
	private SpriteBatch batch;

	private World world;

	public RPG()
	{
		loadAssets();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		batch = new SpriteBatch();

		world = new World(batch, camera);
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		manager.dispose();
		world.dispose();
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// update before render
		update();

		// render World
		world.render();
	}

	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		camera.update();
		world.update(deltaTime);
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

	private void loadAssets()
	{
		// load Tiled map
		manager.setLoader(TiledMap.class, new TmxMapLoader(
				new InternalFileHandleResolver()));
		manager.load(MAP_TMX_PATH, TiledMap.class);

		// load textures
		manager.load(PLAYER_TEXTURE_PATH, Texture.class);
		manager.load(NPC_TEXTURE_PATH, Texture.class);

		manager.update();
		manager.finishLoading();
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

}
