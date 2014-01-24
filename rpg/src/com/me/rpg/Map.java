package com.me.rpg;

import java.util.ArrayList;
import java.util.Iterator;

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
	
	private Character focusedCharacter;
	private int mapWidth;
	private int mapHeight;
	private final ArrayList<Character> charactersOnMap;
	private RectangleMapObject[] objectsOnMap;
	
	// split the map into grid spaces to help with collision and context-sensitive actions
	//private int gridSpaceWidth;
	//private int gridSpaceHeight;
	//private int gridWidth; // in number of grid spaces
	//private int gridHeight; // in number of grid spaces
	
	// Tiled map variables
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer tiledMapRenderer;
	
	private final int[] backgroundLayers = new int[] { 0, 1 }; // Tiled layers drawn behind characters
	private final int[] foregroundLayers = new int[] { 2 }; // Tiled layers drawn in front of characters
	
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
		return objectsOnMap;
	}
	
	public Map(TiledMap tiledMap, SpriteBatch batch)
	{
		this.tiledMap = tiledMap;
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
		
		// get map width and height from .tmx file
		MapProperties mapProperties = tiledMap.getProperties();
		int tileWidth = (Integer) mapProperties.get("tilewidth");
		int tileHeight = (Integer) mapProperties.get("tileheight");
		mapWidth = ((Integer) mapProperties.get("width")) * tileWidth;
		mapHeight = ((Integer) mapProperties.get("height")) * tileHeight;
		
		// make grid in terms of map's tile width and tile height
		//gridSpaceWidth = tileWidth;
		//gridSpaceHeight = tileHeight;
		//gridWidth = mapWidth / gridSpaceWidth;
		//gridHeight = mapHeight / gridSpaceHeight;
		
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
	 * Draws the background image of the map, followed by all the Characters that are in view nearby the focusedCharacter
	 * 
	 * Note: This may be a little too tightly coupled to some of the rendering objects/info.
	 * 
	 * @param batch The spriteBatch being used in the main loop to render to the screen
	 * @param viewportWidth The width of the camera
	 * @param viewportHeight The height of the camera
	 */
	public void render(SpriteBatch batch, int viewportWidth, int viewportHeight)
	{
		Coordinate focusedCoordinate = Coordinate.ZERO;
		if (focusedCharacter != null)
			focusedCoordinate = focusedCharacter.getLocation();
		
		float focusX = focusedCoordinate.getX();
		float focusY = focusedCoordinate.getY();
		float bottomLeftX = focusX - viewportWidth / 2;
		float bottomLeftY = focusY - viewportHeight / 2;
		//int width = Math.min(mapWidth, viewportWidth);
		//int height = Math.min(mapHeight, viewportHeight);
		
		if (bottomLeftX > mapWidth - viewportWidth) {
			bottomLeftX = mapWidth - viewportWidth;
		}
		if (bottomLeftX < 0) {
			bottomLeftX = 0;
		}
		
		if (bottomLeftY > mapHeight - viewportHeight) {
			bottomLeftY = mapHeight - viewportHeight;
		}
		if (bottomLeftY < 0) {
			bottomLeftY = 0;
		}
		
		//float offsetX = Math.min(something, mapWidth/2);
		//float offsetY = Math.min(something, mapHeight/2);
		
		//int drawnX = (int)bottomLeftX;
		//int drawnY = (mapHeight > viewportHeight ? (mapHeight - viewportHeight) - (int)bottomLeftY : (int)bottomLeftY);
		// NOTE:  There is a little bit of a flip going on here with the Y axis because the Image has (0,0) in the topleft corner, axis down and right
		//		  We want (0,0) in the bottom left corner.  This transformation accomplishes that, but is not fully tested
		//batch.draw(backgroundImage, offsetX, offsetY, drawnX, drawnY, width, height);
		
		RPG.camera.position.set(focusX, focusY, 0);
		//RPG.camera.update();
		tiledMapRenderer.setView(RPG.camera);
		//tiledMapRenderer.setView(RPG.camera.combined, 50, 50, width, height);
		tiledMapRenderer.render(backgroundLayers);
		
		batch.setProjectionMatrix(RPG.camera.combined);
		batch.begin();
		
		//System.err.printf("bottomLeftX=%f, bottomLeftRight=%f.  charLoc=%s\n", bottomLeftX, bottomLeftY, focusedCoordinate);
		Iterator<Character> iter = charactersOnMap.iterator();
		Rectangle cameraBounds = new Rectangle(focusX - viewportWidth / 2, focusY - viewportHeight / 2, viewportWidth, viewportHeight);
		while (iter.hasNext()) {
			Character selected = iter.next();
			Coordinate selectedLocation = selected.getLocation();
			float selectedX = selectedLocation.getX();
			float selectedY = selectedLocation.getY();
			float charWidth = selected.getSpriteWidth();
			float charHeight = selected.getSpriteHeight();
			selected.setPosition(selectedX - charWidth / 2, selectedY - charHeight / 2);
			// TODO this calculation is not quite right for characters on the edge of what is being drawn on the map
			if (selected.getSprite().getBoundingRectangle().overlaps(cameraBounds)) {
				selected.render(batch);
			}
		}
		
		batch.end();
		
		tiledMapRenderer.render(foregroundLayers);
	}
	
	public void update(float deltaTime) {
		Iterator<Character> iter = charactersOnMap.iterator();
		while (iter.hasNext()) {
			Character selected = iter.next();
			selected.update(deltaTime, this);
		}
	}
	
	public void removeCharacterFromMap(Character removeCharacter) {
		if (!characterOnMap(removeCharacter)) {
			throw new RuntimeException("Character cannot be removed from map - it is not on the map. Data: " + removeCharacter);
		}
		charactersOnMap.remove(removeCharacter);
		if (focusedCharacter.equals(removeCharacter)) {
			focusedCharacter = null;
		}
	}
	

	public void addCharacterToMap(Character newCharacter, float x, float y) {
		Coordinate newLocation = new Coordinate(x, y);
		addCharacterToMap(newCharacter, newLocation);
	}
	
	public void addCharacterToMap(Character newCharacter, Coordinate newLocation) {
		if (newLocation == null) {
			throw new RuntimeException("Cannot add the Character to the map - null location passed in. Data: " + newCharacter);
		}
		if (characterOnMap(newCharacter)) {
			throw new RuntimeException("Cannot add the Character to the map - it is already on the map. Data: OldLoc: " + newCharacter.getLocation() + " " + newCharacter + " " + newLocation);
		}
		newCharacter.setLocation(newLocation);
		charactersOnMap.add(newCharacter);
	}
	
	public void setFocusedCharacter(Character newFocus) {
		if (!characterOnMap(newFocus)) {
			throw new RuntimeException("Cannot set character to be focus if it is not already on the map. Data: " + newFocus);
		}
		focusedCharacter = newFocus;
	}
	
	public void addFocusedCharacterToMap(Character newFocusedCharacter, float x, float y)
	{
		addFocusedCharacterToMap(newFocusedCharacter, new Coordinate(x, y));
	}
	
	public void addFocusedCharacterToMap(Character newFocusedCharacter, Coordinate newLocation)
	{
		addCharacterToMap(newFocusedCharacter, newLocation);
		setFocusedCharacter(newFocusedCharacter);
	}
	
	public boolean characterOnMap(Character character) {
		return charactersOnMap.contains(character);
	}

	@Override
	public void dispose() {
		tiledMapRenderer.dispose();
	}
	
}
