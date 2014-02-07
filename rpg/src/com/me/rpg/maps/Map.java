package com.me.rpg.maps;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.me.rpg.Character;
import com.me.rpg.Coordinate;
import com.me.rpg.RPG;
import com.me.rpg.World;
import com.me.rpg.combat.MeleeWeapon;
import com.me.rpg.combat.Poison;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.RangedWeapon;
import com.me.rpg.combat.Shield;
import com.me.rpg.combat.StatusEffect;
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
	protected ArrayList<Weapon> equippedWeapons;

	protected RectangleMapObject[] collidables;
	protected RectangleMapObject[] warpPoints;

	// Tiled map variables
	protected TiledMap tiledMap;
	protected OrthogonalTiledMapRenderer tiledMapRenderer;

	// Tiled layers drawn behind characters, labeled by index
	protected int[] backgroundLayers;
	// Tiled layers drawn in front of characters, labeled by index
	protected int[] foregroundLayers;

	private boolean updateEnable = true;
	private boolean enableCameraSwitch = false;
	private boolean cameraPan = false;
	private float oldCameraZoom = 0f;

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

	public boolean isUpdating()
	{
		return updateEnable;
	}

	public void setUpdateEnable(boolean updateEnable)
	{
		this.updateEnable = updateEnable;
	}

	public Map(World world, SpriteBatch batch, OrthographicCamera camera)
	{
		this.world = world;
		this.batch = batch;
		this.camera = camera;

		charactersOnMap = new ArrayList<Character>();
		flyingProjectiles = new ArrayList<Projectile>();
		equippedWeapons = new ArrayList<Weapon>();
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

	protected void genericWeaponSetup(Character character, Character npc)
	{
		int width = 32;
		int height = 32;

		// melee attack test stuff
		Texture swordSprite = RPG.manager.get(RPG.SWORD_PATH);
		Weapon sword = new MeleeWeapon("LameSword");
		sword.initSprite(swordSprite, width, height, 32, 32);
		StatusEffect poison = new Poison(7, 3, 4f);
		sword.addEffect(poison);
		
		character.equip(this, sword);
		character.swapWeapon(this);

		// ranged attack test stuff
		Texture bowSprite = RPG.manager.get(RPG.ARROW_PATH);
		RangedWeapon bow = new RangedWeapon("LameBow");
		bow.initSprite(bowSprite, width, height, 32, 32);
		
		character.equip(this, bow);
		Projectile arrow = new Projectile("arrow", bowSprite, width, height, 32, 32);
		bow.equipProjectile(arrow, 1000);
		
		Shield shield = new Shield("plain shield");
		npc.equipShield(shield);
	}

	private void cameraPanMovement() {
        if(Gdx.input.isKeyPressed(Keys.A)) {
            camera.zoom += 0.02;
        }
        if(Gdx.input.isKeyPressed(Keys.Q)) {
        	camera.zoom -= 0.02;
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            if (camera.position.x > 0)
            	camera.translate(-3, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            if (camera.position.x < mapWidth)
            	camera.translate(3, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            if (camera.position.y > 0)
            	camera.translate(0, -3, 0);
        }
        if(Gdx.input.isKeyPressed(Keys.UP)) {
            if (camera.position.y < mapHeight)
            	camera.translate(0, 3, 0);
        }
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
		
		if (!cameraPan)
		{
			camera.position.set(bottomLeftX + viewportWidth / 2, bottomLeftY + viewportHeight / 2, 0);
		}
		
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render(backgroundLayers);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		Iterator<Character> iter = charactersOnMap.iterator();
		Rectangle cameraBounds = new Rectangle(camera.position.x - camera.zoom*viewportWidth/2, camera.position.y - camera.zoom*viewportHeight/2,
				camera.zoom*viewportWidth, camera.zoom*viewportHeight);
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
			if (selected.getHitBox().overlaps(cameraBounds))
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
		if (Gdx.input.isKeyPressed(Keys.C))
		{
			if (enableCameraSwitch) {
				 enableCameraSwitch = false;
				 cameraPan = !cameraPan;
				 if (cameraPan)
				 {
					 oldCameraZoom = camera.zoom;
				 }
				 else
				 {
					 camera.zoom = oldCameraZoom;
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
		
		if (!updateEnable)
		{
			return;
		}
		
		// check intersections with weapons
		Iterator<Weapon> weaponIter = equippedWeapons.iterator();
		Iterator<Character> charIter;
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
			while (charIter.hasNext()) {
				Character c = charIter.next();
				if (c.equals(w.getOwner()))
					continue;
				if (weaponBox.overlaps(c.getHitBox())) {
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
			while (charIter.hasNext()) {
				Character c = charIter.next();
				if (c.equals(p.getFiredWeapon().getOwner()))
					continue;
				if (projectileBox.overlaps(c.getHitBox())) {
					c.receiveAttack(p);
					p.setHasHit();
				}
			}
		}
		
		// move characters, projectiles
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

	public void removeCharacterFromMap(Character removeCharacter)
	{
		if (!characterOnMap(removeCharacter))
		{
			throw new RuntimeException(
					"Character cannot be removed from map - it is not on the map. Data: "
							+ removeCharacter);
		}
		charactersOnMap.remove(removeCharacter);
		if (focusedCharacter.equals(removeCharacter))
		{
			focusedCharacter = null;
		}
	}

	public void addCharacterToMap(Character newCharacter, float x, float y)
	{
		Coordinate newLocation = new Coordinate(x, y);
		addCharacterToMap(newCharacter, newLocation);
	}

	public void addCharacterToMap(Character newCharacter, Coordinate newLocation)
	{
		if (newLocation == null)
		{
			throw new RuntimeException(
					"Cannot add the Character to the map - null location passed in. Data: "
							+ newCharacter);
		}
		if (characterOnMap(newCharacter))
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
	}

	public void setFocusedCharacter(Character newFocus)
	{
		if (!characterOnMap(newFocus))
		{
			throw new RuntimeException(
					"Cannot set character to be focus if it is not already on the map. Data: "
							+ newFocus);
		}
		focusedCharacter = newFocus;
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

	public boolean characterOnMap(Character character)
	{
		return charactersOnMap.contains(character);
	}
	
	public void addProjectile(Projectile p) {
		flyingProjectiles.add(p);
	}
	
	public void addEquippedWeapon(Weapon w) {
		equippedWeapons.add(w);
	}
	
	public void removeEquippedWeapon(Weapon w) {
		equippedWeapons.remove(w);
	}

	@Override
	public void dispose()
	{
		tiledMapRenderer.dispose();
	}

}
