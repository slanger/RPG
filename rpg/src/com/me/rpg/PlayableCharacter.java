package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.Dialogue;
import com.me.rpg.ai.PlayerControlledWalkAI;
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
	private boolean enablePushing = true;

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
		setWalkAI(new PlayerControlledWalkAI(this));
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
		
		walkAI.update(deltaTime, currentMap);

		/*
		 * END MOVEMENT
		 */

		/*
		 * ACTIONS
		 */

		// do good action
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			if (enableGoodAction)
			{
				enableGoodAction = false;
				doGoodAction();
			}
		}
		else
		{
			enableGoodAction = true;
		}

		// push a character
		if (Gdx.input.isKeyPressed(Keys.S))
		{
			if (enablePushing)
			{
				enablePushing = false;
				doPush();
			}
		}
		else
		{
			enablePushing = true;
		}

		/*
		 * END ACTIONS
		 */

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
					initiateDialogue();
					advanceDialogue("E");
				}
				else
				{ // currently in dialogue
					advanceDialogue("E");
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
				advanceDialogue("NUM_1");
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
				advanceDialogue("NUM_2");
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
				advanceDialogue("NUM_3");
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
				weaponSlot.attack(currentMap, getFaceDirection(), getSprite()
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

	private void doGoodAction()
	{
		Rectangle hitbox = getHitboxInFrontOfCharacter();
		GameCharacter c = getCurrentMap().checkCollisionWithCharacters(hitbox, this);
		if (c != null)
		{
			c.acceptGoodAction(this);
		}
	}

	private void doPush()
	{
		Rectangle hitbox = getHitboxInFrontOfCharacter();
		GameCharacter c = getCurrentMap().checkCollisionWithCharacters(hitbox, this);
		if (c != null)
		{
			c.acceptPush(this);
		}
	}

	private void initiateDialogue()
	{
		Map currentMap = getCurrentMap();
		Rectangle hitbox = getHitboxInFrontOfCharacter();
		GameCharacter c = currentMap.checkCollisionWithCharacters(hitbox, this);
		if (c != null)
		{
			c.setMoving(false);
			Dialogue dialogue = currentMap.getWorld().getDialogue();
			dialogue.setInDialogue(true);
			dialogue.update(c);
		}
	}
	
	@Override
	public boolean isGameOver() {
		return isDead();
	}

	private void advanceDialogue(String key)
	{
		getCurrentMap().getWorld().getDialogue().advanceDialogue(key);
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
