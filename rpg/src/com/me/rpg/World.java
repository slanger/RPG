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
import com.me.rpg.ai.DialogueSystem;
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
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.utils.Timer;

public final class World implements Disposable
{

	private static World instance = null;

	public static final String WARP_SOUND_PATH = "music/ALTTP_warp_sound.mp3";
	public static final String FADED_RED_DOT_PATH = "faded_red_dot.png";
	public static final String PLAYER_TEXTURE_PATH = "hero.png";
	public static final String NPC_TEXTURE_PATH = "villain.png";
	public static final String SWORD_PATH = "sword.png";
	public static final String ARROW_PATH = "arrow.png";

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private BitmapFont debugFont;
	private List<Map> maps;
	private Map currentMap;

	private PlayableCharacter player;

	private Timer timer = new Timer();

	private DialogueSystem dialogueSystem;
	private ReputationSystem reputationSystem;

	private boolean warping = false;
	private float warpingAlpha;
	private Sound warpSound;
	private Sprite whiteScreen;

	private boolean isGameOver = false;
	private boolean movingToAnotherMap = false;
	private boolean updateEnable = true;

	public static World getInstance()
	{
		if (instance == null)
		{
			System.out.println("*** initialize world ***");
			instance = new World();
			instance.initializeWorld();
		}
		return instance;
	}

	public static void clearInstance()
	{
		if (instance == null)
		{
			return;
		}
		instance.dispose();
		instance = null;
	}

	public DialogueSystem getDialogueSystem()
	{
		return dialogueSystem;
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

	public boolean isGameOver()
	{
		return isGameOver;
	}

	public void setGameOver(boolean isGameOver)
	{
		this.isGameOver = isGameOver;
	}

	public void setUpdateEnable(boolean updateEnable)
	{
		this.updateEnable = updateEnable;
	}

	/*
	 *  You CANNOT call World.getInstance() in this constructor--it will cause an
	 *  infinite loop. Things that call World.getInstance() need to be placed in
	 *  initializeWorld()
	 */
	private World()
	{
		batch = RPG.batch;
		camera = RPG.camera;

		shapeRenderer = new ShapeRenderer();

		dialogueSystem = new DialogueSystem();

		// create debug font
		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red

		// warp resources
		warpSound = RPG.manager.get(WARP_SOUND_PATH, Sound.class);
		whiteScreen = new Sprite(RPG.manager.get(RPG.WHITE_DOT_PATH, Texture.class));
	}

	private void initializeWorld()
	{
		// create reputation system
		reputationSystem = new ReputationSystem();

		// create maps
		maps = new ArrayList<Map>();
		Map exampleMap = new ExampleMap();
		maps.add(exampleMap);
		Map prototypeMap = new PrototypeMap();
		maps.add(prototypeMap);
		Map westTown = new WestTownMap();
		maps.add(westTown);
		Map westTownInsideHouse = new WestTownInsideHouse();
		maps.add(westTownInsideHouse);

		currentMap = maps.get(MapType.EXAMPLE.getMapIndex());

		// CHARACTER SETUP

		NonplayableCharacter npc1;
		NonplayableCharacter npc2;
		final String PLAYER_NAME = "Player";
		final String NPC1_NAME = "NPC1";
		final String NPC2_NAME = "NPC2";
		final int width = 28;
		final int height = 28;

		// create characters
		Texture spritesheet1 = RPG.manager.get(PLAYER_TEXTURE_PATH);
		player = new PlayableCharacter(PLAYER_NAME, spritesheet1, width,
				height, 16, 16, 0.15f);
		player.setSpeed(200f);

		MapObjects exampleWalkingBoundaries = exampleMap.getWalkingBoundaries();

		Texture spritesheet2 = RPG.manager.get(NPC_TEXTURE_PATH);
		RectangleMapObject boundary1 = (RectangleMapObject) exampleWalkingBoundaries
				.get(NPC1_NAME);
		npc1 = new NonplayableCharacter(NPC1_NAME, spritesheet2, width, height,
				16, 16, 0.15f, boundary1.getRectangle());

		RectangleMapObject boundary2 = (RectangleMapObject) exampleWalkingBoundaries
				.get(NPC2_NAME);
		npc2 = new NonplayableCharacter(NPC2_NAME, spritesheet2, width, height,
				16, 16, 0.15f, boundary2.getRectangle());

		// add characters to map
		exampleMap.addFocusedCharacterToMap(player, 192, 544);
		exampleMap.addCharacterToMap(npc1, 544, 544);
		exampleMap.addCharacterToMap(npc2, 480, 128);

		// setup weapons
		genericWeaponSetup(player, npc1, exampleMap);
	}

	public void render()
	{
		if (movingToAnotherMap)
		{
			return;
		}

		currentMap.render();

		temporaryVisionConeTest();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// temporary dialogue stuff
		if (dialogueSystem.getInDialogue())
		{
			dialogueSystem.render(batch, camera);
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

		if (!updateEnable)
		{
			player.handleDialogueInput();
			return;
		}

		Iterator<Map> iter = maps.iterator();
		while (iter.hasNext())
		{
			Map map = iter.next();
			map.update(deltaTime);
		}
	}

	public void movePlayerToAnotherMap(final MapType mapType, final Coordinate newLocation)
	{
		currentMap.close();
		updateEnable = false;
		movingToAnotherMap = true;

		// cannot remove player from Map until the Map has stopped updating
		// Exception in thread "LWJGL Application" java.util.ConcurrentModificationException
		timer.scheduleTask(new Timer.Task()
		{

			@Override
			public void run()
			{
				currentMap.removeCharacterFromMap(player);
				currentMap = maps.get(mapType.getMapIndex());
				currentMap.addFocusedCharacterToMap(player, newLocation);
				updateEnable = true;
				movingToAnotherMap = false;
				currentMap.open();
			}

		}, 1);
	}

	public void warpPlayerToAnotherMap(MapType mapType, Coordinate newLocation)
	{
		currentMap.close();
		updateEnable = false;
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
		timer.scheduleTask(new WarpToAnotherMapTask(newMap, newLocation), 3.0f);
	}

	private class WarpToAnotherMapTask extends Timer.Task
	{

		private Map newMap;
		private Coordinate newLocation;

		WarpToAnotherMapTask(Map newMap, Coordinate newLocation)
		{
			this.newMap = newMap;
			this.newLocation = newLocation;
		}

		@Override
		public void run()
		{
			currentMap.removeCharacterFromMap(player);
			setCurrentMap(newMap);
			newMap.addFocusedCharacterToMap(player, newLocation);

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
					updateEnable = true;
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

	private void genericWeaponSetup(GameCharacter character, GameCharacter npc, Map map)
	{
		int width = 32;
		int height = 32;

		// melee attack test stuff
		Texture swordSprite = RPG.manager.get(SWORD_PATH);
		Weapon sword = new MeleeWeapon("LameSword");
		Weapon sword2 = new MeleeWeapon("Sword2");
		sword2.initSprite(swordSprite, width, height, 32, 32);
		sword.initSprite(swordSprite, width, height, 32, 32);
		StatusEffect poison = new Poison(50, 3, 2f);
		sword.addEffect(poison);
		sword2.addEffect(poison);

		character.equip(map, sword);
		character.swapWeapon(map);
		npc.equip(map, sword2);

		// ranged attack test stuff
		Texture bowSprite = RPG.manager.get(ARROW_PATH);
		RangedWeapon bow = new RangedWeapon("LameBow");
		bow.initSprite(bowSprite, width, height, 32, 32);

		character.equip(map, bow);
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
