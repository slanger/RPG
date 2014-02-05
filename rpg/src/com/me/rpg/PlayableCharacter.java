package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.Map;

public class PlayableCharacter extends Character
{

	private boolean enableAttack = true;
	private boolean enableWeaponSwitch = true;
	private boolean enableStyleSwitch = true;
	private boolean enableGoodAction = true;
	private boolean enableDialogue = true;
	private boolean enableControls = true;
    
	private float lastCheckedTime;
	private boolean tempAcceptInput=true;
	
	public boolean getEnableControls()
	{
		return enableControls;
	}

	public void setEnableControls(boolean enable_controls)
	{
		this.enableControls = enable_controls;
	}

	public PlayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration);
	}

	@Override
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
		setMoving(false);
		addToStateTime(deltaTime);

		// handle input
		// I use this idiom to get around having the action be executed like
		// 1000 times before the player takes his finger off the button
		// Let me know if you think of something better -Mark
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			if (enableGoodAction)
			{
				enableGoodAction = false;
				doGoodAction(deltaTime, currentMap);
			}
		}
		else
		{
			enableGoodAction = true;
		}
		
		//DIALOGUE STUFF
		if(tempAcceptInput==true){
		if (Gdx.input.isKeyPressed(Keys.E) )
		{
			if (enableDialogue)
			{
				
				if(currentMap.getWorld().getDialogue().getInDialogue()==false)
				{
					initiateDialogue(deltaTime, currentMap);
					advanceDialogue(deltaTime,currentMap,"E");
				}
				else if(currentMap.getWorld().getDialogue().getInDialogue()==true) //currently in dialogue
				{
					advanceDialogue(deltaTime,currentMap,"E");
				}
				else
				{
					//
				}
				tempAcceptInput=false;
			}
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_1))
		{
			if (enableDialogue && currentMap.getWorld().getDialogue().getRequireResponse()==true)
			{
				tempAcceptInput=false;
				advanceDialogue(deltaTime,currentMap,"NUM_1");
			}
		}
		if (Gdx.input.isKeyPressed(Keys.NUM_2))
		{
			if (enableDialogue && currentMap.getWorld().getDialogue().getRequireResponse()==true)
			{
				tempAcceptInput=false;
				advanceDialogue(deltaTime,currentMap,"NUM_2");
			}
		}
		
		if (Gdx.input.isKeyPressed(Keys.NUM_3))
		{
			if (enableDialogue && currentMap.getWorld().getDialogue().getRequireResponse()==true)
			{
				tempAcceptInput=false;
				advanceDialogue(deltaTime,currentMap,"NUM_3");
			}
		}
		}
		
		if (Gdx.input.isKeyPressed(Keys.R))
		//END DIALOGUE STUFF
		{
			tempAcceptInput=true;
		}
		// check for input
		if (enableControls)
		{
			int dx = 0;
			int dy = 0;
			if (Gdx.input.isKeyPressed(Keys.LEFT))
			{
				dx += Direction.LEFT.getDx();
				dy += Direction.LEFT.getDy();
			}
			if (Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				dx += Direction.RIGHT.getDx();
				dy += Direction.RIGHT.getDy();
			}
			if (Gdx.input.isKeyPressed(Keys.UP))
			{
				dx += Direction.UP.getDx();
				dy += Direction.UP.getDy();
			}
			if (Gdx.input.isKeyPressed(Keys.DOWN))
			{
				dx += Direction.DOWN.getDx();
				dy += Direction.DOWN.getDy();
			}

			// decode Direction from input
			int diff = Math.abs(dx) + Math.abs(dy);
			if (diff >= 2)
			{
				// moving diagonally, slow down movement in x and y
				x += (dx * speed * deltaTime) / Math.sqrt(2);
				y += (dy * speed * deltaTime) / Math.sqrt(2);
				setDirection(Direction.getDirectionByDiff(0, dy));
				setMoving(true);
			}
			else if (diff >= 1)
			{
				// moving in 1 direction
				x += dx * speed * deltaTime;
				y += dy * speed * deltaTime;
				setDirection(Direction.getDirectionByDiff(dx, dy));
				setMoving(true);
			}
		}

		// update x and y
		if (isMoving())
		{
			// collision detection with objects on map
			Coordinate newCoordinate = new Coordinate();
			boolean didMove = currentMap.checkCollision(x, y, oldX, oldY,
					spriteWidth, spriteHeight, this, newCoordinate);

			setMoving(didMove);

			if (didMove)
			{
				x = newCoordinate.getX();
				y = newCoordinate.getY();
				currentLocation.setX(x + spriteWidth / 2);
				currentLocation.setY(y + spriteHeight / 2);

				// check warp point collision
				Map newMap = currentMap.checkWarpPointCollision(new Rectangle(x, y, spriteWidth, spriteHeight));
				if (newMap != null)
				{
					currentMap.getWorld().setMap(newMap);
				}
			}
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
		
		// attack
		if (Gdx.input.isKeyPressed(Keys.J))
		{
			if (weaponSlot != null && enableAttack)
			{
				weaponSlot.attack(currentMap, getDirection(), getSprite()
						.getBoundingRectangle());
				enableAttack = false;
			}
		}
		else
		{
			enableAttack = true;
		}

		// switch attack style
		if (Gdx.input.isKeyPressed(Keys.K))
		{
			if (weaponSlot != null && enableStyleSwitch)
			{
				weaponSlot.switchStyle();
				enableStyleSwitch = false;
			}
		}
		else
		{
			enableStyleSwitch = true;
		}

		if (weaponSlot != null)
		{
			weaponSlot.update(deltaTime);
		}

		// switch weapon
		if (Gdx.input.isKeyPressed(Keys.M))
		{
			if (enableWeaponSwitch)
			{
				Weapon temp = weaponSlot;
				weaponSlot = weaponSlotExtra;
				weaponSlotExtra = temp;
				enableWeaponSwitch = false;
			}
		}
		else
		{
			enableWeaponSwitch = true;
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
	private void initiateDialogue(float deltaTime, Map currentMap)
	{
		float width = getSpriteWidth();
		float height = getSpriteHeight();
		float x = getX() + getSpriteWidth() * getDirection().getDx();
		float y = getY() + getSpriteHeight() * getDirection().getDy();
		Rectangle hitbox = new Rectangle(x, y, width, height);
		Character c = currentMap.checkCharacterCollision(hitbox, this);
		if (c != null)
		{
			c.setMoving(false);
			currentMap.getWorld().getDialogue().setInDialogue(true);
			currentMap.getWorld().getDialogue().update(c);
		}
	}
	
	private void advanceDialogue(float deltaTime, Map currentMap, String key)
	{
			currentMap.getWorld().getDialogue().advanceDialogue(key);
	}
	
	@Override
	public void acceptGoodAction(Character characterDoingAction)
	{
		return; // do nothing
	}

}
