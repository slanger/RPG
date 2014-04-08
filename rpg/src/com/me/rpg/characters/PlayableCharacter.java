package com.me.rpg.characters;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.World;
import com.me.rpg.ai.DialogueSystem;
import com.me.rpg.ai.PlayerControlledWalkAI;
import com.me.rpg.combat.Equippable;
import com.me.rpg.combat.MeleeWeapon;
import com.me.rpg.combat.Projectile;
import com.me.rpg.combat.RangedWeapon;
import com.me.rpg.combat.Shield;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Location;

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
	private boolean enableInputE = true; // for initiating dialogue with
											// character, selecting response
											// option
	private boolean enableInputUp = true;
	private boolean enableInputDown = true;

	// inventory menu keys
	private boolean enableInputI = true;
	// private boolean enableInputUp = true;
	// private boolean enableInputDown = true;
	private boolean enableInputRight = true;
	private boolean enableInputLeft = true;
	
	private long timeInventoryOpened = 0;
	
	//pause menu keys
	private boolean enableInputEscape = true;
		//UP, DOWN, ENTER
	
	//debug menu keys
	private long timeDebugMenuOpened = 0;
	private boolean enableInputQ = true;
		//left right
	
	private ArrayList<Weapon> weaponsInInventory;
	private ArrayList<Shield> shieldsInInventory;
	private ArrayList<Projectile> arrowsInInventory;
	private RangedWeapon rangedWeaponInInventory;
	//private ArrayList<> miscItemsInInventory;
	
	private PlayerControlledWalkAI walkAI;

	public boolean getEnableControls()
	{
		return enableControls;
	}

	public void setEnableControls(boolean enableControls)
	{
		this.enableControls = enableControls;
	}

	public PlayableCharacter(String name, String group, String spritesheetPath,
			int width, int height, int tileWidth, int tileHeight,
			float animationDuration, World world)
	{
		super(name, group, spritesheetPath, width, height, tileWidth,
				tileHeight, animationDuration, world);
		weaponsInInventory = new ArrayList<Weapon>();
		arrowsInInventory = new ArrayList<Projectile>();
		shieldsInInventory = new ArrayList<Shield>();
		npcMemory = null;
		walkAI = new PlayerControlledWalkAI(this);
	}

	@Override
	public void doUpdate(float deltaTime)
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

		// PAUSE MENU
		if (world.getPauseScreen().getInMenu())
		{
			return;
		}

		if (Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			if (enableInputEscape)
			{
				enableInputEscape = false;
				world.getPauseScreen().openPauseMenu();
			}
		}
		else
		{
			enableInputEscape = true;
		}
		//END PAUSE
		
		
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


		/*
		 *  INVENTORY MENU
		 */
		
		if(world.getReputationDebugMenu().getInMenu() == true)
		{
			if(Gdx.input.isKeyPressed(Keys.Q) && (System.currentTimeMillis() - timeDebugMenuOpened > 250))
			{
				world.getReputationDebugMenu().closeMenu();
			}
			else
			{
				//handleReputationDebugMenuInput();
			}
			return;
		}
		
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
		 *  REP DEBUG MENU
		 */
		
		
		if (Gdx.input.isKeyPressed(Keys.Q))
		{
			if (enableInputQ)
			{
				enableInputQ = false;
				if (!world.getReputationDebugMenu().getInMenu() && (System.currentTimeMillis() - timeDebugMenuOpened) >= 300)
				{
					timeDebugMenuOpened = System.currentTimeMillis();
					world.getReputationDebugMenu().openMenu();
				}
			}
		}
		else
		{
			enableInputQ = true;
		}
		
		/*
		 *  END REP DEBUG MENU
		 */
		
		
		/*
		 * DIALOGUE
		 */

		if (world.getDialogueSystem().getInDialogue())
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
					if (world.getDialogueSystem().getInDialogue())
					{
						dialogueEnded = advanceDialogue("ENTER");
					}
				}
				else
				{ // currently in dialogue
					dialogueEnded = advanceDialogue("ENTER");
				}
				if (dialogueEnded == true)
					world.setUpdateEnable(true);
			}
		}
		else
		{
			enableInputE = true;
		}
		/*
		 * END DIALOGUE
		 */

		if (world.getDialogueSystem().getInDialogue() == true)
		{
			return;
		}

		/*
		 * MOVEMENT
		 */

		walkAI.update(deltaTime);

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
				weaponSlot.attack(getCurrentMap(), getFaceDirection(),
						getBoundingRectangle());
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
//		if (Gdx.input.isKeyPressed(Keys.M))
//		{
//			if (enableWeaponSwitch)
//			{
//				swapWeapon(getCurrentMap());
//				enableWeaponSwitch = false;
//			}
//		}
//		else
//		{
//			enableWeaponSwitch = true;
//		}

		/*
		 * END COMBAT
		 */
	}

	public void handleInventoryMenuInput()
	{
		if(world.getInventoryMenu().getInMenu() == false)
		{
			return;
		}
		
		if(Gdx.input.isKeyPressed(Keys.UP))
		{
			if(enableInputUp)
			{
				enableInputUp = false;
				if(world.getInventoryMenu().getInMenu())
				{
					world.getInventoryMenu().acceptPlayerInput("UP");
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
				}
			}
		}
		else
		{
			enableInputLeft = true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.E))
		{
			if(enableInputE)
			{
				enableInputE = false;
				if(world.getInventoryMenu().getInMenu())
				{
					world.getInventoryMenu().acceptPlayerInput("E");
				}
			}
		}
		else
		{
			enableInputE = true;
		}
	}
	
	public void handleDialogueInput()
	{
		if(world.getDialogueSystem().getInDialogue() == false)
		{
			return;
		}
		
		
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
					if (world.getDialogueSystem().getInDialogue())
					{
						dialogueEnded = advanceDialogue("ENTER");
					}
				}
				else
				{ // currently in dialogue
					dialogueEnded = advanceDialogue("ENTER");
				}
				if (dialogueEnded == true)
					world.setUpdateEnable(true);
			}
		}
		else
		{
			enableInputE = true;
		}
		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			if (enableInputUp)
			{
				enableInputUp = false;
				if (world.getDialogueSystem().getInDialogue())
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
			if (enableInputDown == true)
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
	
	public void handlePauseMenuInput()
	{
		if(world.getPauseScreen().getInMenu() == false)
		{
			return;
		}
		
		if(Gdx.input.isKeyPressed(Keys.ESCAPE))
		{
			if(enableInputEscape)
			{
				enableInputEscape = false;
				if(world.getPauseScreen().getInMenu())
				{
					world.getPauseScreen().acceptPlayerInput("ESCAPE");
				}
			}
		}
		else
		{
			enableInputEscape = true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.UP))
		{
			if(enableInputUp)
			{
				enableInputUp = false;
				if(world.getPauseScreen().getInMenu())
				{
					world.getPauseScreen().acceptPlayerInput("UP");
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
				if(world.getPauseScreen().getInMenu())
				{
					world.getPauseScreen().acceptPlayerInput("DOWN");
				}
			}
		}
		else
		{
			enableInputDown = true;
		}
		
		if(Gdx.input.isKeyPressed(Keys.ENTER))
		{
			if(enableInputRight)
			{
				enableInputRight = false;
				if(world.getPauseScreen().getInMenu())
				{
					world.getPauseScreen().acceptPlayerInput("ENTER");
				}
			}
		}
		else
		{
			enableInputRight = true;
		}
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
			boolean foundDialogue = dialogueSystem.startConversation(c);
			if (foundDialogue)
			{
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
	public void removedFromMap()
	{
		nextLocation.getMap().addFocusedCharacterToMap(this, nextLocation);
	}

	@Override
	public void moveToOtherMap(Location newLocation)
	{
		currentLocation.getMap().close();
		nextLocation = newLocation;
		currentLocation.getMap().removeCharacterFromMap(this);
	}

	@Override
	public void warpToOtherMap(Location newLocation)
	{
		moveToOtherMap(newLocation);

		/* TODO
		currentMap.close();
		world.setUpdateEnable(false);
		warping = true;
		warpingAlpha = 0f;
		world.warpSound.play();
		setWarpEnable(false);
		Vector2 center = newLocation.getCenter(new Vector2());
		Coordinate warpLocation = new Coordinate(center.x - getSpriteWidth() / 2, center.y - getSpriteHeight() / 2);

		world.getTimer().scheduleTask(new Timer.Task()
		{

			private static final long serialVersionUID = -4094471058413909756L;

			@Override
			public void run()
			{
				warpingAlpha += 0.1f;
				if (warpingAlpha > 1f)
				{
					warpingAlpha = 1f;
					this.cancel();
				}
			}

		}, 0f, 0.1f);

		timer.scheduleTask(new Timer.Task()
		{

			private static final long serialVersionUID = 8348680306281141956L;

			@Override
			public void run()
			{
				currentMap.removeCharacterFromMap(player);
				newMap.addFocusedCharacterToMap(player, newLocation);

				timer.scheduleTask(new Timer.Task()
				{

					private static final long serialVersionUID = -1100673244597339611L;

					@Override
					public void run()
					{
						warpingAlpha -= 0.1f;
					}

				}, 0f, 0.1f, 10);

				timer.scheduleTask(new Timer.Task()
				{

					private static final long serialVersionUID = 4263563367493321895L;

					@Override
					public void run()
					{
						updateEnable = true;
						warping = false;
						newMap.open();
					}

				}, 1.0f);
			}

		}, 3.0f);
		*/
	}
	
	public void addItemToInventory(Weapon item)
	{
		weaponsInInventory.add(item);
	}
	
	public void addItemToInventory(Projectile item)
	{
		arrowsInInventory.add(item);
	}
	
	public void addItemToInventory(Shield item)
	{
		shieldsInInventory.add(item);
	}
	
	public void addItemToInventory(RangedWeapon item)
	{
		rangedWeaponInInventory = item;
	}
	
	public ArrayList<Weapon> getWeapons()
	{
		return weaponsInInventory;
	}
	
	public ArrayList<Projectile> getArrows()
	{
		return arrowsInInventory;
	}
	
	public ArrayList<Shield> getShields()
	{
		return shieldsInInventory;
	}
	
	public RangedWeapon getRangedWeaponInInventory()
	{
		return rangedWeaponInInventory;
	}
//	public void addItemToInventory(MiscItem item)
//	{
//		
//	}

}
