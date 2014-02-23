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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.PrototypeMap;
import com.me.rpg.maps.WestTownInsideHouse;
import com.me.rpg.maps.WestTownMap;
import com.me.rpg.utils.LoadBar;

public class RPG implements Screen
{

	public static final String WHITE_DOT_PATH = "white_dot.png";
	public static final String FONT_PATH = "font/Microsoft_Uighur_white.fnt";

	public static final AssetManager manager = new AssetManager();
	public static final OrthographicCamera camera = new OrthographicCamera();
	public static final SpriteBatch batch = new SpriteBatch();

	public final ScreenHandler screenHandler;

	private LoadBar loadBar;
	private BitmapFont font;

	public RPG(ScreenHandler screenHandler)
	{
		loadLoadBarAssets();
		loadAssets();

		this.screenHandler = screenHandler;

		camera.setToOrtho(false, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		float offset = 10;
		float width = camera.viewportWidth - 2 * offset;
		float height = camera.viewportHeight / 8;
		float x = offset;
		float y = camera.viewportHeight / 2 - height / 2;
		loadBar = new LoadBar(x, y, width, height, manager.get(WHITE_DOT_PATH, Texture.class));

		font = manager.get(FONT_PATH, BitmapFont.class);
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		manager.dispose();
		World.clearInstance();
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// if AssetManager is still loading assets, draw load bar
		if (!manager.update())
		{
			loadBar.update(manager.getProgress());
			batch.begin();
			loadBar.render(batch);
			font.draw(batch, "Loading", loadBar.x, loadBar.y);
			batch.end();
			return;
		}

		// update before render
		update();

		// render World
		World.getInstance().render();
	}

	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		camera.update();
		World world = World.getInstance();
		world.update(deltaTime);
		if (world.isGameOver())
		{
			screenHandler.moveToOtherScreen(screenHandler.endScreen);
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

	private void loadLoadBarAssets()
	{
		manager.load(WHITE_DOT_PATH, Texture.class);
		manager.load(FONT_PATH, BitmapFont.class);
		manager.update();
		manager.finishLoading();
	}

	private void loadAssets()
	{
		// load Tiled maps
		manager.setLoader(TiledMap.class, new TmxMapLoader(
				new InternalFileHandleResolver()));
		manager.load(ExampleMap.MAP_TMX_PATH, TiledMap.class);
		manager.load(PrototypeMap.MAP_TMX_PATH, TiledMap.class);
		manager.load(WestTownMap.MAP_TMX_PATH, TiledMap.class);
		manager.load(WestTownInsideHouse.MAP_TMX_PATH, TiledMap.class);

		// load textures
		manager.load(World.PLAYER_TEXTURE_PATH, Texture.class);
		manager.load(World.NPC_TEXTURE_PATH, Texture.class);
		manager.load(World.SWORD_PATH, Texture.class);
		manager.load(World.ARROW_PATH, Texture.class);
		manager.load(Map.GRAVESTONE_PATH, Texture.class);
		manager.load(World.FADED_RED_DOT_PATH, Texture.class);
		manager.load(World.SHIELD_PATH, Texture.class);

		// load sounds and music
		manager.load(World.WARP_SOUND_PATH, Sound.class);
		manager.load(PrototypeMap.BACKGROUND_MUSIC_START, Music.class);
		manager.load(PrototypeMap.BACKGROUND_MUSIC_LOOP, Music.class);
	}

	@Override
	public void show()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void hide()
	{
		World.clearInstance();
	}

}
