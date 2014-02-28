package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.PrototypeMap;

public class RPG implements Screen
{

	public static final String PLAYER_TEXTURE_PATH = "hero.png";
	public static final String NPC_TEXTURE_PATH = "villain.png";
	public static final String SWORD_PATH = "sword.png";
	public static final String ARROW_PATH = "arrow.png";
	public static final String GRAVESTONE_PATH = "gravestones.png";

	public static TextureRegion gravestone1;
	public static TextureRegion gravestone2;

	public static AssetManager manager = new AssetManager();
	public ScreenHandler screenHandler;

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;

	private World world;

	public RPG(ScreenHandler screenHandler)
	{
		loadAssets();

		this.screenHandler = screenHandler;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		// camera.zoom -= 0.1f;

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		world = new World(batch, shapeRenderer, camera);

		Texture graves = RPG.manager.get(GRAVESTONE_PATH);
		gravestone1 = new TextureRegion(graves, 0, 0, 34, 41);
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		shapeRenderer.dispose();
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
		if (world.isGameOver())
		{
			screenHandler.setScreen(screenHandler.endScreen);
		}
	}

	@Override
	public void resize(int width, int height)
	{
		// TODO calculate new viewport
		// float aspectRatio = (float)width/(float)height;
		// float scale = 1f;
		// Vector2 crop = new Vector2(0f, 0f);
		//
		// if(aspectRatio > ASPECT_RATIO) {
		// scale = (float) height / (float) VIRTUAL_HEIGHT;
		// crop.x = (width - VIRTUAL_WIDTH * scale) / 2f;
		// } else if(aspectRatio < ASPECT_RATIO) {
		// scale = (float) width / (float) VIRTUAL_WIDTH;
		// crop.y = (height - VIRTUAL_HEIGHT * scale) / 2f;
		// } else {
		// scale = (float) width / (float) VIRTUAL_WIDTH;
		// }
		//
		// float w = (float) VIRTUAL_WIDTH * scale;
		// float h = (float) VIRTUAL_HEIGHT * scale;
		// viewport = new Rectangle(crop.x, crop.y, w, h);
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
		// load Tiled maps
		manager.setLoader(TiledMap.class, new TmxMapLoader(
				new InternalFileHandleResolver()));
		manager.load(ExampleMap.MAP_TMX_PATH, TiledMap.class);
		manager.load(PrototypeMap.MAP_TMX_PATH, TiledMap.class);

		// load textures
		manager.load(PLAYER_TEXTURE_PATH, Texture.class);
		manager.load(NPC_TEXTURE_PATH, Texture.class);
		manager.load(SWORD_PATH, Texture.class);
		manager.load(ARROW_PATH, Texture.class);
		manager.load(GRAVESTONE_PATH, Texture.class);
		manager.load(World.WHITE_DOT_PATH, Texture.class);
		manager.load(World.FADED_RED_DOT_PATH, Texture.class);

		// load sounds and music
		manager.load(World.WARP_SOUND_PATH, Sound.class);
		manager.load(PrototypeMap.BACKGROUND_MUSIC_START, Music.class);
		manager.load(PrototypeMap.BACKGROUND_MUSIC_LOOP, Music.class);

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
