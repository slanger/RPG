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
import com.me.rpg.maps.WestTownInsideHouse;
import com.me.rpg.maps.WestTownMap;
import com.me.rpg.reputation.ReputationDebugMenu;
import com.me.rpg.reputation.ReputationSystem;
import com.me.rpg.state.HierarchicalState;
import com.me.rpg.state.MeleeFightState;
import com.me.rpg.state.PatrolState;
import com.me.rpg.state.RandomWalkState;
import com.me.rpg.state.RangedFightState;
import com.me.rpg.state.RunAwayState;
import com.me.rpg.state.WalkToLocationState;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.RememberNearestPersonAction;
import com.me.rpg.state.transition.AndCondition;
import com.me.rpg.state.transition.Condition;
import com.me.rpg.state.transition.DistanceCondition;
import com.me.rpg.state.transition.HearPeopleCondition;
import com.me.rpg.state.transition.NotCondition;
import com.me.rpg.state.transition.OrCondition;
import com.me.rpg.state.transition.SeePeopleCondition;
import com.me.rpg.state.transition.TargetDeadCondition;
import com.me.rpg.state.transition.Transition;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.utils.GlobalTimerTask;
import com.me.rpg.utils.Location;
import com.me.rpg.utils.Timer;
import com.me.rpg.utils.Waypoint;

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
	public static final String FIRE_ARROW_PATH = "fire_arrow.png";
	//shields
	public static final String SHIELD_PATH = "shield.png";
	public static final String SHADOW_SHIELD_PATH = "shadow_shield.png";
	public static final String GRAY_SHIELD_PATH = "gray_shield.png";

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
	private final PauseScreen pauseScreen = new PauseScreen(this);
	private final ReputationDebugMenu reputationDebugMenu = new ReputationDebugMenu(this);
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

	private long removeMessageTimer = 0;
	private final long MESSAGE_DISPLAY_SECONDS = 5;
	private ArrayList<Message> messageQueue = new ArrayList<Message>();
	
	
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
	
	public PauseScreen getPauseScreen()
	{
		return pauseScreen;
	}
	
	public ReputationDebugMenu getReputationDebugMenu()
	{
		return reputationDebugMenu;
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
		
		// setup global timer
		GlobalTimerTask.getInstance().cancel();
		timer.scheduleTask(GlobalTimerTask.getInstance(), 1000000000.0f);

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
			if (waypoint.isSourceWarpPoint())
			{
				for (int j = 0; j < length; j++)
				{
					Waypoint w = waypoints.get(j);
					if (w.isDestinationWarpPoint()
							&& w.name.equals(waypoint.connectedWarpPointName))
					{
						// warp point edges are directed
						waypoint.connectedWarpPoint = w;
						waypoint.connections.add(new Waypoint.Edge(w, 0));
					}
				}
			}
		}

		// CHARACTERS SETUP

		NonplayableCharacter npc1;
		NonplayableCharacter npc2;
		NonplayableCharacter npc3;
		NonplayableCharacter npc4;
		final int width = 28;
		final int height = 28;

		// create characters
		player = new PlayableCharacter("Player", "player_group", PLAYER_TEXTURE_PATH,
				width, height, 16, 16, 0.15f, this);
		player.setBaseSpeed(200f);

		npc1 = new NonplayableCharacter("NPC1", "Test_Group", NPC_TEXTURE_PATH,
				width, height, 16, 16, 0.15f, this);
		npc2 = new NonplayableCharacter("NPC2", "Test_Group", NPC_TEXTURE_PATH,
				width, height, 16, 16, 0.15f, this);
		npc3 = new NonplayableCharacter("NPC3", "Test_Group", NPC_TEXTURE_PATH,
				width, height, 16, 16, 0.15f, this);
		npc4 = new NonplayableCharacter("NPC4", "Test_Group", NPC_TEXTURE_PATH,
				width, height, 16, 16, 0.15f, this);

		// add characters to map
		exampleMap.addFocusedCharacterToMap(player, new Location(exampleMap, 192, 544));
		exampleMap.addCharacterToMap(npc1, new Location(exampleMap, 544, 544));
		exampleMap.addCharacterToMap(npc2, new Location(exampleMap, 480, 128));
		exampleMap.addCharacterToMap(npc3, new Location(exampleMap, 64, 64));
		westTown.addCharacterToMap(npc4, new Location(westTown, 1000, 480));

		// state machine for npc1
		HierarchicalState parent1 = new HierarchicalState(null, npc1);

		Location[] patrolLocations1 = new Location[] {
				new Location(exampleMap, new Coordinate(50, 50)),
				new Location(exampleMap, new Coordinate(300, 50)),
				new Location(exampleMap, new Coordinate(300, 300)),
				new Location(exampleMap, new Coordinate(50, 300))
		};
		PatrolState patrol1 = new PatrolState(parent1, npc1, patrolLocations1);

		MeleeFightState fight1 = new MeleeFightState(parent1, npc1);

		Condition canSee = new SeePeopleCondition(npc1, 0, Comparison.NOTEQUALS);
		Condition canHear = new HearPeopleCondition(npc1, 0, Comparison.NOTEQUALS);
		Condition cannotSee = new NotCondition(canSee);
		Condition cannotHear = new NotCondition(canHear);
		Condition canSeeOrHear = new OrCondition(canSee, canHear);
		Condition cannotSeeNorHear = new AndCondition(cannotSee, cannotHear);
		
		Condition enemyDead = new TargetDeadCondition(npc1);
		Condition or = new OrCondition(cannotSeeNorHear, enemyDead);

		ArrayList<Action> patrolToFightActions = new ArrayList<Action>();
		patrolToFightActions.add(new RememberNearestPersonAction(npc1, true, true, true));
		Transition patrolToFight = new Transition(fight1, patrolToFightActions, canSeeOrHear);
		Transition fightToPatrol = new Transition(patrol1, or);

		patrol1.setTransitions(patrolToFight);
		fight1.setTransitions(fightToPatrol);
		parent1.setInitialState(patrol1);

		npc1.setStateMachine(parent1);

		// state machine for npc2
		HierarchicalState parent2 = new HierarchicalState(null, npc2);
		HierarchicalState subparent = new HierarchicalState(parent2, npc2);
		subparent.setName("subParent");
		WalkToLocationState notAtCen = new WalkToLocationState(subparent, npc2, new Location(exampleMap, 500, 100));
		notAtCen.setName("notAtCen");
		Rectangle walkingBoundary = new Rectangle(32, 32, 576, 224);
		RandomWalkState randomWalkState = new RandomWalkState(subparent, npc2,
				walkingBoundary);
		randomWalkState.setName("randomWalk");
		RunAwayState runaway = new RunAwayState(parent2, npc2);
		runaway.setName("runaway");

		subparent.setInitialState(notAtCen);
		parent2.setInitialState(subparent);

		Condition dist = new DistanceCondition(npc2, exampleMap,
				new Coordinate(500, 100));
		Transition notCenToRandom = new Transition(randomWalkState, dist);

		Condition canSee2 = new SeePeopleCondition(npc2, 0, Comparison.NOTEQUALS);
		Condition canHear2 = new HearPeopleCondition(npc2, 0, Comparison.NOTEQUALS);
		Condition cannotSee2 = new NotCondition(canSee2);
		Condition cannotHear2 = new NotCondition(canHear2);
		Condition canSeeOrHear2 = new OrCondition(canSee2, canHear2);
		Condition cannotSeeNorHear2 = new AndCondition(cannotSee2, cannotHear2);

		Condition timer = runaway.getFloatCondition("timeInState", 5f,
				Comparison.GREATER);
		Condition and = new AndCondition(cannotSeeNorHear2, timer);
		Transition subStateToRun = new Transition(runaway, canSeeOrHear2);
		Transition runToNotAtCen = new Transition(notAtCen, and);

		notAtCen.setTransitions(notCenToRandom);
		runaway.setTransitions(runToNotAtCen);
		subparent.setTransitions(subStateToRun);

		npc2.setStateMachine(parent2);

		// state machine for npc3
		HierarchicalState parent3 = new HierarchicalState(null, npc3);

		Location[] patrolLocations3 = new Location[] {
				new Location(westTown, 1536, 160),
				new Location(exampleMap, 500, 500)
		};
		PatrolState patrol3 = new PatrolState(parent3, npc3, patrolLocations3);

		parent3.setInitialState(patrol3);

		npc3.setStateMachine(parent3);
		
		// state machine for npc4
		HierarchicalState parent4 = new HierarchicalState(null, npc4);
		Rectangle wb4 = new Rectangle(50, 50, 1100, 1100);
		RandomWalkState rw4 = new RandomWalkState(parent4, npc4, wb4);
		RangedFightState rfs = new RangedFightState(parent4, npc4);
		
		Condition seePeople4 = new SeePeopleCondition(npc4, 0, Comparison.GREATER);
		Condition hearPeople4 = new HearPeopleCondition(npc4, 0, Comparison.GREATER);
		Condition or2 = new OrCondition(seePeople4, hearPeople4);
		Condition not = new NotCondition(or2);
		
		ArrayList<Action> toRangeAtkActions = new ArrayList<Action>();
		toRangeAtkActions.add(new RememberNearestPersonAction(npc4, true, true, true));
		
		Transition toRangeAtk = new Transition(rfs, toRangeAtkActions, or2);
		Transition toRandWlk = new Transition(rw4, not);
		rw4.setTransitions(toRangeAtk);
		rfs.setTransitions(toRandWlk);
		
		parent4.setInitialState(rw4);
		
		npc4.setSightDistance(400f);
		npc4.setStateMachine(parent4);

		// WEAPONS SETUP

		final int weaponWidth = 32;
		final int weaponHeight = 32;

// MELEE WEAPONS
		
		Weapon npc_sword = new MeleeWeapon("Lame Sword", SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon sword = new MeleeWeapon("Lame Sword", SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon evilSword = new MeleeWeapon("Evil Sword", EVIL_SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon holySword = new MeleeWeapon("Holy Sword", HOLY_SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon blueSword = new MeleeWeapon("Blue Sword", BLUE_SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Weapon shadowSword = new MeleeWeapon("Shadow Sword", SHADOW_SWORD_PATH, weaponWidth, weaponHeight,
				32, 32);
	
		shadowSword.addEffect(new Poison(500, 1, 1));
		
		StatusEffect poison = new Poison(50, 3, 2f);
		sword.addEffect(poison);

		player.equipWeapon(exampleMap, sword);
		//player.swapWeapon(exampleMap);
		npc1.equipWeapon(exampleMap, npc_sword);

		player.addItemToInventory(sword);
		player.addItemToInventory(evilSword);
		player.addItemToInventory(holySword);
		player.addItemToInventory(blueSword);
		player.addItemToInventory(shadowSword);
		
		
// BOWS/PROJECTILES
		RangedWeapon bow = new RangedWeapon("Lame Bow", ARROW_PATH, weaponWidth,
				weaponHeight, 32, 32);
		RangedWeapon npc_bow = new RangedWeapon("NPCBow", ARROW_PATH, weaponWidth,
				weaponHeight, 32, 32);
		npc4.equipWeapon(westTownInsideHouse, npc_bow);
		Projectile arrow = new Projectile("Arrow", ARROW_PATH, weaponWidth, weaponHeight,
				32, 32);
		Projectile fireArrow = new Projectile("Fire Arrow", FIRE_ARROW_PATH, weaponWidth, weaponHeight,
				32, 32);
		
		player.addItemToInventory(bow);
		player.addItemToInventory(arrow);
		player.addItemToInventory(fireArrow);
		player.setEquippedArrows(fireArrow);
		
		bow.equipProjectile(fireArrow, 1000);
		npc_bow.equipProjectile(fireArrow, 10000);

//SHIELDS		
		Shield shield = new Shield("Lame Shield", SHIELD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Shield shadowShield = new Shield("Shadow Shield", SHADOW_SHIELD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Shield grayShield = new Shield("Gray Shield", GRAY_SHIELD_PATH, weaponWidth, weaponHeight,
				32, 32);
		Shield npc_shield = new Shield("Plain Shield", SHIELD_PATH, weaponWidth,
				weaponHeight, 32, 32);
		
		player.addItemToInventory(shield);
		player.addItemToInventory(shadowShield);
		player.addItemToInventory(grayShield);
		
		npc1.equipShield(npc_shield);
		
		
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
		
		if(pauseScreen.getInMenu())
		{
			pauseScreen.render(batch, camera);
		}
		
		if(reputationDebugMenu.getInMenu())
		{
			reputationDebugMenu.render(batch, camera);
		}
		// rendering reputation, and other notifications
		if(messageQueue.size() > 0)
		{
			renderMessage();
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
			player.handlePauseMenuInput();
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
		
		LabelStyle normalStyle = new LabelStyle(messageFont, Color.DARK_GRAY);
		LabelStyle errorStyle = new LabelStyle(messageFont, Color.RED);
		
		if((System.currentTimeMillis() - removeMessageTimer)/1000 > MESSAGE_DISPLAY_SECONDS )
		{
			if(messageQueue.size()>0)
			{
				messageQueue.remove(messageQueue.size()-1);
				removeMessageTimer = System.currentTimeMillis();
			}
		}
		while(messageQueue.size() >= 10)
		{
			messageQueue.remove(messageQueue.size()-1);
		}
		if(messageQueue.size()<1)
		{
			return;
		}
		
		//draw the background texture
		//float messagePositionX = (float) (camera.position.x
		//		- camera.viewportWidth / 2 + (0.1 * camera.viewportWidth));
		//float messagePositionY = camera.position.y - camera.viewportHeight / 2;
		//float messageHeight = 40.0f;
		float messageWidth = (float) (camera.viewportWidth * 0.9);
		float messagePositionX = (float) (camera.position.x
						- camera.viewportWidth / 2 + (0.05 * camera.viewportWidth));
		float messagePositionY = camera.position.y - camera.viewportHeight / 2;
		float messageHeight = 20.0f * (messageQueue.size()+1);
		
		batch.draw(messageTexture, messagePositionX, messagePositionY,
				messageWidth, messageHeight);
		
		//draw the messages
		for(int i=0; i<messageQueue.size(); i++)
		{
			if(messageQueue.get(i).getIsErrorMessage() == true)
			{
				Label messageLabel = new Label(messageQueue.get(i).getMessage(), errorStyle);
				messageLabel.setWrap(true);
				
				messageLabel.setBounds(messagePositionX,messagePositionY + (i+1)*20.0f, 0, 0);
				
				messageLabel.draw(batch, 1.0f);
				messageLabel.clear();
			}
			else
			{
				Label messageLabel = new Label(messageQueue.get(i).getMessage(), normalStyle);
				messageLabel.setWrap(true);
				
				messageLabel.setBounds(messagePositionX,messagePositionY + (i+1)*20.0f, 0, 0);
				
				messageLabel.draw(batch, 1.0f);
				messageLabel.clear();
			}
		}
	}

	public void pushMessage(String msg)
	{
		Message message = new Message(msg, false);
		if(messageQueue.size()<1)
		{
			removeMessageTimer = System.currentTimeMillis();
		}
		messageQueue.add(0, message);
	}
	
	public void pushErrorMessage(String msg)
	{
		Message message = new Message(msg, true);
		if(messageQueue.size()<1)
		{
			removeMessageTimer = System.currentTimeMillis();
		}
		messageQueue.add(0, message);
	}
	
	private class Message implements Serializable
	{
		
		private static final long serialVersionUID = -2899401714649534910L;
		private String message;
		private boolean isErrorMessage;
		
		public Message(String message, boolean isErrorMessage)
		{
			this.message = message;
			this.isErrorMessage = isErrorMessage;
		}
		
		public String getMessage()
		{
			return message;
		}
		
		public boolean getIsErrorMessage()
		{
			return isErrorMessage;
		}
	}
	
	public ArrayList<GameCharacter> getCharactersInWorld()
	{
		ArrayList<GameCharacter> allGameCharacters = new ArrayList<GameCharacter>();
		for(Map map : maps)
		{
			allGameCharacters.addAll(map.getCharactersOnMap());
		}
		return allGameCharacters;
	}
}
