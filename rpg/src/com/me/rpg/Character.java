package com.me.rpg;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

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
		RIGHT	(0),
		LEFT	(1),
		UP		(2),
		DOWN	(3);
		
		int index;
		
		Direction(int index)
		{
			this.index = index;
		}
		
		public int getIndex()
		{
			return index;
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
	
	protected Character(String name, Texture spritesheet, int width, int height, int tileWidth, int tileHeight, int startX, int startY, float animationDuration)
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
		sprite.setPosition(startX, startY);
	}
	
	public void setPosition(float x, float y) {
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
	public abstract void update(float deltaTime, Coordinate currentLocation, int mapWidth, int mapHeight, RectangleMapObject[] objects, HashMap<Character, Coordinate> characters);
	
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
