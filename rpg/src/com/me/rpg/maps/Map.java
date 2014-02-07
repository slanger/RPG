package com.me.rpg.maps;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.Timer;
import com.me.rpg.Character;
import com.me.rpg.Coordinate;
import com.me.rpg.RPG;
import com.me.rpg.World;
import com.me.rpg.combat.MeleeWeapon;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.RangedWeapon;
import com.me.rpg.combat.Weapon;

public abstract class Map implements Disposable
{

	protected World world;
	protected SpriteBatch batch;
	protected OrthographicCamera camera;

	protected Character focusedCharacter;
	protected int mapWidth;
	protected int mapHeight;
	
	protected ArrayList<Character> charactersOnMap;
	protected ArrayList<Projectile> flyingProjectiles;

	protected RectangleMapObject[] collidables;
	protected RectangleMapObject[] warpPoints;

	// Tiled map variables
	protected TiledMap tiledMap;
	protected OrthogonalTiledMapRenderer tiledMapRenderer;

	// Tiled layers drawn behind characters, labeled by index
	protected int[] backgroundLayers;
	// Tiled layers drawn in front of characters, labeled by index
	protected int[] foregroundLayers;

	protected Timer timer;

	protected MapType mapType;

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

	public ArrayList<Character> getCharactersOnMap()
	{
		return charactersOnMap;
	}

	public RectangleMapObject[] getObjectsOnMap()
	{
		return collidables;
	}

	public World getWorld()
	{
		return world;
	}

	public Timer getTimer()
	{
		return timer;
	}

	public Map(World world, SpriteBatch batch, OrthographicCamera camera)
	{
		this.world = world;
		this.batch = batch;
		this.camera = camera;

		timer = new Timer();

		charactersOnMap = new ArrayList<Character>();
		flyingProjectiles = new ArrayList<Projectile>();
	}

	protected void setup()
	{
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

	protected MapObjects getWalkingBoundaries()
	{
		MapLayer walkingBoundariesLayer = tiledMap.getLayers().get("WalkingBoundaries");
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

	protected void genericWeaponSetup(Character character)
	{
		int width = 32;
		int height = 32;

		// melee attack test stuff
		Texture swordSprite = RPG.manager.get(RPG.SWORD_PATH);
		Weapon sword = new MeleeWeapon("LameSword", swordSprite, width, height, 32, 32);
		character.equip(sword);

		// ranged attack test stuff
		Texture bowSprite = RPG.manager.get(RPG.ARROW_PATH);
		RangedWeapon bow = new RangedWeapon("LameBow", bowSprite, width, height, 32, 32);
		character.equip(bow);
		Projectile arrow = new Projectile("arrow", bowSprite, width, height, 32, 32, bow);
		bow.equipProjectile(arrow, 1000);
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
	 * This method has a lot of hardcoding and should be used just for testing.
	 */
	public Rectangle[] getTestPath()
	{
		MapObjects waypointObjects = getWaypoints();
		RectangleMapObject castleWaypoint = (RectangleMapObject) waypointObjects.get("castle");
		RectangleMapObject desertWaypoint = (RectangleMapObject) waypointObjects.get("desert");
		Rectangle[] returnPath = { castleWaypoint.getRectangle(), desertWaypoint.getRectangle() };
		return returnPath;
	}

	public Rectangle getEnclosingWalkingBounds(Rectangle characterBoundingRectangle)
	{
		MapObjects walkingBoundaries = getWalkingBoundaries();
		int count = walkingBoundaries.getCount();
		Rectangle r;
		for (int i = 0; i < count; i++)
		{
			r = ((RectangleMapObject) walkingBoundaries.get(i)).getRectangle();
			if (r.contains(characterBoundingRectangle))
			{
				return r;
			}
		}
		throw new RuntimeException(
				"Cannot find a walking boundary for Rectangle: "
						+ characterBoundingRectangle.toString());
	}

	/**
	 * Draws the background image of the map, followed by all the Characters
	 * that are in view nearby the focusedCharacter
	 */
	public void render()
	{
		Coordinate focusedCoordinate = Coordinate.ZERO;
		if (focusedCharacter != null)
		{
			focusedCoordinate = focusedCharacter.getLocation();
		}

		float viewportWidth = camera.viewportWidth;
		float viewportHeight = camera.viewportHeight;
		float focusX = focusedCoordinate.getX();
		float focusY = focusedCoordinate.getY();
		float bottomLeftX = focusX - viewportWidth / 2;
		float bottomLeftY = focusY - viewportHeight / 2;
		//int width = Math.min(mapWidth, viewportWidth);
		//int height = Math.min(mapHeight, viewportHeight);
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
		//float offsetX = Math.min(something, mapWidth/2);
		//float offsetY = Math.min(something, mapHeight/2);
		
		//int drawnX = (int)bottomLeftX;
		//int drawnY = (mapHeight > viewportHeight ? (mapHeight - viewportHeight) - (int)bottomLeftY : (int)bottomLeftY);
		// NOTE:  There is a little bit of a flip going on here with the Y axis because the Image has (0,0) in the topleft corner, axis down and right
		//		  We want (0,0) in the bottom left corner.  This transformation accomplishes that, but is not fully tested
		//batch.draw(backgroundImage, offsetX, offsetY, drawnX, drawnY, width, height);
		
		camera.position.set(bottomLeftX + viewportWidth / 2, bottomLeftY + viewportHeight / 2, 0);
		//RPG.camera.update();
		tiledMapRenderer.setView(camera);
		//tiledMapRenderer.setView(RPG.camera.combined, 50, 50, width, height);
		tiledMapRenderer.render(backgroundLayers);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		Iterator<Character> iter = charactersOnMap.iterator();
		Rectangle cameraBounds = new Rectangle(bottomLeftX, bottomLeftY,
				viewportWidth, viewportHeight);
		while (iter.hasNext())
		{
			Character selected = iter.next();
			Coordinate selectedLocation = selected.getLocation();
			float selectedX = selectedLocation.getX();
			float selectedY = selectedLocation.getY();
			float charWidth = selected.getSpriteWidth();
			float charHeight = selected.getSpriteHeight();
			selected.setPosition(selectedX - charWidth / 2, selectedY
					- charHeight / 2);
			// TODO this calculation is not quite right for characters on the
			// edge of what is being drawn on the map
			if (selected.getSprite().getBoundingRectangle()
					.overlaps(cameraBounds))
			{
				selected.render(batch);
			}
		}
		
		Iterator<Projectile> it = flyingProjectiles.iterator();
		while (it.hasNext())
		{
			Projectile p = it.next();
			if (p.isFinished() || !p.isFired())
			{
				it.remove();
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
		Iterator<Character> iter = charactersOnMap.iterator();
		while (iter.hasNext())
		{
			Character selected = iter.next();
			selected.update(deltaTime, this);
		}
		Iterator<Projectile> it = flyingProjectiles.iterator();
		while (it.hasNext())
		{
			it.next().update(deltaTime);
		}
	}

	public boolean checkCollision(float x, float y, float oldX, float oldY,
			float width, float height, Character thisCharacter, Coordinate newCoordinate)
	{
		RectangleMapObject[] objectsOnMap = getObjectsOnMap();
		ArrayList<Character> charactersOnMap = getCharactersOnMap();

		Rectangle boundingBox = new Rectangle(x, y, width, height);
		Rectangle boundingBoxWithNewY = new Rectangle(oldX, y, width, height);
		Rectangle boundingBoxWithNewX = new Rectangle(x, oldY, width, height);
		newCoordinate.setX(x);
		newCoordinate.setY(y);

		// collision detection with objects on map
		for (RectangleMapObject object : objectsOnMap)
		{
			Rectangle r = object.getRectangle();
			if (r.overlaps(boundingBox))
			{
				if (r.overlaps(boundingBoxWithNewY))
				{
					newCoordinate.setY(oldY);
				}
				if (r.overlaps(boundingBoxWithNewX))
				{
					newCoordinate.setX(oldX);
				}
			}
		}

		// collision detection with characters
		Iterator<Character> iter = charactersOnMap.iterator();
		while (iter.hasNext())
		{
			Character selected = iter.next();
			if (selected.equals(thisCharacter))
			{
				continue;
			}
			Coordinate location = selected.getLocation();
			float tempWidth = selected.getSpriteWidth();
			float tempHeight = selected.getSpriteHeight();
			float tempX = location.getX() - tempWidth / 2;
			float tempY = location.getY() - tempHeight / 2;
			Rectangle r = new Rectangle(tempX, tempY, tempWidth, tempHeight);
			if (r.overlaps(boundingBox))
			{
				if (r.overlaps(boundingBoxWithNewY))
				{
					newCoordinate.setY(oldY);
				}
				if (r.overlaps(boundingBoxWithNewX))
				{
					newCoordinate.setX(oldX);
				}
			}
		}

		return !(newCoordinate.getX() == oldX && newCoordinate.getY() == oldY);
	}

	/**
	 * A collision check that only checks characters. For example, you can check
	 * if a weapon's hitbox collided with any characters without having to worry
	 * about other objects on the map.
	 * Returns a reference to a Character object if the input hitbox collides
	 * with the Character's hitbox.
	 * Returns null otherwise.
	 */
	public Character checkCharacterCollision(Rectangle hitbox, Character thisCharacter)
	{
		ArrayList<Character> charactersOnMap = getCharactersOnMap();
		Iterator<Character> iter = charactersOnMap.iterator();
		while (iter.hasNext())
		{
			Character selected = iter.next();
			if (selected.equals(thisCharacter))
			{
				continue;
			}
			Coordinate location = selected.getLocation();
			float tempWidth = selected.getSpriteWidth();
			float tempHeight = selected.getSpriteHeight();
			float tempX = location.getX() - tempWidth / 2;
			float tempY = location.getY() - tempHeight / 2;
			Rectangle r = new Rectangle(tempX, tempY, tempWidth, tempHeight);
			if (hitbox.overlaps(r))
			{
				return selected;
			}
		}
		return null;
	}

	/**
	 * A collision check that only checks warp points.
	 * Returns a reference to a Map object if the input hitbox collides with a
	 * warp point.
	 * Returns null otherwise.
	 */
	public Map checkWarpPointCollision(Rectangle hitbox)
	{
		Vector2 centerPoint = hitbox.getCenter(new Vector2());
		for (RectangleMapObject warpPoint : warpPoints)
		{
			if (warpPoint.getRectangle().contains(centerPoint))
			{
				String newMapString = warpPoint.getName();
				return getMap(MapType.getMapType(newMapString));
			}
		}
		return null;
	}

	public Map getMap(MapType mapType)
	{
		switch (mapType)
		{
		case EXAMPLE:
			return new ExampleMap(world, batch, camera);
		case PROTOTYPE:
			return new PrototypeMap(world, batch, camera);
		}
		throw new RuntimeException("Cannot get Map from the given MapType");
	}

	/*
	 * ADDING/REMOVING CHARACTERS
	 */

	public void removeCharacterFromMap(Character removeCharacter)
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

	public void addCharacterToMap(Character newCharacter, Coordinate newLocation)
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
							+ newCharacter.getLocation()
							+ " "
							+ newCharacter
							+ " " + newLocation);
		}
		newCharacter.setLocation(newLocation);
		charactersOnMap.add(newCharacter);
		newCharacter.addedToMap(this);
	}

	public void setFocusedCharacter(Character newFocus)
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

	public void addCharacterToMap(Character newCharacter, float x, float y)
	{
		Coordinate newLocation = new Coordinate(x, y);
		addCharacterToMap(newCharacter, newLocation);
	}

	public void addFocusedCharacterToMap(Character newFocusedCharacter,
			float x, float y)
	{
		addFocusedCharacterToMap(newFocusedCharacter, new Coordinate(x, y));
	}

	public void addFocusedCharacterToMap(Character newFocusedCharacter,
			Coordinate newLocation)
	{
		addCharacterToMap(newFocusedCharacter, newLocation);
		setFocusedCharacter(newFocusedCharacter);
	}

	/*
	 * END ADDING/REMOVING CHARACTERS
	 */

	public boolean isCharacterOnMap(Character character)
	{
		return charactersOnMap.contains(character);
	}

	public void addProjectile(Projectile p)
	{
		flyingProjectiles.add(p);
	}

	@Override
	public void dispose()
	{
		tiledMapRenderer.dispose();
	}

}
