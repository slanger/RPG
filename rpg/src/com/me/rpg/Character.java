package com.me.rpg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.StandStillAI;
import com.me.rpg.ai.WalkAI;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.Map;

public abstract class Character
{

	private String name;
	private Sprite sprite;
	private Coordinate location;
	private TextureRegion rightIdle, leftIdle, upIdle, downIdle;
	private Animation rightWalkAnimation, leftWalkAnimation, upWalkAnimation,
			downWalkAnimation;
	private Direction direction = Direction.DOWN;
	private boolean moving = false;
	private float stateTime = 0f;
	private float speed = 100f;

	protected WalkAI walkAI;
	protected Map currentMap = null;
	
	// Combat stuff
	protected Weapon weaponSlot;
	protected Weapon weaponSlotExtra;

	protected Character(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration)
	{
		setName(name);
		TextureRegion[][] sheet = TextureRegion.split(spritesheet, tileWidth,
				tileHeight);
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
		sprite.setRegion(downIdle);
		// default walk AI
		walkAI = new StandStillAI();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Coordinate getLocation()
	{
		return location;
	}

	public void setLocation(Coordinate location)
	{
		this.location = location;
	}

	public float getX()
	{
		return location.getX();
	}

	public float getY()
	{
		return location.getY();
	}

	public Direction getDirection()
	{
		return direction;
	}

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}

	public boolean isMoving()
	{
		return moving;
	}

	public void setMoving(boolean moving)
	{
		this.moving = moving;
	}

	protected float getStateTime()
	{
		return stateTime;
	}

	protected void addToStateTime(float deltaTime)
	{
		this.stateTime += deltaTime;
	}

	public Sprite getSprite()
	{
		return sprite;
	}

	public Rectangle getBoundingRectangle()
	{
		return sprite.getBoundingRectangle();
	}

	protected TextureRegion getRightIdle()
	{
		return rightIdle;
	}

	protected TextureRegion getLeftIdle()
	{
		return leftIdle;
	}

	protected TextureRegion getUpIdle()
	{
		return upIdle;
	}

	protected TextureRegion getDownIdle()
	{
		return downIdle;
	}

	protected Animation getRightWalkAnimation()
	{
		return rightWalkAnimation;
	}

	protected Animation getLeftWalkAnimation()
	{
		return leftWalkAnimation;
	}

	protected Animation getUpWalkAnimation()
	{
		return upWalkAnimation;
	}

	protected Animation getDownWalkAnimation()
	{
		return downWalkAnimation;
	}

	public float getSpeed()
	{
		return speed;
	}

	public void setSpeed(float newSpeed)
	{
		this.speed = newSpeed;
	}

	public float getSpriteWidth()
	{
		return sprite.getWidth();
	}

	public float getSpriteHeight()
	{
		return sprite.getHeight();
	}

	public void setPosition(float x, float y)
	{
		sprite.setPosition(x, y);
	}

	public WalkAI getWalkAI()
	{
		return walkAI;
	}

	public void setWalkAI(WalkAI walkAI)
	{
		this.walkAI = walkAI;
	}

	public Map getCurrentMap()
	{
		return currentMap;
	}

	public void setCurrentMap(Map currentMap)
	{
		this.currentMap = currentMap;
	}

	public void render(SpriteBatch batch)
	{
		sprite.draw(batch);
		if (weaponSlot != null)
		{
			weaponSlot.render(sprite.getBoundingRectangle(), getDirection(), batch);
		}
	}

	/**
	 * Will modify the currentLocation object to have the new location
	 * 
	 * @param deltaTime
	 *            As reported by Graphics.getDeltaTime()
	 * @param currentLocation
	 *            The location of this Character on the current map
	 * @param mapWidth
	 *            The map width of the current map
	 * @param mapHeight
	 *            The map height of the current map
	 */
	public abstract void update(float deltaTime, Map currentMap);

	protected void updateTexture()
	{
		TextureRegion currentFrame = null;
		switch (getDirection())
		{
		case RIGHT:
			currentFrame = isMoving() ? getRightWalkAnimation().getKeyFrame(
					getStateTime(), true) : getRightIdle();
					break;
		case LEFT:
			currentFrame = isMoving() ? getLeftWalkAnimation().getKeyFrame(
					getStateTime(), true) : getLeftIdle();
					break;
		case UP:
			currentFrame = isMoving() ? getUpWalkAnimation().getKeyFrame(
					getStateTime(), true) : getUpIdle();
					break;
		case DOWN:
			currentFrame = isMoving() ? getDownWalkAnimation().getKeyFrame(
					getStateTime(), true) : getDownIdle();
					break;
		}

		if (currentFrame != null)
		{
			sprite.setRegion(currentFrame);
		}
	}

	public void addedToMap(Map map)
	{
		currentMap = map;
		walkAI.start();
	}

	public void removedFromMap(Map map)
	{
		walkAI.stop();
		currentMap = null;
	}

	public abstract void acceptGoodAction(Character characterDoingAction);

	public abstract void doneFollowingPath();

	/**
	 * May be useful with debugging. Likely will be out of date if Character
	 * class is updated.
	 */
	@Override
	public String toString()
	{
		return String
				.format("(CharacterToString){name:%s, sprite:%s, direction:%s, moving:%s, stateTime:%lf}",
						name, sprite, direction, moving, stateTime);
	}

	/**
	 * Likely insufficient implementation of equip
	 * @param sword
	 */
	public void equip(Weapon weapon)
	{
		this.weaponSlotExtra = weaponSlot; // blah
		this.weaponSlot = weapon;
	}

}
