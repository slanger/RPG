package com.me.rpg;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.combat.Weapon;

public abstract class Character
{
	
	private String name;
	private Sprite sprite;
	private Coordinate location;
	private TextureRegion rightIdle, leftIdle, upIdle, downIdle;
	private Animation rightWalkAnimation, leftWalkAnimation, upWalkAnimation, downWalkAnimation;
	private Direction direction = Direction.DOWN;
	private boolean moving = false;
	private float stateTime = 0f;
	private float speed = 100f;
	
	// Combat stuff
	protected Weapon weaponSlot;
	
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
	
	protected String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	protected Coordinate getLocation() {
		return location;
	}

	protected void setLocation(Coordinate location) {
		this.location = location;
	}

	protected Direction getDirection() {
		return direction;
	}

	protected void setDirection(Direction direction) {
		this.direction = direction;
	}

	protected boolean isMoving() {
		return moving;
	}

	protected void setMoving(boolean moving) {
		this.moving = moving;
	}

	protected float getStateTime() {
		return stateTime;
	}

	protected void addToStateTime(float deltaTime) {
		this.stateTime += deltaTime;
	}

	protected Sprite getSprite() {
		return sprite;
	}

	protected TextureRegion getRightIdle() {
		return rightIdle;
	}

	protected TextureRegion getLeftIdle() {
		return leftIdle;
	}

	protected TextureRegion getUpIdle() {
		return upIdle;
	}

	protected TextureRegion getDownIdle() {
		return downIdle;
	}

	protected Animation getRightWalkAnimation() {
		return rightWalkAnimation;
	}

	protected Animation getLeftWalkAnimation() {
		return leftWalkAnimation;
	}

	protected Animation getUpWalkAnimation() {
		return upWalkAnimation;
	}

	protected Animation getDownWalkAnimation() {
		return downWalkAnimation;
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
	
	public void setPosition(float x, float y)
	{
		sprite.setPosition(x, y);
	}
	
	public void render(SpriteBatch batch)
	{
		sprite.draw(batch);
		if (weaponSlot != null)
			weaponSlot.render(sprite.getBoundingRectangle(), getDirection(), batch);
	}
	
	/**
	 * Will modify the currentLocation object to have the new location
	 * @param deltaTime As reported by Graphics.getDeltaTime()
	 * @param currentLocation The location of this Character on the current map
	 * @param mapWidth The map width of the current map
	 * @param mapHeight The map height of the current map
	 */
	public abstract void update(float deltaTime, Map currentMap);
	
	public Coordinate checkCollision(float x, float y, float oldX, float oldY, float width, float height, RectangleMapObject[] objectsOnMap, ArrayList<Character> charactersOnMap)
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
		Iterator<Character> iter = charactersOnMap.iterator();
		while (iter.hasNext())
		{
			Character selected = iter.next();
			if (selected.equals(this))
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
	 * May be useful with debugging. Likely will be out of date if Character class is updated.
	 */
	@Override
	public String toString() {
		return String.format("(CharacterToString){name:%s, sprite:%s, direction:%s, moving=%s, stateTime=%lf}", name, sprite, direction, moving, stateTime);
	}

	/**
	 * Likely insufficient implementation of equip
	 * @param sword
	 */
	public void equip(Weapon weapon) {
		this.weaponSlot = weapon;
	}
	
}
