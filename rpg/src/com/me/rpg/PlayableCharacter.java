package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.combat.Weapon;

public class PlayableCharacter extends Character
{
	private boolean switchBool = false;
	private boolean enable_good_action = true;


	public PlayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration);
	}

	public void update(float deltaTime, Map currentMap)
	{	
		float spriteWidth = getSpriteWidth();
		float spriteHeight = getSpriteHeight();
		Coordinate currentLocation = getLocation();
		float oldX = currentLocation.getX() - spriteWidth / 2;
		float oldY = currentLocation.getY() - spriteHeight / 2;
		float x = oldX;
		float y = oldY;
		float speed = getSpeed();
		int mapWidth = currentMap.getWidth();
		int mapHeight = currentMap.getHeight();
		setMoving(false);
		addToStateTime(deltaTime);

		// handle input
		// I use this idiom to get around having the action be executed like
		// 1000 times before the player takes his finger off the button
		// Let me know if you think of something better -Mark
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			if (enable_good_action)
			{
				enable_good_action = false;
				doGoodAction(deltaTime, currentMap);
			}
		}
		else
		{
			enable_good_action = true;
		}

		// check for input
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			x -= speed * deltaTime;
			setDirection(Direction.LEFT);
			setMoving(true);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			x += speed * deltaTime;
			setDirection(Direction.RIGHT);
			setMoving(true);
		}
		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			y += speed * deltaTime;
			setDirection(Direction.UP);
			setMoving(true);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			y -= speed * deltaTime;
			setDirection(Direction.DOWN);
			setMoving(true);
		}

		// update x and y
		if (isMoving())
		{
			// clamp x
			if (x < 0)
			{
				x = 0;
			}
			if (x > mapWidth - spriteWidth)
			{
				x = mapWidth - spriteWidth;
			}

			// clamp y
			if (y < 0)
			{
				y = 0;
			}
			if (y > mapHeight - spriteHeight)
			{
				y = mapHeight - spriteHeight;
			}

			// collision detection with objects on map
			Coordinate newCoordinate = currentMap.checkCollision(x, y, oldX, oldY,
					spriteWidth, spriteHeight, this);
			x = newCoordinate.getX();
			y = newCoordinate.getY();

			currentLocation.setX(x + spriteWidth / 2);
			currentLocation.setY(y + spriteHeight / 2);
		}

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
			getSprite().setRegion(currentFrame);
		}
		
		// attack thing
		if (Gdx.input.isKeyPressed(Keys.J)) {
			if (weaponSlot != null) {
				weaponSlot.attack(currentMap, getDirection(), getSprite().getBoundingRectangle());
			}
		}
		if (Gdx.input.isKeyPressed(Keys.K)) {
			if (weaponSlot != null && switchBool) {
				switchBool = false;
				weaponSlot.switchStyle();
			}
		} else {
			switchBool = true;
		}
		if (weaponSlot != null) {
			weaponSlot.update(deltaTime);
		}
		if (Gdx.input.isKeyPressed(Keys.M)){
			if (switchBool) {
				Weapon temp = weaponSlot;
				weaponSlot = weaponSlotExtra;
				weaponSlotExtra = temp;
				switchBool = false;
			} else {
				switchBool = true;
			}
		}
	}

	private void doGoodAction(float deltaTime, Map currentMap)
	{
		float width = getSpriteWidth();
		float height = getSpriteHeight();
		float x = getX() + getSpriteWidth() * getDirection().getDx();
		float y = getY() + getSpriteHeight() * getDirection().getDy();
		Rectangle hitbox = new Rectangle(x, y, width, height);
		Character c = currentMap.checkCharacterCollision(hitbox, this);
		if (c != null)
		{
			c.acceptGoodAction(this);
		}
	}

	public void acceptGoodAction(Character characterDoingAction)
	{
		return; // do nothing
	}

}
