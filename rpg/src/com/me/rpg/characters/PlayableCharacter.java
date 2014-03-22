package com.me.rpg.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.World;
import com.me.rpg.ai.DialogueSystem;
import com.me.rpg.ai.PlayerControlledWalkAI;
import com.me.rpg.maps.Map;

public class PlayableCharacter extends GameCharacter
{

	// for debugging hitboxes
	// private Texture redHitboxTexture;
	// private Rectangle redHitbox = new Rectangle(0, 0, 0, 0);

	private boolean enableAttack = true;
	private boolean enableWeaponSwitch = true;
	private boolean enableStyleSwitch = true;
	private boolean enableControls = true;
	private boolean enablePushing = true;

	//dialogue keys
	private boolean enableInputE = true;  //for initiating dialogue with character, selecting response option
	private boolean enableInputUp = true;
	private boolean enableInputDown = true;
	//end dialogue keys
	
	private PlayerControlledWalkAI walkAI;
	
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
		npcMemory = null;
		walkAI = new PlayerControlledWalkAI(this);
		// redHitboxTexture = RPG.manager.get(World.FADED_RED_DOT_PATH,
		// Texture.class);
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
		// batch.draw(redHitboxTexture, redHitbox.x, redHitbox.y,
		// redHitbox.width, redHitbox.height);
	}

	public void handleInput(float deltaTime)
	{
		if (!enableControls)
		{
			return;
		}

		if(world.getDialogueSystem().getInDialogue()) 
		{
			return;
		}
		
		if (Gdx.input.isKeyPressed(Keys.E))
		{
			boolean dialogueEnded = false;
			if (enableInputE)
			{
				enableInputE = false;
				if (!world.getDialogueSystem().getInDialogue())
				{
					initiateDialogue();
					if(world.getDialogueSystem().getInDialogue())
					{						
						dialogueEnded = advanceDialogue("ENTER");
					}
				}
				else
				{ // currently in dialogue
					dialogueEnded = advanceDialogue("ENTER");
				}
				if(dialogueEnded == true) world.setUpdateEnable(true);
			}
		}
		else
		{
			enableInputE = true;
		}
		/*
		 * END DIALOGUE
		 */
		
		if(world.getDialogueSystem().getInDialogue() == true)
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
		 * COMBAT
		 */

		// attack
		if (Gdx.input.isKeyPressed(Keys.J))
		{
			if (weaponSlot != null && enableAttack)
			{
				weaponSlot.attack(currentMap, getFaceDirection(), getBoundingRectangle());
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

	public void handleDialogueInput()
	{
		if (!enableControls)
		{
			return;
		}

		/*
		 * DIALOGUE
		 */
		if (Gdx.input.isKeyPressed(Keys.E))
		{
			boolean dialogueEnded = false;
			if (enableInputE)
			{
				enableInputE = false;
				if (!world.getDialogueSystem().getInDialogue())
				{
					initiateDialogue();
					if(world.getDialogueSystem().getInDialogue())
					{						
						dialogueEnded = advanceDialogue("ENTER");
					}
				}
				else
				{ // currently in dialogue
					dialogueEnded = advanceDialogue("ENTER");
				}
				if(dialogueEnded == true) world.setUpdateEnable(true);
			}
		}
		else
		{
			enableInputE = true;
		}
		if(Gdx.input.isKeyPressed(Keys.UP))
		{
			if(enableInputUp)
			{
				enableInputUp = false;
				if(world.getDialogueSystem().getInDialogue())
				{
					advanceDialogue("UP");
				}
			}
		}
		else
		{
			enableInputUp = true;
		}
		
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			if(enableInputDown == true)
			{
				enableInputDown = false;
				if (world.getDialogueSystem().getInDialogue())
				{
					advanceDialogue("DOWN");
				}
			}
		}
		else
		{
			enableInputDown = true;
		}
		
		/*
		 * END DIALOGUE
		 */
		
	}
	

	private void doPush()
	{
		Rectangle hitbox = getHitboxInFrontOfCharacter();
		GameCharacter c = getCurrentMap().checkCollisionWithCharacters(hitbox,
				this);
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
			DialogueSystem dialogueSystem = World.getInstance().getDialogueSystem();
			boolean foundDialogue = dialogueSystem.startConversation(this, c);
			if(foundDialogue){
				c.setFaceDirection(this.getFaceDirection().opposite());
				world.setUpdateEnable(false);
			}
		}
	}

	@Override
	public boolean isGameOver()
	{
		return isDead();
	}

	private boolean advanceDialogue(String key)
	{	
		return world.getDialogueSystem().advanceDialogue(key);
	}

}
