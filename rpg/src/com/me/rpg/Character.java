package com.me.rpg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

public abstract class Character
{
	
	protected String name;
	protected Sprite sprite;
	protected TextureRegion rightIdle, leftIdle, upIdle, downIdle;
	protected Animation rightWalkAnimation, leftWalkAnimation, upWalkAnimation, downWalkAnimation;
	protected Direction direction = Direction.DOWN;
	protected boolean moving = false;
	protected float stateTime = 0f;
	protected float speed = 100f;
	
	protected enum Direction
	{
		RIGHT	(0,  1,  0),
		LEFT	(1, -1,  0),
		UP		(2,  0,  1),
		DOWN	(3,  0, -1);
		
		private int index, dx, dy;
		
		private Direction(int index, int dx, int dy)
		{
			this.index = index;
			this.dx = dx;
			this.dy = dy;
		}
		
		public int getIndex()
		{
			return index;
		}
		
		public int getDx()
		{
			return dx;
		}
		
		public int getDy()
		{
			return dy;
		}
		
		public static Direction getDirection(int index)
		{
			for (Direction d : Direction.values())
			{
				if (d.getIndex() == index)
				{
					return d;
				}
			}
			throw new RuntimeException("Could not find the Direction with index of " + index);
		}
	}
	
	protected Character(String name, Texture spritesheet, int width, int height, int tileWidth, int tileHeight, float animationDuration)
	{
		TextureRegion[][] sheet = TextureRegion.split(spritesheet, tileWidth, tileHeight);
		int columns = sheet[0].length;
		TextureRegion[] rightWalkFrames = new TextureRegion[columns];
		TextureRegion[] leftWalkFrames = new TextureRegion[columns];
		TextureRegion[] upWalkFrames = new TextureRegion[columns];
		TextureRegion[] downWalkFrames = new TextureRegion[columns];
		for (int i = 0; i < columns; i++)
		{
			rightWalkFrames[i] = sheet[Direction.RIGHT.getIndex()][i];
			leftWalkFrames[i] = sheet[Direction.LEFT.getIndex()][i];
			upWalkFrames[i] = sheet[Direction.UP.getIndex()][i];
			downWalkFrames[i] = sheet[Direction.DOWN.getIndex()][i];
		}
		rightWalkAnimation = new Animation(animationDuration, rightWalkFrames);
		leftWalkAnimation = new Animation(animationDuration, leftWalkFrames);
		upWalkAnimation = new Animation(animationDuration, upWalkFrames);
		downWalkAnimation = new Animation(animationDuration, downWalkFrames);
		rightIdle = sheet[Direction.RIGHT.getIndex()][0];
		leftIdle = sheet[Direction.LEFT.getIndex()][0];
		upIdle = sheet[Direction.UP.getIndex()][0];
		downIdle = sheet[Direction.DOWN.getIndex()][0];
		// start sprite facing downward
		sprite = new Sprite(downIdle, 0, 0, width, height);
	}
	
	public void setPosition(float x, float y)
	{
		sprite.setPosition(x, y);
	}
	
	public void render(SpriteBatch batch)
	{
		sprite.draw(batch);
	}
	
	/**
	 * Will modify the currentLocation object to have the new location
	 * @param deltaTime As reported by Graphics.getDeltaTime()
	 * @param currentLocation The location of this Character on the current map
	 * @param mapWidth The map width of the current map
	 * @param mapHeight The map height of the current map
	 */
	public abstract void update(float deltaTime, Map currentMap, Coordinate currentLocation);
	
	public Coordinate checkCollision(float x, float y, float oldX, float oldY, float width, float height, RectangleMapObject[] objectsOnMap, HashMap<Character, Coordinate> charactersOnMap)
	{
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
		Iterator<Entry<Character, Coordinate>> iter = charactersOnMap.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<Character, Coordinate> entry = iter.next();
			Character selected = entry.getKey();
			if (selected.equals(this))
			{
				continue;
			}
			Coordinate location = entry.getValue();
			Rectangle r = new Rectangle(location.getX() - selected.getSpriteWidth()/2, location.getY() - selected.getSpriteHeight()/2, selected.getSpriteWidth(), selected.getSpriteHeight());
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
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float newSpeed) {
		this.speed = newSpeed;
	}
	
	public float getSpriteWidth() {
		return sprite.getWidth();
	}
	
	public float getSpriteHeight() {
		return sprite.getHeight();
	}
	
	/**
	 * May be useful with debugging. Likely will be out of date if Character class is updated.
	 */
	@Override
	public String toString() {
		return String.format("(CharacterToString){name:%s, sprite:%s, direction:%s, moving=%s, stateTime=%lf}", name, sprite, direction, moving, stateTime);
	}
	
}
