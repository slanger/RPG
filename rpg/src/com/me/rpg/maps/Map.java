package com.me.rpg.maps;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
import com.me.rpg.utils.Location;
import com.me.rpg.utils.Timer;
import com.me.rpg.utils.Waypoint;

public abstract class Map
	implements Disposable, Serializable
{

	private static final long serialVersionUID = 6670415681217808756L;

	public static final String GRAVESTONE_PATH = "gravestones.png";

	protected final World world;
	protected final MapType mapType;
	private final String mapTmxPath;

	protected GameCharacter focusedCharacter;
	private List<Waypoint> waypoints = null;
	private List<Waypoint> sourceWarpPoints = null;
	protected int mapWidth, mapHeight;
	protected int tileWidth, tileHeight;

	private ArrayList<GameCharacter> charactersOnMap = new ArrayList<GameCharacter>();
	private ArrayList<GameCharacter> charactersToRemove = new ArrayList<GameCharacter>();
	private ArrayList<Projectile> flyingProjectiles = new ArrayList<Projectile>();
	private ArrayList<Weapon> equippedWeapons = new ArrayList<Weapon>();
	private ArrayList<DeadCharacter> corpses = new ArrayList<DeadCharacter>();

	private List<Rectangle> collidables = new ArrayList<Rectangle>();

	// Tiled map variables
	protected transient TiledMap tiledMap;
	protected transient OrthogonalTiledMapRenderer tiledMapRenderer;

	// Tiled layers drawn behind characters, labeled by index
	private final int[] backgroundLayers = new int[] { 0, 1, 2 };
	// Tiled layers drawn in front of characters, labeled by index
	private final int[] foregroundLayers = new int[] { 3 };

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

	public Timer getTimer()
	{
		return timer;
	}

	public String getName()
	{
		return mapType.getMapName();
	}

	public List<Waypoint> getWaypoints()
	{
		if (waypoints == null)
		{
			waypoints = new ArrayList<Waypoint>();
			sourceWarpPoints = new ArrayList<Waypoint>();
			createWaypoints(waypoints, sourceWarpPoints);
		}
		return waypoints;
	}

	public List<Waypoint> getSourceWarpPoints()
	{
		if (sourceWarpPoints == null)
		{
			waypoints = new ArrayList<Waypoint>();
			sourceWarpPoints = new ArrayList<Waypoint>();
			createWaypoints(waypoints, sourceWarpPoints);
		}
		return sourceWarpPoints;
	}

	protected Map(World world, MapType mapType, String mapTmxPath)
	{
		this.world = world;
		this.mapType = mapType;
		this.mapTmxPath = mapTmxPath;

		setup();

		// get map width and height from .tmx file
		MapProperties mapProperties = tiledMap.getProperties();
		tileWidth = (Integer) mapProperties.get("tilewidth");
		tileHeight = (Integer) mapProperties.get("tileheight");
		mapWidth = ((Integer) mapProperties.get("width")) * tileWidth;
		mapHeight = ((Integer) mapProperties.get("height")) * tileHeight;
	}

	protected void setup()
	{
		// get Tiled map
		tiledMap = ScreenHandler.manager.get(mapTmxPath, TiledMap.class);
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, world.batch);

		// load collidables
		MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
		MapObjects collidableObjects = collisionLayer.getObjects();
		for (int i = 0; i < collidableObjects.getCount(); i++)
		{
			RectangleMapObject collidableObject = (RectangleMapObject) collidableObjects.get(i);
			collidables.add(collidableObject.getRectangle());
		}
	}

	private void readObject(ObjectInputStream inputStream)
		throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		setup();
	}

	private void loadWarpPoints(final List<RectangleMapObject> sourceWarpPoints, final List<RectangleMapObject> destinationWarpPoints)
	{
		MapLayer warpLayer = tiledMap.getLayers().get("Warp");
		MapObjects warpObjects = warpLayer.getObjects();
		for (int i = 0; i < warpObjects.getCount(); i++)
		{
			RectangleMapObject warpObject = (RectangleMapObject) warpObjects.get(i);
			if (warpObject.getName() == null)
			{
				sourceWarpPoints.add(warpObject);
			}
			else
			{
				destinationWarpPoints.add(warpObject);
			}
		}
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
			Rectangle projectileBox = p.getSpriteBounds();
			GameCharacter hitChar = checkCollisionWithCharacters(projectileBox, null);
			if (hitChar != null && !hitChar.equals(p.getFiredWeapon().getOwner())) {
				hitChar.receiveAttack(p);
				p.setHasHit();
			}
			boolean hit = !checkCollisionWithObjects(projectileBox);
			if (hit)
				p.setHasHit();
			
			if (p.isFinished() || !p.isFired())
			{
				projIter.remove();
			}
		}

		// move characters, projectiles
		Iterator<GameCharacter> characterIter = charactersOnMap.iterator();
		while (characterIter.hasNext())
		{
			GameCharacter character = characterIter.next();
			character.update(deltaTime);
		}

		// remove characters
		Iterator<GameCharacter> removeIter = charactersToRemove.iterator();
		while (removeIter.hasNext())
		{
			GameCharacter character = removeIter.next();
			removeCharacter(character);
		}
		charactersToRemove.clear();

		Iterator<Projectile> projectileIter = flyingProjectiles.iterator();
		while (projectileIter.hasNext())
		{
			projectileIter.next().update(deltaTime);
		}
	}

	/*
	 * CREATE WAYPOINTS
	 */

	private void createWaypoints(final List<Waypoint> waypoints, final List<Waypoint> sourceWarpPoints)
	{
		// load warp points
		List<RectangleMapObject> sourceWarpPointObjects = new ArrayList<RectangleMapObject>();
		List<RectangleMapObject> destinationWarpPointObjects = new ArrayList<RectangleMapObject>();
		loadWarpPoints(sourceWarpPointObjects, destinationWarpPointObjects);

		for (RectangleMapObject warpPoint : sourceWarpPointObjects)
		{
			Rectangle r = warpPoint.getRectangle();
			String connectedWarpPointName = (String) warpPoint.getProperties()
					.get("connected_to");
			if (checkRectangle(r, waypoints, true))
			{
				Waypoint w = new Waypoint(new Location(this, r), connectedWarpPointName, true);
				waypoints.add(w);
				sourceWarpPoints.add(w);
			}
		}

		for (RectangleMapObject warpPoint : destinationWarpPointObjects)
		{
			Rectangle r = warpPoint.getRectangle();
			String name = warpPoint.getName();
			if (checkRectangle(r, waypoints, true))
			{
				Waypoint w = new Waypoint(new Location(this, r), name, false);
				waypoints.add(w);
				sourceWarpPoints.add(w);
			}
		}

		for (Rectangle collidable : collidables)
		{
			float x = collidable.getX();
			float y = collidable.getY();
			float width = collidable.getWidth();
			float height = collidable.getHeight();

			Rectangle bottomLeft = new Rectangle(x - tileWidth, y - tileHeight,
					tileWidth, tileHeight);
			if (checkRectangle(bottomLeft, waypoints, false))
				waypoints.add(new Waypoint(new Location(this, bottomLeft)));

			Rectangle topLeft = new Rectangle(x - tileWidth, y + height,
					tileWidth, tileHeight);
			if (checkRectangle(topLeft, waypoints, false))
				waypoints.add(new Waypoint(new Location(this, topLeft)));

			Rectangle topRight = new Rectangle(x + width, y + height,
					tileWidth, tileHeight);
			if (checkRectangle(topRight, waypoints, false))
				waypoints.add(new Waypoint(new Location(this, topRight)));

			Rectangle bottomRight = new Rectangle(x + width, y - tileHeight,
					tileWidth, tileHeight);
			if (checkRectangle(bottomRight, waypoints, false))
				waypoints.add(new Waypoint(new Location(this, bottomRight)));
		}

		createConnections(waypoints);
	}

	private void createConnections(final List<Waypoint> waypoints)
	{
		final int length = waypoints.size();
		for (int i = 0; i < length; i++)
		{
			Waypoint waypoint = waypoints.get(i);
			Coordinate waypointCenter = waypoint.getCenter();
			for (int j = i + 1; j < length; j++)
			{
				Waypoint w = waypoints.get(j);
				if (waypoint.connectedTo(w))
				{
					Coordinate wCenter = w.getCenter();
					float distance = waypointCenter.distanceTo(wCenter);
					waypoint.connections.add(new Waypoint.Edge(w, distance));
					w.connections.add(new Waypoint.Edge(waypoint, distance));
				}
			}
		}
	}

	private boolean checkRectangle(Rectangle rectangle,
			List<Waypoint> waypoints, boolean isWarpPoint)
	{
		// check if waypoint is out of bounds
		if (!isWarpPoint)
		{
			if (!checkIfInBounds(rectangle))
			{
				return false;
			}
		}

		// check if waypoint equals another waypoint we've already added
		for (Waypoint w : waypoints)
		{
			Rectangle r = w.getRectangle();
			if (sameRectangles(rectangle, r))
			{
				return false;
			}
		}

		// check if waypoint is inside a static collidable
		for (Rectangle collidable : collidables)
		{
			if (rectangle.overlaps(collidable))
			{
				return false;
			}
		}

		return true;
	}

	private boolean checkIfInBounds(Rectangle r)
	{
		if (r.x < 0 || r.x > mapWidth)
			return false;
		if (r.y < 0 || r.y > mapHeight)
			return false;
		if (r.x + r.width < 0 || r.x + r.width > mapWidth)
			return false;
		if (r.y + r.height < 0 || r.y + r.height > mapHeight)
			return false;

		return true;
	}

	private boolean sameRectangles(Rectangle r1, Rectangle r2)
	{
		return r1.equals(r2);
	}

	public boolean rectanglesConnected(Rectangle r1, Rectangle r2)
	{
		// check centers
		Vector2 center1 = r1.getCenter(new Vector2());
		Vector2 center2 = r2.getCenter(new Vector2());
		if (!pointsConnected(center1, center2))
			return false;

		// check bottom left corners
		Vector2 bottomLeft1 = new Vector2(r1.x, r1.y);
		Vector2 bottomLeft2 = new Vector2(r2.x, r2.y);
		if (!pointsConnected(bottomLeft1, bottomLeft2))
			return false;

		// check top left corners
		Vector2 topLeft1 = new Vector2(r1.x, r1.y + r1.height);
		Vector2 topLeft2 = new Vector2(r2.x, r2.y + r2.height);
		if (!pointsConnected(topLeft1, topLeft2))
			return false;

		// check top right corners
		Vector2 topRight1 = new Vector2(r1.x + r1.width, r1.y + r1.height);
		Vector2 topRight2 = new Vector2(r2.x + r2.width, r2.y + r2.height);
		if (!pointsConnected(topRight1, topRight2))
			return false;

		// check top right corners
		Vector2 bottomRight1 = new Vector2(r1.x + r1.width, r1.y);
		Vector2 bottomRight2 = new Vector2(r2.x + r2.width, r2.y);
		if (!pointsConnected(bottomRight1, bottomRight2))
			return false;

		return true;
	}

	private boolean pointsConnected(Vector2 c1, Vector2 c2)
	{
		float deltaX = Math.abs(c1.x - c2.x);
		float deltaY = Math.abs(c1.y - c2.y);
		int dx = (c1.x < c2.x) ? 1 : (c2.x < c1.x) ? -1 : 0;
		int dy = (c1.y < c2.y) ? 1 : (c2.y < c1.y) ? -1 : 0;
		int n = (int) (Math
				.sqrt((((double) (deltaX * deltaX + deltaY * deltaY)) / ((double) (tileWidth
						* tileWidth + tileHeight * tileHeight)))) * 2);

		for (int i = 0; i <= n; i++)
		{
			float x = c1.x + i * (deltaX / n) * dx;
			float y = c1.y + i * (deltaY / n) * dy;
			Vector2 v = new Vector2(x, y);
			for (Rectangle r : collidables)
			{
				if (rectangleContainsPoint(r, v))
				{
					return false;
				}
			}
		}

		return true;
	}

	private boolean rectangleContainsPoint(Rectangle r, Vector2 v)
	{
		return r.x < v.x && r.x + r.width > v.x && r.y < v.y && r.y + r.height > v.y;
	}

	/*
	 * END CREATE WAYPOINTS
	 */

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
		for (Rectangle object : collidables)
		{
			if (object.overlaps(boundingRectangle))
			{
				return false;
			}
		}
		return true;
	}

	public boolean checkCollisionWithObjects(float x, float y, float oldX,
			float oldY, float width, float height,
			final Coordinate newCoordinate)
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
	 * A collision check that only checks warp points. Returns a Waypoint if the
	 * input hitbox collides with a warp point. Returns null otherwise.
	 */
	public Waypoint checkCollisionWithWarpPoints(Rectangle hitbox)
	{
		Vector2 centerPoint = hitbox.getCenter(new Vector2());
		for (Waypoint w : getSourceWarpPoints())
		{
			if (w.getRectangle().contains(centerPoint))
			{
				return w.connectedWarpPoint;
			}
		}
		return null;
	}

	/**
	 * Better implementation - checks if characters are inside the vision cone
	 * of given char
	 * 
	 * @param mainChar
	 * @return List of Characters than the input Character can see
	 */
	public ArrayList<GameCharacter> canSeeCharacters(GameCharacter mainChar)
	{
		Iterator<GameCharacter> iter = charactersOnMap.iterator();
		ArrayList<GameCharacter> result = new ArrayList<GameCharacter>();
		while (iter.hasNext())
		{
			GameCharacter next = iter.next();
			if (next == mainChar)
				continue;
			Coordinate center = next.getCenter();
			if (mainChar.checkCoordinateInVision(center))
				result.add(next);
		}
		return result;
	}

	public ArrayList<GameCharacter> canHearCharacters(GameCharacter mainChar)
	{
		Iterator<GameCharacter> iter = charactersOnMap.iterator();
		ArrayList<GameCharacter> result = new ArrayList<GameCharacter>();
		while (iter.hasNext())
		{
			GameCharacter next = iter.next();
			if (next == mainChar)
				continue;
			Coordinate center = next.getCenter();
			if (mainChar.checkCoordinateWithinHearing(center))
				result.add(next);
		}
		return result;
	}

	public ArrayList<GameCharacter> canSeeOrHearCharacters(
			final GameCharacter mainChar)
	{
		ArrayList<GameCharacter> chars = canSeeCharacters(mainChar);
		chars.addAll(canHearCharacters(mainChar));
		Collections.sort(chars, new Comparator<GameCharacter>() {

			@Override
			public int compare(GameCharacter o1, GameCharacter o2) {
				float d1 = o1.getCenter().distanceSquared(mainChar.getCenter());
				float d2 = o2.getCenter().distanceSquared(mainChar.getCenter());
				if (d1 < d2)
					return -1;
				else if (d2 > d1)
					return 1;
				return 0;
			}
			
		});
		return chars;
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
		charactersToRemove.add(removeCharacter);
	}

	private void removeCharacter(GameCharacter removeCharacter)
	{
		removeEquippedWeapon(removeCharacter.getEquippedWeapon());
		charactersOnMap.remove(removeCharacter);
		removeCharacter.removedFromMap();
		if (focusedCharacter != null && focusedCharacter.equals(removeCharacter))
		{
			focusedCharacter = null;
		}
	}

	public void addCharacterToMap(GameCharacter newCharacter,
			Location newLocation)
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
		charactersOnMap.add(newCharacter);
		addEquippedWeapon(newCharacter.getEquippedWeapon());
		newCharacter.addedToMap(newLocation);
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

	public void addFocusedCharacterToMap(GameCharacter newFocusedCharacter,
			Location newLocation)
	{
		addCharacterToMap(newFocusedCharacter, newLocation);
		setFocusedCharacter(newFocusedCharacter);
	}

	/*
	 * END ADDING/REMOVING CHARACTERS
	 */

	private boolean isCharacterOnMap(GameCharacter character)
	{
		return charactersOnMap.contains(character);
	}

	public void addProjectile(Projectile p)
	{
		flyingProjectiles.add(p);
	}

	public void addEquippedWeapon(Weapon w)
	{
		if (w != null)
			equippedWeapons.add(w);
	}

	public void removeEquippedWeapon(Weapon w)
	{
		if (w != null)
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

	public boolean isRectangleOutOfBounds(Rectangle r)
	{
		Rectangle bounds = new Rectangle(0, 0, mapWidth, mapHeight);
		return !(((r.x >= bounds.x && r.x <= bounds.x + bounds.width) && (r.x + r.width >= bounds.x && r.x + r.width <= bounds.x + bounds.width))
			&& ((r.y >= bounds.y && r.y <= bounds.y + bounds.height) && (r.y + r.height >= bounds.y && r.y + r.height <= bounds.y + bounds.height)));
	}

}
