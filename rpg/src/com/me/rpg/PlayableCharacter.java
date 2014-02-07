package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.maps.Map;

public class PlayableCharacter extends GameCharacter
{

	// for debugging hitboxes
	//private Texture redHitboxTexture;
	//private Rectangle redHitbox = new Rectangle(0, 0, 0, 0);

	private boolean enableAttack = true;
	private boolean enableWeaponSwitch = true;
	private boolean enableStyleSwitch = true;
	private boolean enableGoodAction = true;
	private boolean enableControls = true;

	private boolean enableInputE = true;
	private boolean enableInput1 = true;
	private boolean enableInput2 = true;
	private boolean enableInput3 = true;

	public boolean getEnableControls()
	{
		return enableControls;
	}

	public void setEnableControls(boolean enableControls)
	{
		this.enableControls = enableControls;
	}

	public PlayableCharacter(String name, Texture spritesheet, int width,
			int height, int tileWidth, int tileHeight, float animationDuration)
	{
		super(name, spritesheet, width, height, tileWidth, tileHeight,
				animationDuration);

		//redHitboxTexture = RPG.manager.get(World.FADED_RED_DOT_PATH, Texture.class);
	}

	@Override
	public void doUpdate(float deltaTime, Map currentMap)
	{
		setMoving(false);

		// handle input
		handleInput(deltaTime);

		// update texture
		updateTexture();
	}

	@Override
	public void doRenderAfter(SpriteBatch batch)
	{
		//batch.draw(redHitboxTexture, redHitbox.x, redHitbox.y, redHitbox.width, redHitbox.height);
	}

	private void handleInput(float deltaTime)
	{
		if (!enableControls)
		{
			return;
		}

		/*
		 * MOVEMENT
		 */

		float spriteWidth = getSpriteWidth();
		float spriteHeight = getSpriteHeight();
		float oldX = getBottomLeftX();
		float oldY = getBottomLeftY();
		float x = oldX;
		float y = oldY;
		float speed = getSpeed();
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

		// update x and y
		if (isMoving())
		{
			// collision detection with objects on map
			Coordinate newCoordinate = new Coordinate();
			boolean didMove = currentMap.checkCollision(x, y, oldX, oldY, this, newCoordinate);

			setMoving(didMove);

			if (didMove)
			{
				setBottomLeftCorner(newCoordinate);

				// check warp point collision
				x = newCoordinate.getX();
				y = newCoordinate.getY();
				Map newMap = currentMap.checkWarpPointCollision(new Rectangle(
						x, y, spriteWidth, spriteHeight));
				if (newMap != null)
				{
					currentMap.getWorld().warpToAnotherMap(newMap);
				}
			}
		}

		/*
		 * END MOVEMENT
		 */

		// do good action
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

		/*
		 * DIALOGUE
		 */

		if (Gdx.input.isKeyPressed(Keys.E))
		{
			if (enableInputE)
			{
				enableInputE = false;
				if (!currentMap.getWorld().getDialogue().getInDialogue())
				{
					initiateDialogue(deltaTime, currentMap);
					advanceDialogue(deltaTime, currentMap, "E");
				}
				else
				{ // currently in dialogue
					advanceDialogue(deltaTime, currentMap, "E");
				}
			}
		}
		else
		{
			enableInputE = true;
		}

		if (Gdx.input.isKeyPressed(Keys.NUM_1))
		{
			if (enableInput1)
			{
				enableInput1 = false;
				advanceDialogue(deltaTime, currentMap, "NUM_1");
			}
		}
		else
		{
			enableInput1 = true;
		}

		if (Gdx.input.isKeyPressed(Keys.NUM_2))
		{
			if (enableInput2)
			{
				enableInput2 = false;
				advanceDialogue(deltaTime, currentMap, "NUM_2");
			}
		}
		else
		{
			enableInput2 = true;
		}

		if (Gdx.input.isKeyPressed(Keys.NUM_3))
		{
			if (enableInput3)
			{
				enableInput3 = false;
				advanceDialogue(deltaTime, currentMap, "NUM_3");
			}
		}
		else
		{
			enableInput3 = true;
		}

		/*
		 * END DIALOGUE
		 */

		/*
		 * COMBAT
		 */

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

		// switch weapon
		if (Gdx.input.isKeyPressed(Keys.M))
		{
			if (enableWeaponSwitch)
			{
				swapWeapon(currentMap);
				enableWeaponSwitch = false;
			}
		}
		else
		{
			enableWeaponSwitch = true;
		}

		/*
		 * END COMBAT
		 */
	}

	private void doGoodAction(float deltaTime, Map currentMap)
	{
		Rectangle hitbox = getHitboxInFrontOfCharacter();
		GameCharacter c = currentMap.checkCharacterCollision(hitbox, this);
		if (c != null)
		{
			c.acceptGoodAction(this);
		}
	}

	private void initiateDialogue(float deltaTime, Map currentMap)
	{
		Rectangle hitbox = getHitboxInFrontOfCharacter();
		GameCharacter c = currentMap.checkCharacterCollision(hitbox, this);
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
	public void acceptGoodAction(GameCharacter characterDoingAction)
	{
		return; // do nothing
	}

	@Override
	public void doneFollowingPath()
	{
		return; // do nothing
	}

}
