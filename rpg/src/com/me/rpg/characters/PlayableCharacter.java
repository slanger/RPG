package com.me.rpg.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.World;
import com.me.rpg.ai.DialogueSystem;
import com.me.rpg.ai.PlayerControlledWalkAI;
import com.me.rpg.maps.Map;
import com.me.rpg.maps.MapType;
import com.me.rpg.utils.Coordinate;

public class PlayableCharacter extends GameCharacter
{

	private static final long serialVersionUID = 2335023793457785574L;

	private boolean enableReseting = true;
	private boolean enableSaving = true;
	private boolean enableAttack = true;
	private boolean enableWeaponSwitch = true;
	private boolean enableStyleSwitch = true;
	private boolean enableControls = true;
	private boolean enablePushing = true;

	// dialogue keys
	private boolean enableInputE = true;  //for initiating dialogue with character, selecting response option
	private boolean enableInputUp = true;
	private boolean enableInputDown = true;
	
	//inventory menu keys
	private boolean enableInputI = true;
	//private boolean enableInputUp = true;
	//private boolean enableInputDown = true;
	private boolean enableInputRight = true;
	private boolean enableInputLeft = true;
	private long timeInventoryOpened = 0;
	
	private PlayerControlledWalkAI walkAI;
	
	public boolean getEnableControls()
	{
		return enableControls;
	}

	public void setEnableControls(boolean enableControls)
	{
		this.enableControls = enableControls;
	}

	public PlayableCharacter(String name, String spritesheetPath, int width,
			int height, int tileWidth, int tileHeight, float animationDuration,
			World world)
	{
		super(name, spritesheetPath, width, height, tileWidth, tileHeight,
				animationDuration, world);
		npcMemory = null;
		walkAI = new PlayerControlledWalkAI(this);
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

	public void handleInput(float deltaTime)
	{
		if (!enableControls)
		{
			return;
		}

		// reset the game
		if (Gdx.input.isKeyPressed(Keys.R))
		{
			if (enableReseting)
			{
				enableReseting = true;
				world.setResetGame(true);
			}
		}
		else
		{
			enableReseting = true;
		}

		// save the game
		if (Gdx.input.isKeyPressed(Keys.ENTER))
		{
			if (enableSaving)
			{
				enableSaving = false;
				world.setSaveGame(true);
			}
		}
		else
		{
			enableSaving = true;
		}

		/*
		 *  INVENTORY MENU
		 */
		
		if(world.getInventoryMenu().getInMenu() == true)
		{
			if(Gdx.input.isKeyPressed(Keys.I) && (System.currentTimeMillis() - timeInventoryOpened > 250))
			{
				world.getInventoryMenu().closeInventory();
			}
			else
			{
				handleInventoryMenuInput();
			}
			return;
		}
		if (Gdx.input.isKeyPressed(Keys.I))
		{
			if (enableInputI)
			{
				enableInputI = false;
				if (!world.getInventoryMenu().getInMenu() && (System.currentTimeMillis() - timeInventoryOpened) >= 300)
				{
					timeInventoryOpened = System.currentTimeMillis();
					world.getInventoryMenu().openInventory(this);
				}
				else
				{ // currently in dialogue
					System.out.println("already in menu");
				}
			}
		}
		else
		{
			enableInputI = true;
		}
		
		/*
		 *  END INVENTORY MENU
		 */
		
		/*
		 * DIALOGUE
		 */

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

		/*
		 * ACTIONS
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

	public void handleInventoryMenuInput()
	{
		if(Gdx.input.isKeyPressed(Keys.UP))
		{
			if(enableInputUp)
			{
				enableInputUp = false;
				if(world.getInventoryMenu().getInMenu())
				{
					world.getInventoryMenu().acceptPlayerInput("UP");
					System.out.println("In Menu:  UP");
				}
			}
		}
		else
		{
			enableInputUp = true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.DOWN))
		{
			if(enableInputDown)
			{
				enableInputDown = false;
				if(world.getInventoryMenu().getInMenu())
				{
					world.getInventoryMenu().acceptPlayerInput("DOWN");
					System.out.println("In Menu:  DOWN");
				}
			}
		}
		else
		{
			enableInputDown = true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			if(enableInputRight)
			{
				enableInputRight = false;
				if(world.getInventoryMenu().getInMenu())
				{
					world.getInventoryMenu().acceptPlayerInput("RIGHT");
					System.out.println("In Menu:  RIGHT");

				}
			}
		}
		else
		{
			enableInputRight = true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT))
		{
			if(enableInputLeft)
			{
				enableInputLeft = false;
				if(world.getInventoryMenu().getInMenu())
				{
					world.getInventoryMenu().acceptPlayerInput("LEFT");
					System.out.println("In Menu:  LEFT");
				}
			}
		}
		else
		{
			enableInputLeft = true;
		}
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
			DialogueSystem dialogueSystem = world.getDialogueSystem();
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

	@Override
	public void moveToOtherMap(MapType mapType, Coordinate newLocation)
	{
		world.movePlayerToOtherMap(mapType, newLocation);
	}

	@Override
	public void warpToOtherMap(MapType mapType, Coordinate newLocation)
	{
		world.warpPlayerToOtherMap(mapType, newLocation);
	}

}
