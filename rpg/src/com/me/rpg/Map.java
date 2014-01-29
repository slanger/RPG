package com.me.rpg;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Map implements Disposable
{

	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Character focusedCharacter;
	private int mapWidth;
	private int mapHeight;
	private static ArrayList<Character> charactersOnMap;
	private static RectangleMapObject[] objectsOnMap;

	// Tiled map variables
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer tiledMapRenderer;

	// Tiled layers drawn behind characters, labeled by index
	private final int[] backgroundLayers = new int[] { 0, 1 };
	// Tiled layers drawn in front of characters, labeled by index
	private final int[] foregroundLayers = new int[] { 2 };

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

	public static ArrayList<Character> getCharactersOnMap()
	{
		return charactersOnMap;
	}

	public static RectangleMapObject[] getObjectsOnMap()
	{
		return objectsOnMap;
	}

	public Map(SpriteBatch batch, OrthographicCamera camera, TiledMap tiledMap)
	{
		this.batch = batch;
		this.camera = camera;
		this.tiledMap = tiledMap;

		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);

		// get map width and height from .tmx file
		MapProperties mapProperties = tiledMap.getProperties();
		int tileWidth = (Integer) mapProperties.get("tilewidth");
		int tileHeight = (Integer) mapProperties.get("tileheight");
		mapWidth = ((Integer) mapProperties.get("width")) * tileWidth;
		mapHeight = ((Integer) mapProperties.get("height")) * tileHeight;

		charactersOnMap = new ArrayList<Character>();

		// get collision objects
		MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
		MapObjects mapObjects = collisionLayer.getObjects();
		objectsOnMap = new RectangleMapObject[mapObjects.getCount()];
		for (int i = 0; i < objectsOnMap.length; i++)
		{
			objectsOnMap[i] = (RectangleMapObject) mapObjects.get(i);
		}
	}

	/**
	 * Draws the background image of the map, followed by all the Characters
	 * that are in view nearby the focusedCharacter
	 * 
	 * Note: This may be a little too tightly coupled to some of the rendering
	 * objects/info.
	 * 
	 * @param batch
	 *            The spriteBatch being used in the main loop to render to the
	 *            screen
	 * @param viewportWidth
	 *            The width of the camera
	 * @param viewportHeight
	 *            The height of the camera
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
		// int width = Math.min(mapWidth, viewportWidth);
		// int height = Math.min(mapHeight, viewportHeight);

		if (bottomLeftX > mapWidth - viewportWidth)
		{
			bottomLeftX = mapWidth - viewportWidth;
		}
		if (bottomLeftX < 0)
		{
			bottomLeftX = 0;
		}

		if (bottomLeftY > mapHeight - viewportHeight)
		{
			bottomLeftY = mapHeight - viewportHeight;
		}
		if (bottomLeftY < 0)
		{
			bottomLeftY = 0;
		}

		// float offsetX = Math.min(something, mapWidth/2);
		// float offsetY = Math.min(something, mapHeight/2);

		// int drawnX = (int)bottomLeftX;
		// int drawnY = (mapHeight > viewportHeight ? (mapHeight -
		// viewportHeight) - (int)bottomLeftY : (int)bottomLeftY);
		// NOTE: There is a little bit of a flip going on here with the Y axis
		// because the Image has (0,0) in the topleft corner, axis down and
		// right
		// We want (0,0) in the bottom left corner. This transformation
		// accomplishes that, but is not fully tested
		// batch.draw(backgroundImage, offsetX, offsetY, drawnX, drawnY, width, height);

		camera.position.set(focusX, focusY, 0);
		// camera.update();
		tiledMapRenderer.setView(camera);
		// tiledMapRenderer.setView(camera.combined, 50, 50, width, height);
		tiledMapRenderer.render(backgroundLayers);

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		Iterator<Character> iter = charactersOnMap.iterator();
		Rectangle cameraBounds = new Rectangle(focusX - viewportWidth / 2,
				focusY - viewportHeight / 2, viewportWidth, viewportHeight);
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
	}

	public static Coordinate checkCollision(float x, float y, float oldX, float oldY,
			float width, float height, Character thisCharacter)
	{
		RectangleMapObject[] objectsOnMap = getObjectsOnMap();
		ArrayList<Character> charactersOnMap = getCharactersOnMap();

		Rectangle boundingBox = new Rectangle(x, y, width, height);
		Rectangle boundingBoxWithNewY = new Rectangle(oldX, y, width, height);
		Rectangle boundingBoxWithNewX = new Rectangle(x, oldY, width, height);
		Coordinate returnCoordinate = new Coordinate(x, y);

		// collision detection with objects on map
		for (RectangleMapObject object : objectsOnMap)
		{
			Rectangle r = object.getRectangle();
			if (r.overlaps(boundingBox))
			{
				if (r.overlaps(boundingBoxWithNewY))
				{
					returnCoordinate.setY(oldY);
				}
				if (r.overlaps(boundingBoxWithNewX))
				{
					returnCoordinate.setX(oldX);
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
					returnCoordinate.setY(oldY);
				}
				if (r.overlaps(boundingBoxWithNewX))
				{
					returnCoordinate.setX(oldX);
				}
			}
		}

		return returnCoordinate;
	}

	/**
	 * A collision check that only checks characters. For example, you can check
	 * if a weapon's hitbox collided with any characters without having to worry
	 * about other objects on the map. Returns a reference to a Character object
	 * if the input hitbox collides with the Character's hitbox. Returns null
	 * otherwise
	 */
	public static Character checkCharacterCollision(Rectangle hitbox, Character thisCharacter)
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

	@Override
	public void dispose()
	{
		tiledMapRenderer.dispose();
	}

}
