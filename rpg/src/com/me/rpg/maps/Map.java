package com.me.rpg.maps;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.me.rpg.ScreenHandler;
import com.me.rpg.World;
import com.me.rpg.characters.DeadCharacter;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.Weapon;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Timer;

public abstract class Map implements Disposable, Serializable
{

	private static final long serialVersionUID = 6670415681217808756L;

	public static final String GRAVESTONE_PATH = "gravestones.png";

	protected final World world;
	protected final MapType mapType;
	private final String mapTmxPath;

	protected GameCharacter focusedCharacter;
	protected int mapWidth;
	protected int mapHeight;

	protected ArrayList<GameCharacter> charactersOnMap = new ArrayList<GameCharacter>();
	protected ArrayList<Projectile> flyingProjectiles = new ArrayList<Projectile>();
	protected ArrayList<Weapon> equippedWeapons = new ArrayList<Weapon>();
	protected ArrayList<DeadCharacter> corpses = new ArrayList<DeadCharacter>();

	protected transient RectangleMapObject[] collidables;
	protected transient RectangleMapObject[] warpPoints;

	// Tiled map variables
	protected transient TiledMap tiledMap;
	protected transient OrthogonalTiledMapRenderer tiledMapRenderer;

	// Tiled layers drawn behind characters, labeled by index
	protected final int[] backgroundLayers = new int[] { 0, 1, 2 };
	// Tiled layers drawn in front of characters, labeled by index
	protected final int[] foregroundLayers = new int[] { 3 };

	private boolean enableCameraSwitch = false;
	private boolean cameraPan = false;
	private float oldCameraZoom = 0f;

	protected Timer timer = new Timer();

	public MapType getMapType()
	{
		return mapType;
	}

	public int getWidth()
	{
		return mapWidth;
	}

	public int getHeight()
	{
		return mapHeight;
	}

	public TiledMap getTiledMap()
	{
		return tiledMap;
	}

	public ArrayList<GameCharacter> getCharactersOnMap()
	{
		return charactersOnMap;
	}

	public RectangleMapObject[] getObjectsOnMap()
	{
		return collidables;
	}

	public Timer getTimer()
	{
		return timer;
	}

	protected Map(World world, MapType mapType, String mapTmxPath)
	{
		this.world = world;
		this.mapType = mapType;
		this.mapTmxPath = mapTmxPath;
	}

	protected void setup()
	{
		// get Tiled map
		tiledMap = ScreenHandler.manager.get(mapTmxPath, TiledMap.class);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, world.batch);

		// get map width and height from .tmx file
		MapProperties mapProperties = tiledMap.getProperties();
		int tileWidth = (Integer) mapProperties.get("tilewidth");
		int tileHeight = (Integer) mapProperties.get("tileheight");
		mapWidth = ((Integer) mapProperties.get("width")) * tileWidth;
		mapHeight = ((Integer) mapProperties.get("height")) * tileHeight;

		// get collidables
		MapObjects collidableObjects = getCollidables();
		collidables = new RectangleMapObject[collidableObjects.getCount()];
		for (int i = 0; i < collidables.length; i++)
		{
			collidables[i] = (RectangleMapObject) collidableObjects.get(i);
		}

		// get warp points
		MapObjects warpObjects = getWarpPoints();
		warpPoints = new RectangleMapObject[warpObjects.getCount()];
		for (int i = 0; i < warpPoints.length; i++)
		{
			warpPoints[i] = (RectangleMapObject) warpObjects.get(i);
		}
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		setup();
	}

	protected MapObjects getCollidables()
	{
		MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
		return collisionLayer.getObjects();
	}

	protected MapObjects getSpawnPoints()
	{
		MapLayer spawnLayer = tiledMap.getLayers().get("Spawn");
		return spawnLayer.getObjects();
	}

	public MapObjects getWalkingBoundaries()
	{
		MapLayer walkingBoundariesLayer = tiledMap.getLayers().get(
				"WalkingBoundaries");
		return walkingBoundariesLayer.getObjects();
	}

	protected MapObjects getWarpPoints()
	{
		MapLayer warpLayer = tiledMap.getLayers().get("Warp");
		return warpLayer.getObjects();
	}

	protected MapObjects getWaypoints()
	{
		MapLayer waypointsLayer = tiledMap.getLayers().get("Waypoints");
		return waypointsLayer.getObjects();
	}

	private void cameraPanMovement()
	{
		OrthographicCamera camera = world.camera;
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			camera.zoom += 0.02;
		}
		if (Gdx.input.isKeyPressed(Keys.Q))
		{
			camera.zoom -= 0.02;
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			if (camera.position.x > 0)
				camera.translate(-3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			if (camera.position.x < mapWidth)
				camera.translate(3, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			if (camera.position.y > 0)
				camera.translate(0, -3, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			if (camera.position.y < mapHeight)
				camera.translate(0, 3, 0);
		}
	}

	/**
	 * This method should be called after construction of Map when control is
	 * first given to the player. Subclasses will override it with specific
	 * implementation.
	 */
	public void open()
	{
		// default implementation
	}

	/**
	 * This method should be called right before going to a new Map. Subclasses
	 * will override it with specific implementation.
	 */
	public void close()
	{
		// default implementation
	}

	/**
	 * Draws the background image of the map, followed by all the Characters
	 * that are in view nearby the focusedCharacter
	 */
	public void render(OrthographicCamera camera, SpriteBatch batch)
	{
		Coordinate focusedCoordinate = Coordinate.ZERO;
		if (focusedCharacter != null)
		{
			focusedCoordinate = focusedCharacter.getCenter();
		}

		float viewportWidth = camera.viewportWidth;
		float viewportHeight = camera.viewportHeight;
		float focusX = focusedCoordinate.getX();
		float focusY = focusedCoordinate.getY();
		float bottomLeftX = focusX - viewportWidth / 2;
		float bottomLeftY = focusY - viewportHeight / 2;
		// int width = Math.min(mapWidth, viewportWidth);
		// int height = Math.min(mapHeight, viewportHeight);
		if (mapWidth >= viewportWidth)
		{
			if (bottomLeftX > mapWidth - viewportWidth)
			{
				bottomLeftX = mapWidth - viewportWidth;
			}
			if (bottomLeftX < 0)
			{
				bottomLeftX = 0;
			}
		}
		else
		{ // handles screen larger than map
			bottomLeftX = (mapWidth - viewportWidth) / 2;
		}

		if (mapHeight >= viewportHeight)
		{
			if (bottomLeftY > mapHeight - viewportHeight)
			{
				bottomLeftY = mapHeight - viewportHeight;
			}
			if (bottomLeftY < 0)
			{
				bottomLeftY = 0;
			}
		}
		else
		{
			bottomLeftY = (mapHeight - viewportHeight) / 2;
		}

		if (!cameraPan)
		{
			camera.position.set(bottomLeftX + viewportWidth / 2, bottomLeftY
					+ viewportHeight / 2, 0);
		}

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render(backgroundLayers);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		Rectangle cameraBounds = new Rectangle(camera.position.x - camera.zoom
				* viewportWidth / 2, camera.position.y - camera.zoom
				* viewportHeight / 2, camera.zoom * viewportWidth, camera.zoom
				* viewportHeight);

		Iterator<DeadCharacter> deadIter = corpses.iterator();
		while (deadIter.hasNext())
		{
			DeadCharacter dead = deadIter.next();
			if (dead.getHitBox().overlaps(cameraBounds))
			{
				dead.render(batch);
			}
		}

		Iterator<GameCharacter> charIter = charactersOnMap.iterator();
		while (charIter.hasNext())
		{
			GameCharacter selected = charIter.next();
			if (selected.getBoundingRectangle().overlaps(cameraBounds))
			{
				selected.render(batch);
			}
		}

		Iterator<Projectile> projectileIter = flyingProjectiles.iterator();
		while (projectileIter.hasNext())
		{
			Projectile p = projectileIter.next();
			if (p.isFinished() || !p.isFired())
			{
				projectileIter.remove();
				continue;
			}
			Rectangle projectileLoc = p.getSpriteBounds();
			if (projectileLoc.overlaps(cameraBounds))
			{
				p.render(batch);
			}
		}

		batch.end();

		tiledMapRenderer.render(foregroundLayers);
	}

	public void update(float deltaTime)
	{
		if (Gdx.input.isKeyPressed(Keys.C))
		{
			if (enableCameraSwitch)
			{
				enableCameraSwitch = false;
				cameraPan = !cameraPan;
				if (cameraPan)
				{
					oldCameraZoom = world.camera.zoom;
				}
				else
				{
					world.camera.zoom = oldCameraZoom;
				}
			}
		}
		else
		{
			enableCameraSwitch = true;
		}

		if (cameraPan)
		{
			cameraPanMovement();
			return;
		}

		timer.update(deltaTime);

		// update dead character fade
		Iterator<DeadCharacter> deadIter = corpses.iterator();
		while (deadIter.hasNext())
		{
			DeadCharacter deadChar = deadIter.next();
			deadChar.update(deltaTime);
			if (deadChar.isGameOver())
			{
				world.setGameOver(true);
			}
		}

		// check for dead characters
		Iterator<GameCharacter> charIter;
		charIter = charactersOnMap.iterator();
		while (charIter.hasNext())
		{
			GameCharacter character = charIter.next();
			if (character.isDead())
			{
				charIter.remove();
				corpses.add(new DeadCharacter(character, GRAVESTONE_PATH, 3.0f));
			}
		}

		// check intersections with weapons
		Iterator<Weapon> weaponIter = equippedWeapons.iterator();
		Iterator<Projectile> projIter;
		while (weaponIter.hasNext())
		{
			Weapon w = weaponIter.next();
			if (!w.isAttacking())
			{
				continue;
			}
			charIter = charactersOnMap.iterator();
			Rectangle weaponBox = w.getSpriteBounds();
			while (charIter.hasNext())
			{
				GameCharacter c = charIter.next();
				if (c.equals(w.getOwner()))
					continue;
				if (weaponBox.overlaps(c.getHitBox()))
				{
					c.receiveAttack(w);
				}
			}
		}

		// check if projectiles hit
		projIter = flyingProjectiles.iterator();
		while (projIter.hasNext())
		{
			Projectile p = projIter.next();
			charIter = charactersOnMap.iterator();
			Rectangle projectileBox = p.getSpriteBounds();
			while (charIter.hasNext())
			{
				GameCharacter c = charIter.next();
				if (c.equals(p.getFiredWeapon().getOwner()))
					continue;
				if (projectileBox.overlaps(c.getHitBox()))
				{
					c.receiveAttack(p);
					p.setHasHit();
				}
			}
		}

		// move characters, projectiles
		Iterator<GameCharacter> iter = charactersOnMap.iterator();
		while (iter.hasNext())
		{
			GameCharacter selected = iter.next();
			selected.update(deltaTime, this);
		}
		Iterator<Projectile> it = flyingProjectiles.iterator();
		while (it.hasNext())
		{
			it.next().update(deltaTime);
		}
	}

	/**
	 * Checks collision with objects and characters on map. Does not check warp
	 * points.
	 */
	public boolean checkCollision(float x, float y, float oldX, float oldY,
			GameCharacter thisCharacter, final Coordinate newCoordinate)
	{
		boolean canMoveInXDirection = true;
		boolean canMoveInYDirection = true;

		float width = thisCharacter.getSpriteWidth();
		float height = thisCharacter.getSpriteHeight();
		Rectangle boundingBox = new Rectangle(x, y, width, height);
		Rectangle boundingBoxWithNewY = new Rectangle(oldX, y, width, height);
		Rectangle boundingBoxWithNewX = new Rectangle(x, oldY, width, height);
		newCoordinate.setX(x);
		newCoordinate.setY(y);

		// collision detection with objects on map
		boolean canMove = checkCollisionWithObjects(boundingBox);
		if (!canMove)
		{
			canMoveInXDirection &= checkCollisionWithObjects(boundingBoxWithNewX);
			canMoveInYDirection &= checkCollisionWithObjects(boundingBoxWithNewY);
		}

		// collision detection with characters
		GameCharacter c = checkCollisionWithCharacters(boundingBox,
				thisCharacter);
		if (c != null)
		{
			c = checkCollisionWithCharacters(boundingBoxWithNewX, thisCharacter);
			canMoveInXDirection &= (c == null);
			c = checkCollisionWithCharacters(boundingBoxWithNewY, thisCharacter);
			canMoveInYDirection &= (c == null);
		}

		if (!canMoveInXDirection)
		{
			newCoordinate.setX(oldX);
		}
		if (!canMoveInYDirection)
		{
			newCoordinate.setY(oldY);
		}

		return canMoveInXDirection || canMoveInYDirection;
	}

	/**
	 * A collision check that only check objects. Objects right now is vague,
	 * but it will probably be trees, buildings, walls, rocks, and other
	 * immovable objects.
	 */
	public boolean checkCollisionWithObjects(Rectangle boundingRectangle)
	{
		RectangleMapObject[] objectsOnMap = getObjectsOnMap();
		for (RectangleMapObject object : objectsOnMap)
		{
			Rectangle r = object.getRectangle();
			if (r.overlaps(boundingRectangle))
			{
				return false;
			}
		}
		return true;
	}

	public boolean checkCollisionWithObjects(float x, float y, float oldX,
			float oldY, float width, float height, final Coordinate newCoordinate)
	{
		newCoordinate.setX(x);
		newCoordinate.setY(y);

		Rectangle boundingBox = new Rectangle(x, y, width, height);
		boolean canMove = checkCollisionWithObjects(boundingBox);
		if (canMove)
		{
			return true;
		}

		Rectangle boundingBoxWithNewX = new Rectangle(x, oldY, width, height);
		if (checkCollisionWithObjects(boundingBoxWithNewX))
		{
			newCoordinate.setY(oldY);
			return true;
		}

		Rectangle boundingBoxWithNewY = new Rectangle(oldX, y, width, height);
		if (checkCollisionWithObjects(boundingBoxWithNewY))
		{
			newCoordinate.setX(oldX);
			return true;
		}

		return false; // we cannot move in either direction
	}

	/**
	 * A collision check that only checks characters. For example, you can check
	 * if a weapon's hitbox collided with any characters without having to worry
	 * about other objects on the map. Returns a reference to a Character object
	 * if the input hitbox collides with the Character's hitbox. Returns null
	 * otherwise.
	 */
	public GameCharacter checkCollisionWithCharacters(Rectangle hitbox,
			GameCharacter thisCharacter)
	{
		ArrayList<GameCharacter> charactersOnMap = getCharactersOnMap();
		Iterator<GameCharacter> iter = charactersOnMap.iterator();
		while (iter.hasNext())
		{
			GameCharacter selected = iter.next();
			if (selected.equals(thisCharacter))
			{
				continue;
			}
			Rectangle r = selected.getBoundingRectangle();
			if (hitbox.overlaps(r))
			{
				return selected;
			}
		}
		return null;
	}

	/**
	 * A collision check that only checks warp points. Returns a reference to a
	 * MapType object if the input hitbox collides with a warp point. Returns null
	 * otherwise.
	 */
	public MapType checkCollisionWithWarpPoints(Rectangle hitbox, Coordinate warpCoordinate)
	{
		Vector2 centerPoint = hitbox.getCenter(new Vector2());
		for (RectangleMapObject warpPoint : warpPoints)
		{
			if (warpPoint.getRectangle().contains(centerPoint))
			{
				String newMapString = warpPoint.getName();
				Coordinate c = getWarpCoordinate(warpPoint);
				warpCoordinate.setX(c.getX());
				warpCoordinate.setY(c.getY());
				return MapType.getMapType(newMapString);
			}
		}
		return null;
	}
	
	/**
	 * Currently lazy implementation - it will just return characters in a certain radius
	 * from the given character
	 * @param mainChar
	 * @return List of Characters than the input Character can see
	 */
	// TODO: make this based off of the line-of-sight cone of the character
	public ArrayList<GameCharacter> canSeeCharacters(GameCharacter mainChar, int dist) {
		Iterator<GameCharacter> iter = charactersOnMap.iterator();
		Coordinate mainCenter = mainChar.getCenter();
		ArrayList<GameCharacter> result = new ArrayList<GameCharacter>();
		while (iter.hasNext()) {
			GameCharacter next = iter.next();
			if (next == mainChar)
				continue;
			Coordinate center = next.getCenter();
			float diffx = center.getX() - mainCenter.getX();
			float diffy = center.getY() - mainCenter.getY();
			if (diffx*diffx + diffy*diffy < dist*dist)
				result.add(next);
		}
		return result;
	}

	private Coordinate getWarpCoordinate(RectangleMapObject warpPoint)
	{
		float warpX = Float.parseFloat(warpPoint.getProperties().get("x", String.class));
		float warpY = Float.parseFloat(warpPoint.getProperties().get("y", String.class));
		return new Coordinate(warpX, warpY);
	}

	/*
	 * ADDING/REMOVING CHARACTERS
	 */

	public void removeCharacterFromMap(GameCharacter removeCharacter)
	{
		if (!isCharacterOnMap(removeCharacter))
		{
			throw new RuntimeException(
					"Character cannot be removed from map - it is not on the map. Data: "
							+ removeCharacter);
		}
		charactersOnMap.remove(removeCharacter);
		removeCharacter.removedFromMap(this);
		if (focusedCharacter.equals(removeCharacter))
		{
			focusedCharacter = null;
		}
	}

	public void addCharacterToMap(GameCharacter newCharacter,
			Coordinate newLocation)
	{
		if (newLocation == null)
		{
			throw new RuntimeException(
					"Cannot add the Character to the map - null location passed in. Data: "
							+ newCharacter);
		}
		if (isCharacterOnMap(newCharacter))
		{
			throw new RuntimeException(
					"Cannot add the Character to the map - it is already on the map. Data: OldLoc: "
							+ newCharacter.getBottomLeftCorner()
							+ " "
							+ newCharacter + " " + newLocation);
		}
		newCharacter.setBottomLeftCorner(newLocation);
		charactersOnMap.add(newCharacter);
		newCharacter.addedToMap(this);
	}

	public void setFocusedCharacter(GameCharacter newFocus)
	{
		if (!isCharacterOnMap(newFocus))
		{
			throw new RuntimeException(
					"Cannot set character to be focus if it is not already on the map. Data: "
							+ newFocus);
		}
		focusedCharacter = newFocus;
	}

	// HELPER METHODS

	public void addCharacterToMap(GameCharacter newCharacter, float x, float y)
	{
		Coordinate newLocation = new Coordinate(x, y);
		addCharacterToMap(newCharacter, newLocation);
	}

	public void addFocusedCharacterToMap(GameCharacter newFocusedCharacter,
			float x, float y)
	{
		addFocusedCharacterToMap(newFocusedCharacter, new Coordinate(x, y));
	}

	public void addFocusedCharacterToMap(GameCharacter newFocusedCharacter,
			Coordinate newLocation)
	{
		addCharacterToMap(newFocusedCharacter, newLocation);
		setFocusedCharacter(newFocusedCharacter);
	}

	/*
	 * END ADDING/REMOVING CHARACTERS
	 */

	public boolean isCharacterOnMap(GameCharacter character)
	{
		return charactersOnMap.contains(character);
	}

	public void addProjectile(Projectile p)
	{
		flyingProjectiles.add(p);
	}

	public void addEquippedWeapon(Weapon w)
	{
		equippedWeapons.add(w);
	}

	public void removeEquippedWeapon(Weapon w)
	{
		equippedWeapons.remove(w);
	}

	@Override
	public void dispose()
	{
		tiledMapRenderer.dispose();
	}

	public GameCharacter getFocusedCharacter()
	{
		return focusedCharacter;
	}

}
