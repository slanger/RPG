package com.me.rpg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
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
import com.me.rpg.inventory.InventoryMenu;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.PrototypeMap;
import com.me.rpg.maps.Waypoint;
import com.me.rpg.maps.WestTownInsideHouse;
import com.me.rpg.maps.WestTownMap;
import com.me.rpg.reputation.ReputationSystem;
import com.me.rpg.state.CustomState;
import com.me.rpg.state.HierarchicalState;
import com.me.rpg.state.MeleeFightState;
import com.me.rpg.state.PatrolState;
import com.me.rpg.state.RandomWalkState;
import com.me.rpg.state.RunAwayState;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.state.transition.AndCondition;
import com.me.rpg.state.transition.Condition;
import com.me.rpg.state.transition.DistanceCondition;
import com.me.rpg.state.transition.HearPeopleCondition;
import com.me.rpg.state.transition.NotCondition;
import com.me.rpg.state.transition.OrCondition;
import com.me.rpg.state.transition.SeePeopleCondition;
import com.me.rpg.state.transition.Transition;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.utils.Timer;

public final class World
	implements Disposable, Serializable
{

	private static final long serialVersionUID = -3230953287610036756L;

	public static final String WARP_SOUND_PATH = "music/ALTTP_warp_sound.mp3";
	public static final String FADED_RED_DOT_PATH = "faded_red_dot.png";
	public static final String PLAYER_TEXTURE_PATH = "hero.png";
	public static final String NPC_TEXTURE_PATH = "villain.png";
	

	//swords
	public static final String SWORD_PATH = "sword.png";
	public static final String EVIL_SWORD_PATH = "evil_sword.png";
	public static final String HOLY_SWORD_PATH = "holy_sword.png";
	public static final String BLUE_SWORD_PATH = "blue_sword.png";
	public static final String SHADOW_SWORD_PATH = "shadow_sword.png";

	//bows
	public static final String ARROW_PATH = "arrow.png";

	//shields
	public static final String SHIELD_PATH = "shield.png";
	
	//other items
	
	
	public transient OrthographicCamera camera;
	public transient SpriteBatch batch;
	private transient ShapeRenderer shapeRenderer;
	private transient BitmapFont debugFont;
	private final List<Map> maps = new ArrayList<Map>();
	private final Timer timer = new Timer();

	private PlayableCharacter player;
	public List<Waypoint> waypoints;

	private int dayCount = 0;
	private final int NUM_SECONDS_PER_DAY = 60;
	private final int NUM_DAYS = 10;
	private Timer dayTimer = new Timer();

	private final DialogueSystem dialogueSystem = new DialogueSystem();
	private final InventoryMenu	inventoryMenu	= new InventoryMenu();
	private ReputationSystem reputationSystem;

	private boolean warping = false;
	private float warpingAlpha;
	public transient Sound warpSound;
	private transient Sprite whiteScreen;

	private boolean isGameOver = false;
	private boolean resetGame = false;
	private boolean saveGame = false;
	private boolean movingToAnotherMap = false;
	private boolean updateEnable = true;

	private boolean renderMessage = false;
	private String message = null;
	private long startMessageDisplayTime = 0;
	private final long MESSAGE_DISPLAY_SECONDS = 5;

	private transient BitmapFont messageFont;
	private transient Texture messageTexture;

	public DialogueSystem getDialogueSystem()
	{
		return dialogueSystem;
	}

	public ReputationSystem getReputationSystem()
	{
		return reputationSystem;
	}

	public InventoryMenu getInventoryMenu()
	{
		return inventoryMenu;
	}
	
	public Map getCurrentMap()
	{
		return player.getCurrentMap();
	}

	public boolean isGameOver()
	{
		return isGameOver;
	}

	public void setGameOver(boolean isGameOver)
	{
		this.isGameOver = isGameOver;
	}

	public boolean resetGame()
	{
		return resetGame;
	}

	public void setResetGame(boolean resetGame)
	{
		this.resetGame = resetGame;
	}

	public boolean saveGame()
	{
		return saveGame;
	}

	public void setSaveGame(boolean saveGame)
	{
		this.saveGame = saveGame;
	}

	public boolean updateEnable()
	{
		return updateEnable;
	}

	public void setUpdateEnable(boolean updateEnable)
	{
		this.updateEnable = updateEnable;
	}

	public static World getNewInstance()
	{
		World world = new World();
		world.initializeWorld();
		return world;
	}

	private World()
	{
		create();
	}

	private void create()
	{
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red

		messageFont = new BitmapFont();
		messageTexture = new Texture(
				Gdx.files.internal("images/DialogueBackground.png"));

		// warp resources
		warpSound = ScreenHandler.manager.get(WARP_SOUND_PATH, Sound.class);
		Texture whiteScreenTexture = ScreenHandler.manager.get(
				LoadScreen.WHITE_DOT_PATH, Texture.class);
		whiteScreen = new Sprite(whiteScreenTexture);
	}

	private void readObject(ObjectInputStream inputStream)
		throws IOException, ClassNotFoundException
	{
		create();
		inputStream.defaultReadObject();
	}

	private void initializeWorld()
	{
		// create day timer
		dayTimer.scheduleTask(new Timer.Task()
		{

			private static final long serialVersionUID = 121629940674023363L;

			@Override
			public void run()
			{
				dayCount++;
				if (dayCount >= NUM_DAYS)
				{
					setGameOver(true);
				}
			}

		}, NUM_SECONDS_PER_DAY, NUM_SECONDS_PER_DAY);

		// create reputation system
		reputationSystem = new ReputationSystem(this);

		// MAPS SETUP

		Map exampleMap = new ExampleMap(this);
		maps.add(exampleMap);
		Map prototypeMap = new PrototypeMap(this);
		maps.add(prototypeMap);
		Map westTown = new WestTownMap(this);
		maps.add(westTown);
		Map westTownInsideHouse = new WestTownInsideHouse(this);
		maps.add(westTownInsideHouse);

		// WAYPOINTS SETUP

		waypoints = new ArrayList<Waypoint>();
		for (Map map : maps)
		{
			waypoints.addAll(map.getWaypoints());
		}

		final int length = waypoints.size();
		for (int i = 0; i < length; i++)
		{
			Waypoint waypoint = waypoints.get(i);
			if (!waypoint.isWarpPoint())
			{
				continue;
			}
			for (int j = i + 1; j < length; j++)
			{
				Waypoint w = waypoints.get(j);
				if (w.isWarpPoint()
						&& w.name.equals(waypoint.connectedWarpPointName))
				{
					waypoint.connectedWarpPoint = w;
					w.connectedWarpPoint = waypoint;
					waypoint.connections.add(new Waypoint.Edge(w, 0));
					w.connections.add(new Waypoint.Edge(waypoint, 0));
				}
			}
		}

		// CHARACTERS SETUP

		NonplayableCharacter npc1;
		NonplayableCharacter npc2;
		NonplayableCharacter npc3;
		final int width = 28;
		final int height = 28;

		// create characters
		player = new PlayableCharacter("Player", PLAYER_TEXTURE_PATH, width,
				height, 16, 16, 0.15f, this);
		player.setBaseSpeed(200f);

		npc1 = new NonplayableCharacter("NPC1", NPC_TEXTURE_PATH, width,
				height, 16, 16, 0.15f, this);
		npc2 = new NonplayableCharacter("NPC2", NPC_TEXTURE_PATH, width,
				height, 16, 16, 0.15f, this);
		npc3 = new NonplayableCharacter("NPC3", NPC_TEXTURE_PATH, width,
				height, 16, 16, 0.15f, this);

		// add characters to map
		exampleMap.addFocusedCharacterToMap(player, 192, 544);
		exampleMap.addCharacterToMap(npc1, 544, 544);
		exampleMap.addCharacterToMap(npc2, 480, 128);
		exampleMap.addCharacterToMap(npc3, 32, 32);

		// state machine for npc1
		HierarchicalState parent = new HierarchicalState(null, npc1);

		Coordinate[] patrol = new Coordinate[] { new Coordinate(50, 50),
				new Coordinate(300, 50), new Coordinate(300, 300),
				new Coordinate(50, 300) };
		PatrolState patrol0 = new PatrolState(parent, npc1, patrol);

		MeleeFightState fight0 = new MeleeFightState(parent, npc1);

		SeePeopleCondition canSee = new SeePeopleCondition(npc1, 0,
				Comparison.NOTEQUALS);
		HearPeopleCondition canHear = new HearPeopleCondition(npc1, 0,
				Comparison.NOTEQUALS);
		Condition cannotSee = new NotCondition(canSee);
		Condition cannotHear = new NotCondition(canHear);
		Condition canSeeOrHear = new OrCondition(canSee, canHear);
		Condition cannotSeeNorHear = new AndCondition(cannotSee, cannotHear);

		Transition patrolToFight = new Transition(fight0, canSeeOrHear);
		Transition fightToPatrol = new Transition(patrol0, cannotSeeNorHear);

		patrol0.setTransitions(patrolToFight);
		fight0.setTransitions(fightToPatrol);
		parent.setInitialState(patrol0);

		npc1.setStateMachine(parent);

		// state machine for npc2
		parent = new HierarchicalState(null, npc2);
		HierarchicalState subparent = new HierarchicalState(parent, npc2);
		subparent.setName("subParent");
		CustomState notAtCen = new CustomState(subparent, npc2);
		notAtCen.setName("notAtCen");
		Rectangle walkingBoundary = new Rectangle(32, 32, 576, 224);
		RandomWalkState randomWalkState = new RandomWalkState(subparent, npc2,
				walkingBoundary);
		randomWalkState.setName("randomWalk");
		RunAwayState runaway = new RunAwayState(parent, npc2);
		runaway.setName("runaway");

		subparent.setInitialState(notAtCen);
		parent.setInitialState(subparent);

		WalkAction walkAction0 = new WalkAction(npc2, new Coordinate(500, 100), exampleMap);
		notAtCen.setActions(walkAction0);

		Condition dist = new DistanceCondition(npc2, exampleMap,
				new Coordinate(500, 100));
		Transition notCenToRandom = new Transition(randomWalkState, dist);

		canSee = new SeePeopleCondition(npc2, 0, Comparison.NOTEQUALS);
		canHear = new HearPeopleCondition(npc2, 0, Comparison.NOTEQUALS);
		cannotSee = new NotCondition(canSee);
		cannotHear = new NotCondition(canHear);
		canSeeOrHear = new OrCondition(canSee, canHear);
		cannotSeeNorHear = new AndCondition(cannotSee, cannotHear);

		Condition timer = runaway.getFloatCondition("timeInState", 5f,
				Comparison.GREATER);
		Condition and = new AndCondition(cannotSeeNorHear, timer);
		Transition subStateToRun = new Transition(runaway, canSeeOrHear);
		Transition runToNotAtCen = new Transition(notAtCen, and);

		notAtCen.setTransitions(notCenToRandom);
		runaway.setTransitions(runToNotAtCen);
		subparent.setTransitions(subStateToRun);

		npc2.setStateMachine(parent);

		// state machine for npc3
		parent = new HierarchicalState(null, npc3);
		CustomState walkToLocationState = new CustomState(parent, npc3);
		WalkAction walkAction1 = new WalkAction(npc3, new Coordinate(1536, 160), westTown);
		walkToLocationState.setActions(walkAction1);

		parent.setInitialState(walkToLocationState);

		npc3.setStateMachine(parent);

		// WEAPONS SETUP

		final int weaponWidth = 32;
		final int weaponHeight = 32;

		// melee attack test stuff
		Shield shield = new Shield("LameShield", SHIELD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon sword = new MeleeWeapon("LameSword", SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon sword2 = new MeleeWeapon("Sword2", SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon evilSword = new MeleeWeapon("Evil Sword", EVIL_SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon holySword = new MeleeWeapon("Holy Sword", HOLY_SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon blueSword = new MeleeWeapon("Blue Sword", BLUE_SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon shadowSword = new MeleeWeapon("Shadow Sword", SHADOW_SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
	
		StatusEffect poison = new Poison(50, 3, 2f);
		sword.addEffect(poison);
		sword2.addEffect(poison);

		player.equipWeapon(exampleMap, sword);
		player.swapWeapon(exampleMap);
		player.equipShield(shield);
		npc1.equipWeapon(exampleMap, sword2);

		player.addItemToInventory(sword);
		player.addItemToInventory(evilSword);
		player.addItemToInventory(holySword);
		player.addItemToInventory(blueSword);
		player.addItemToInventory(shadowSword);
		
		// ranged attack test stuff
		RangedWeapon bow = new RangedWeapon("LameBow", ARROW_PATH, weaponWidth,
				weaponHeight, 32, 32);
		player.equipWeapon(exampleMap, bow);
		Projectile arrow = new Projectile("Arrow", ARROW_PATH, weaponWidth, weaponHeight,
				32, 32);
		bow.equipProjectile(arrow, 1000);

		Shield plainShield = new Shield("Plain Shield", SHIELD_PATH, weaponWidth,
				weaponHeight, 32, 32);
		npc1.equipShield(plainShield);
		// npc1.usingShield(true);
	}
	
	public void render()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (movingToAnotherMap)
		{
			return;
		}

		getCurrentMap().render(camera, batch);

		temporaryVisionConeTest();
		temporaryHearingTest();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// temporary dialogue stuff
		if (dialogueSystem.getInDialogue())
		{
			dialogueSystem.render(batch, camera);
		}

		if(inventoryMenu.getInMenu())
		{
			inventoryMenu.render(batch, camera);
		}
		
		// rendering reputation, and other notifications
		if (renderMessage)
		{
			if (((System.currentTimeMillis() - startMessageDisplayTime) / 1000) < MESSAGE_DISPLAY_SECONDS)
			{
				renderMessage();
			}
			else
			{
				renderMessage = false;
			}
		}

		if (warping)
		{
			whiteScreen.setSize(camera.viewportWidth, camera.viewportHeight);
			whiteScreen.setPosition(camera.position.x - camera.viewportWidth
					/ 2, camera.position.y - camera.viewportHeight / 2);
			whiteScreen.draw(batch, warpingAlpha);
		}

		// render HUD and overlays
		float healthX = camera.position.x - camera.viewportWidth / 2 + 15;
		float healthY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont
				.draw(batch, "Health: " + player.getHealth(), healthX, healthY);

		float dayCountX = camera.position.x - camera.viewportWidth / 2 + 15;
		float dayCountY = camera.position.y + camera.viewportHeight / 2 - 35;
		int daysLeft = NUM_DAYS - dayCount;
		debugFont.draw(batch, "Days left: " + daysLeft, dayCountX, dayCountY);

		float fpsX = camera.position.x + camera.viewportWidth / 2 - 70;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

		batch.end();
	}

	public void update(float deltaTime)
	{
		camera.update();
		timer.update(deltaTime);

		if (!updateEnable)
		{
			player.handleDialogueInput();
			return;
		}
		
		dayTimer.update(deltaTime);

		Iterator<Map> iter = maps.iterator();
		while (iter.hasNext())
		{
			Map map = iter.next();
			map.update(deltaTime);
		}
	}

	public void warpPlayerToOtherMap(final Map newMap,
			final Coordinate newLocation)
	{
		
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

	private void temporaryVisionConeTest()
	{
		float tempX = 0.0f;
		float tempY = 0.0f;
		float tempSightDistance = 0.0f;
		Direction tempDirection = null;
		float visionFieldPoints[] = new float[8];

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);

		ArrayList<GameCharacter> charactersOnMap = getCurrentMap()
				.getCharactersOnMap();
		Iterator<GameCharacter> iterator1 = charactersOnMap.iterator();
		while (iterator1.hasNext())
		{
			GameCharacter tempCharacter = iterator1.next();
			if (!tempCharacter.equals(player))
			{
				tempSightDistance = tempCharacter.getSightDistance();
				tempDirection = tempCharacter.getFaceDirection();
				tempX = tempCharacter.getCenterX();
				tempY = tempCharacter.getCenterY();
				if (tempDirection == Direction.UP)
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
				else if (tempDirection == Direction.DOWN)
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
				else if (tempDirection == Direction.LEFT)
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
				{// facing right
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

	private void temporaryHearingTest()
	{
		float tempX = 0.0f;
		float tempY = 0.0f;
		float tempHearingRadius = 0.0f;

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);

		ArrayList<GameCharacter> charactersOnMap = getCurrentMap()
				.getCharactersOnMap();
		Iterator<GameCharacter> iterator1 = charactersOnMap.iterator();
		while (iterator1.hasNext())
		{
			GameCharacter tempCharacter = iterator1.next();
			if (!tempCharacter.equals(player))
			{
				tempHearingRadius = tempCharacter.getHearingRadius();

				tempX = tempCharacter.getCenterX();
				tempY = tempCharacter.getCenterY();

				shapeRenderer.setColor(0, 1, 0, 0.025f);
				shapeRenderer.circle(tempX, tempY, tempHearingRadius);
			}
		}
		shapeRenderer.end();
	}

	private void renderMessage()
	{
		float messagePositionX = (float) (camera.position.x
				- camera.viewportWidth / 2 + (0.1 * camera.viewportWidth));
		float messagePositionY = camera.position.y - camera.viewportHeight / 2;

		float messageWidth = (float) (camera.viewportWidth * 0.8);
		float messageHeight = 40.0f;

		LabelStyle style = new LabelStyle(messageFont, Color.DARK_GRAY);
		Label messageLabel = new Label(message, style);
		messageLabel.setWrap(true);
		messageLabel.setBounds(messagePositionX, messagePositionY,
				messageWidth, messageHeight);

		batch.draw(messageTexture, messagePositionX, messagePositionY,
				messageWidth, messageHeight);

		messageLabel.draw(batch, 1.0f);
		messageLabel.clear();
	}

	public void pushMessage(String message)
	{
		renderMessage = true;
		this.message = message;
		startMessageDisplayTime = System.currentTimeMillis();
	}
}
