package com.me.rpg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Map {
	
	private Texture backgroundImage;
	private Character focusedCharacter;
	private Coordinate focusedCoordinate;
	private int mapWidth;
	private int mapHeight;
	private final HashMap<Character, Coordinate> charactersOnMap;
	
	public Map(Character focusedCharacter, Coordinate focusedCoordinate, Texture backgroundImage) {
		this.focusedCharacter = focusedCharacter;
		this.focusedCoordinate = focusedCoordinate;
		this.backgroundImage = backgroundImage;
		mapWidth = backgroundImage.getWidth();
		mapHeight = backgroundImage.getHeight();
		charactersOnMap = new HashMap<>();
		addCharacterToMap(focusedCharacter, focusedCoordinate);
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
	public void render(SpriteBatch batch, int viewportWidth, int viewportHeight) {
		float focusX = focusedCoordinate.getX();
		float focusY = focusedCoordinate.getY();
		float bottomLeftX = focusX - viewportWidth/2;
		float bottomLeftY = focusY - viewportHeight/2;
		
		// TODO below calculations are valid only if image is bigger than the viewport.
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
		// end disclaimer
		
		// NOTE:  There is a little bit of a flip going on here with the Y axis because the Image has (0,0) in the topleft corner, axis down and right
		//		  We want (0,0) in the bottom left corner.  This transformation accomplishes that, but is not fully tested
		batch.draw(backgroundImage, 0, 0, (int)bottomLeftX, (mapHeight - viewportHeight) - (int)bottomLeftY, viewportWidth, viewportHeight);
		//System.err.printf("bottomLeftX=%f, bottomLeftRight=%f.  charLoc=%s\n", bottomLeftX, bottomLeftY, focusedCoordinate);
		Iterator<Entry<Character, Coordinate>> iter = charactersOnMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Character, Coordinate> entry = iter.next();
			Character selected = entry.getKey();
			Coordinate selectedLocation = entry.getValue();
			float selectedX = selectedLocation.getX();
			float selectedY = selectedLocation.getY();
			// TODO this calculation is not quite right for characters on the edge of what is being drawn as the map.
			if (bottomLeftX <= selectedX && selectedX <= (bottomLeftX + viewportWidth) && bottomLeftY <= selectedY && selectedY <= (bottomLeftY + viewportHeight)) {
				selected.setPosition(selectedX - bottomLeftX, selectedY - bottomLeftY);
				selected.render(batch);
			}
		}
	}
	
	public void update(float deltaTime) {
		Iterator<Entry<Character, Coordinate>> iter = charactersOnMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Character, Coordinate> entry = iter.next();
			Character selected = entry.getKey();
			Coordinate location = entry.getValue();
			selected.update(deltaTime, location, mapWidth, mapHeight);
		}
	}
	
	public void removeCharacterFromMap(Character removeCharacter) {
		Coordinate oldLocation = charactersOnMap.get(removeCharacter);
		if (oldLocation == null) {
			throw new RuntimeException("Character cannot be removed from map - it is not on the map. Data:" + removeCharacter);
		}
		charactersOnMap.remove(removeCharacter);
		if (focusedCharacter == removeCharacter) {
			focusedCharacter = null;
			focusedCoordinate = Coordinate.ZERO;
		}
	}
	

	public void addCharacterToMap(Character newCharacter, int x, int y) {
		Coordinate newLocation = new Coordinate(x, y);
		addCharacterToMap(newCharacter, newLocation);
	}
	
	public void addCharacterToMap(Character newCharacter, Coordinate newLocation) {
		Coordinate oldLocation = charactersOnMap.get(newCharacter);
		if (newLocation == null) {
			throw new RuntimeException("Cannot add the Character to the map - null location passed in.  Data: " + newCharacter);
		}
		if (oldLocation != null) {
			throw new RuntimeException("Cannot add the Character to the map - it is already on the map. Data: OldLoc:" + oldLocation + " " + newCharacter + " " + newLocation);
		}
		charactersOnMap.put(newCharacter, newLocation);
	}
	
	public void setFocusedCharacter(Character newFocus) {
		Coordinate oldLocation = charactersOnMap.get(newFocus);
		if (oldLocation == null) {
			throw new RuntimeException("Cannot set character to be focus if it is not already on the map.  Data: " + newFocus);
		}
		focusedCharacter = newFocus;
		focusedCoordinate = oldLocation;
	}
	
}
