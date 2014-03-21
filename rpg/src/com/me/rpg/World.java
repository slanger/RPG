package com.me.rpg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Disposable;
import com.me.rpg.ai.Dialogue;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.characters.NonplayableCharacter;
import com.me.rpg.characters.PlayableCharacter;
import com.me.rpg.combat.MeleeWeapon;
import com.me.rpg.combat.Poison;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.RangedWeapon;
import com.me.rpg.combat.Shield;
import com.me.rpg.combat.StatusEffect;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.MapType;
import com.me.rpg.maps.PrototypeMap;
import com.me.rpg.maps.WestTownInsideHouse;
import com.me.rpg.maps.WestTownMap;
import com.me.rpg.reputation.ReputationSystem;
import com.me.rpg.utils.Direction;
import com.me.rpg.utils.Timer;

public final class World implements Disposable
{

	public static final String WARP_SOUND_PATH = "music/ALTTP_warp_sound.mp3";
	public static final String FADED_RED_DOT_PATH = "faded_red_dot.png";

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private BitmapFont debugFont;
	private List<Map> maps;
	private Map currentMap;

	private List<GameCharacter> characters;
	private PlayableCharacter player;

	private Dialogue dialogue;
	private ReputationSystem reputationSystem;

	private boolean warping = false;
	private float warpingAlpha;
	private Sound warpSound;
	private Sprite whiteScreen;

	private Timer timer = new Timer();

	public boolean isGameOver = false;

	public Dialogue getDialogue()
	{
		return dialogue;
	}

	public ReputationSystem getReputationSystem()
	{
		return reputationSystem;
	}

	public Map getCurrentMap()
	{
		return currentMap;
	}

	private void setCurrentMap(Map currentMap)
	{
		this.currentMap = currentMap;
	}

	public World(SpriteBatch batch, ShapeRenderer shapeRenderer,
			OrthographicCamera camera)
	{
		this.batch = batch;
		this.shapeRenderer = shapeRenderer;
		this.camera = camera;

		dialogue = new Dialogue(batch, camera);
		reputationSystem = new ReputationSystem(this);

		// create debug font
		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red

		// warp resources
		warpSound = RPG.manager.get(WARP_SOUND_PATH, Sound.class);
		whiteScreen = new Sprite(RPG.manager.get(RPG.WHITE_DOT_PATH, Texture.class));

		// create maps
		maps = new ArrayList<Map>();
		Map exampleMap = new ExampleMap(this, batch, camera);
		maps.add(exampleMap);
		Map prototypeMap = new PrototypeMap(this, batch, camera);
		maps.add(prototypeMap);
		Map westTown = new WestTownMap(this, batch, camera);
		maps.add(westTown);
		Map westTownInsideHouse = new WestTownInsideHouse(this, batch, camera);
		maps.add(westTownInsideHouse);

		currentMap = maps.get(MapType.EXAMPLE.getMapIndex());

		// CHARACTER SETUP

		characters = new ArrayList<GameCharacter>();

		NonplayableCharacter npc1;
		NonplayableCharacter npc2;
		final String PLAYER_NAME = "Player";
		final String NPC1_NAME = "NPC1";
		final String NPC2_NAME = "NPC2";
		final int width = 28;
		final int height = 28;

		// get spawn points and walking boundaries from .tmx
		MapObjects spawnPoints = getSpawnPoints();
		MapObjects walkingBoundaries = getWalkingBoundaries();

		// create characters
		Texture spritesheet1 = RPG.manager.get(RPG.PLAYER_TEXTURE_PATH);
		player = new PlayableCharacter(PLAYER_NAME, spritesheet1, width,
				height, 16, 16, 0.15f, world);
		player.setSpeed(200f);

		Texture spritesheet2 = RPG.manager.get(RPG.NPC_TEXTURE_PATH);
		RectangleMapObject boundary1 = (RectangleMapObject) walkingBoundaries
				.get(NPC1_NAME);
		npc1 = new NonplayableCharacter(NPC1_NAME, spritesheet2, width, height,
				16, 16, 0.15f, world, boundary1.getRectangle());

		RectangleMapObject boundary2 = (RectangleMapObject) walkingBoundaries
				.get(NPC2_NAME);
		npc2 = new NonplayableCharacter(NPC2_NAME, spritesheet2, width, height,
				16, 16, 0.15f, world, boundary2.getRectangle());

		// add characters to map
		RectangleMapObject playerSpawn = (RectangleMapObject) spawnPoints
				.get(PLAYER_NAME);
		addFocusedCharacterToMap(player, playerSpawn.getRectangle().x,
				playerSpawn.getRectangle().y);
		RectangleMapObject npc1Spawn = (RectangleMapObject) spawnPoints
				.get(NPC1_NAME);
		addCharacterToMap(npc1, npc1Spawn.getRectangle().x,
				npc1Spawn.getRectangle().y);
		RectangleMapObject npc2Spawn = (RectangleMapObject) spawnPoints
				.get(NPC2_NAME);
		addCharacterToMap(npc2, npc2Spawn.getRectangle().x,
				npc2Spawn.getRectangle().y);

		// setup weapons
		genericWeaponSetup(player, npc1);
	}

	public void render()
	{
		currentMap.render();

		temporaryVisionConeTest();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// temporary dialogue stuff
		if (dialogue.getInDialogue())
		{
			dialogue.render();
		}
		// end dialogue stuff

		if (warping)
		{
			whiteScreen.setSize(camera.viewportWidth, camera.viewportHeight);
			whiteScreen.setPosition(camera.position.x - camera.viewportWidth
					/ 2, camera.position.y - camera.viewportHeight / 2);
			whiteScreen.draw(batch, warpingAlpha);
		}

		// render HUD and overlays
		float fpsX = camera.position.x - camera.viewportWidth / 2 + 15;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

		batch.end();
	}

	public void update(float deltaTime)
	{
		timer.update(deltaTime);
		Iterator<Map> iter = maps.iterator();
		while (iter.hasNext())
		{
			Map map = iter.next();
			map.update(deltaTime);
		}
	}

	public void moveToAnotherMap(MapType mapType)
	{
		currentMap.close();
		currentMap = maps.get(mapType.getMapIndex());
	}

	public void warpToAnotherMap(MapType mapType)
	{
		currentMap.close();
		warping = true;
		warpingAlpha = 0f;
		warpSound.play();
		timer.scheduleTask(new Timer.Task()
		{

			@Override
			public void run()
			{
				warpingAlpha += 0.1f;
				if (warpingAlpha > 1f)
				{
					warpingAlpha = 1f;
					this.cancel();
				}
			}

		}, 0f, 0.1f);
		Map newMap = maps.get(mapType.getMapIndex());
		timer.scheduleTask(new WarpToAnotherMapTask(newMap), 3.0f);
	}

	private class WarpToAnotherMapTask extends Timer.Task
	{

		private Map newMap;

		WarpToAnotherMapTask(Map newMap)
		{
			this.newMap = newMap;
		}

		@Override
		public void run()
		{
			setCurrentMap(newMap);

			timer.scheduleTask(new Timer.Task()
			{

				@Override
				public void run()
				{
					warpingAlpha -= 0.1f;
				}

			}, 0f, 0.1f, 10);

			timer.scheduleTask(new Timer.Task()
			{

				@Override
				public void run()
				{
					warping = false;
					newMap.open();
				}

			}, 1.0f);
		}

	}

	@Override
	public void dispose()
	{
		debugFont.dispose();
		Iterator<Map> iter = maps.iterator();
		while (iter.hasNext())
		{
			Map map = iter.next();
			map.dispose();
		}
	}

	private void genericWeaponSetup(GameCharacter character, GameCharacter npc)
	{
		int width = 32;
		int height = 32;

		// melee attack test stuff
		Texture swordSprite = RPG.manager.get(RPG.SWORD_PATH);
		Weapon sword = new MeleeWeapon("LameSword");
		Weapon sword2 = new MeleeWeapon("Sword2");
		sword2.initSprite(swordSprite, width, height, 32, 32);
		sword.initSprite(swordSprite, width, height, 32, 32);
		StatusEffect poison = new Poison(50, 3, 2f);
		sword.addEffect(poison);
		sword2.addEffect(poison);

		character.equip(this, sword);
		character.swapWeapon(this);
		npc.equip(this, sword2);

		// ranged attack test stuff
		Texture bowSprite = RPG.manager.get(RPG.ARROW_PATH);
		RangedWeapon bow = new RangedWeapon("LameBow");
		bow.initSprite(bowSprite, width, height, 32, 32);

		character.equip(this, bow);
		Projectile arrow = new Projectile("arrow", bowSprite, width, height,
				32, 32);
		bow.equipProjectile(arrow, 1000);

		Shield shield = new Shield("plain shield");
		npc.equipShield(shield);
	}

	public void temporaryVisionConeTest()
	{
		float tempX = 0.0f;
		float tempY = 0.0f;
		float tempSightDistance = 0.0f;
		Direction tempDirection = null;
		float visionFieldPoints[] = new float[8];

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);

		ArrayList<GameCharacter> charactersOnMap = currentMap.getCharactersOnMap();
		Iterator<GameCharacter> iterator1 = charactersOnMap.iterator();
		while (iterator1.hasNext())
		{
			GameCharacter tempCharacter = iterator1.next();
			if (tempCharacter.getName() != "Player")
			{
				tempSightDistance = tempCharacter.getSightDistance();
				tempDirection = tempCharacter.getFaceDirection();
				tempX = tempCharacter.getCenterX();
				tempY = tempCharacter.getCenterY();
				if (tempDirection.name().equalsIgnoreCase("up"))
				{
					visionFieldPoints[0] = tempX; // x value of point centered
													// on NPC
					visionFieldPoints[1] = tempY; // y value of point centered
													// on NPC
					visionFieldPoints[2] = tempX - 0.8f * (tempSightDistance);
					visionFieldPoints[3] = tempY + 0.8f * (tempSightDistance);
					visionFieldPoints[4] = tempX;
					visionFieldPoints[5] = tempY + tempSightDistance;
					visionFieldPoints[6] = tempX + 0.8f * (tempSightDistance);
					visionFieldPoints[7] = tempY + 0.8f * (tempSightDistance);
				}
				else if (tempDirection.name().equalsIgnoreCase("down"))
				{
					visionFieldPoints[0] = tempX; // x value of point centered
													// on NPC
					visionFieldPoints[1] = tempY; // y value of point centered
													// on NPC
					visionFieldPoints[2] = tempX - 0.8f * (tempSightDistance);
					visionFieldPoints[3] = tempY - 0.8f * (tempSightDistance);
					visionFieldPoints[4] = tempX;
					visionFieldPoints[5] = tempY - tempSightDistance;
					visionFieldPoints[6] = tempX + 0.8f * (tempSightDistance);
					visionFieldPoints[7] = tempY - 0.8f * (tempSightDistance);
				}
				else if (tempDirection.name().equalsIgnoreCase("left"))
				{
					visionFieldPoints[0] = tempX; // x value of point centered
													// on NPC
					visionFieldPoints[1] = tempY; // y value of point centered
													// on NPC
					visionFieldPoints[2] = tempX - 0.8f * (tempSightDistance);
					visionFieldPoints[3] = tempY - 0.8f * (tempSightDistance);
					visionFieldPoints[4] = tempX - tempSightDistance;
					visionFieldPoints[5] = tempY;
					visionFieldPoints[6] = tempX - 0.8f * (tempSightDistance);
					visionFieldPoints[7] = tempY + 0.8f * (tempSightDistance);
				}
				else
				// facing right
				{
					visionFieldPoints[0] = tempX; // x value of point centered
													// on NPC
					visionFieldPoints[1] = tempY; // y value of point centered
													// on NPC
					visionFieldPoints[2] = tempX + 0.8f * (tempSightDistance);
					visionFieldPoints[3] = tempY - 0.8f * (tempSightDistance);
					visionFieldPoints[4] = tempX + tempSightDistance;
					visionFieldPoints[5] = tempY;
					visionFieldPoints[6] = tempX + 0.8f * (tempSightDistance);
					visionFieldPoints[7] = tempY + 0.8f * (tempSightDistance);
				}

				shapeRenderer.setColor(0, 1, 0, 0.025f);
				shapeRenderer.polygon(visionFieldPoints);
			}
		}
		shapeRenderer.end();
	}

}
