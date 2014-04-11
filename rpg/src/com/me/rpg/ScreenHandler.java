package com.me.rpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.PrototypeMap;
import com.me.rpg.maps.WestTownInsideHouse;
import com.me.rpg.maps.WestTownMap;

public class ScreenHandler extends Game
{

	public static final AssetManager manager = new AssetManager();

	@Override
	public void create()
	{
		loadLoadBarAssets();
		loadAssets();

		setScreen(new LoadScreen(this));
	}

	@Override
	public void resize(int width, int height)
	{
		getScreen().resize(width, height);
	}

	private void loadLoadBarAssets()
	{
		manager.load(LoadScreen.WHITE_DOT_PATH, Texture.class);
		manager.load(LoadScreen.FONT_PATH, BitmapFont.class);
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
		manager.load(World.RGUARD_TEXTURE_PATH, Texture.class);

		manager.load(Map.GRAVESTONE_PATH, Texture.class);
		manager.load(World.FADED_RED_DOT_PATH, Texture.class);

		// load sword textures
		manager.load(World.SWORD_PATH, Texture.class);
		manager.load(World.EVIL_SWORD_PATH, Texture.class);
		manager.load(World.HOLY_SWORD_PATH, Texture.class);
		manager.load(World.BLUE_SWORD_PATH, Texture.class);
		manager.load(World.SHADOW_SWORD_PATH, Texture.class);
		
		//load arrow textures
		manager.load(World.ARROW_PATH, Texture.class);
		manager.load(World.FIRE_ARROW_PATH, Texture.class);
		//load shield textures
		manager.load(World.SHIELD_PATH, Texture.class);
		manager.load(World.GRAY_SHIELD_PATH, Texture.class);
		manager.load(World.SHADOW_SHIELD_PATH, Texture.class);
		//load misc item textures
		
		
		// load sounds and music
		manager.load(World.WARP_SOUND_PATH, Sound.class);
		manager.load(PrototypeMap.BACKGROUND_MUSIC_START, Music.class);
		manager.load(PrototypeMap.BACKGROUND_MUSIC_LOOP, Music.class);
	}

}
