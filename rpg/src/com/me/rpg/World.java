package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;

public class World implements Disposable
{

	private BitmapFont debugFont;
	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Map map;
	private PlayableCharacter player;
	private NonplayableCharacter npc1;
	private NonplayableCharacter npc2;

	public World(SpriteBatch batch, OrthographicCamera camera)
	{
		final String PLAYER_NAME = "Player";
		final String NPC1_NAME = "NPC1";
		final String NPC2_NAME = "NPC2";

		this.batch = batch;
		this.camera = camera;

		// create map
		TiledMap tiledMap = RPG.manager.get(RPG.MAP_TMX_PATH, TiledMap.class);
		map = new Map(batch, camera, tiledMap);

		// get spawn points and walking boundaries from .tmx
		MapLayer spawnLayer = tiledMap.getLayers().get("Spawn");
		MapObjects spawnPoints = spawnLayer.getObjects();
		MapLayer walkingBoundariesLayer = tiledMap.getLayers().get("WalkingBoundaries");
		MapObjects walkingBoundaries = walkingBoundariesLayer.getObjects();

		// create characters
		Texture spritesheet1 = RPG.manager.get(RPG.PLAYER_TEXTURE_PATH);
		player = new PlayableCharacter(PLAYER_NAME, spritesheet1, 32, 32, 16, 16,
				0.15f);
		player.setSpeed(200f);

		Texture spritesheet2 = RPG.manager.get(RPG.NPC_TEXTURE_PATH);
		RectangleMapObject boundary1 = (RectangleMapObject) walkingBoundaries.get(NPC1_NAME);
		npc1 = new NonplayableCharacter(NPC1_NAME, spritesheet2, 32, 32, 16, 16,
				0.15f, boundary1.getRectangle());

		RectangleMapObject boundary2 = (RectangleMapObject) walkingBoundaries.get(NPC2_NAME);
		npc2 = new NonplayableCharacter(NPC2_NAME, spritesheet2, 32, 32, 16, 16,
				0.15f, boundary2.getRectangle());

		// add characters to map
		RectangleMapObject playerSpawn = (RectangleMapObject) spawnPoints.get(PLAYER_NAME);
		map.addFocusedCharacterToMap(player, playerSpawn.getRectangle().x,
				playerSpawn.getRectangle().y);
		RectangleMapObject npc1Spawn = (RectangleMapObject) spawnPoints.get(NPC1_NAME);
		map.addCharacterToMap(npc1, npc1Spawn.getRectangle().x, npc1Spawn.getRectangle().y);
		RectangleMapObject npc2Spawn = (RectangleMapObject) spawnPoints.get(NPC2_NAME);
		map.addCharacterToMap(npc2, npc2Spawn.getRectangle().x, npc2Spawn.getRectangle().y);

		// create debug font
		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red
	}

	public void render()
	{
		map.render();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// render HUD and overlays
		float fpsX = camera.position.x - camera.viewportWidth / 2 + 15;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

		batch.end();
	}

	public void update(float deltaTime)
	{
		map.update(deltaTime);
	}

	@Override
	public void dispose()
	{
		map.dispose();
		debugFont.dispose();
	}

}
