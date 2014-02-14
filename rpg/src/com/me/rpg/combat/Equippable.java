package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.me.rpg.Direction;
import com.me.rpg.characters.GameCharacter;

public abstract class Equippable implements Cloneable {
	
	private String itemName;
	private GameCharacter owner;
	private GameCharacter lastOwner;
	
	private boolean spriteInit;
	private Sprite spriteRight;
	private Sprite spriteUp;
	private Sprite spriteLeft;
	private Sprite spriteDown;
	
	protected Equippable(String itemName)
	{
		this.itemName = itemName;
		spriteInit = false;
	}
	
	/**
	 * Expects itemSprite images to be in order "right up left down"
	 * Expects each image to be in 4 adjacent regions of tileWidth x tileHeight
	 * Expects right, left to be width x height in dimension, with bottom left in corner of region
	 * Expects up, down to be height x width in dimension, with bottom left in corner of region
	 * @param itemSprite Texture containing sprite data - Expected to be (4 tileWidth) x tileHeight in size
	 * @param width Width of the particular subimage of sprite
	 * @param height Height of the particular subimage of sprite
	 * @param tileWidth Width of the tiles the itemSprite texture is split into
	 * @param tileHeight Height of the tiles the itemSprite texture is split into
	 */
	public void initSprite(Texture itemSprite, int width, int height,
			int tileWidth, int tileHeight) {
		if (spriteInit) {
			throw new RuntimeException("Item " + this + " already has initialized its sprite.");
		}
		// IMPORTANT: Expects images to be in order "right up left down"
		//			AND dimensions for right -- up are reversed
		//			AND that given dimensions are for right
		TextureRegion[][] sheet = TextureRegion.split(itemSprite, tileWidth, tileHeight);
		spriteRight = new Sprite(sheet[0][0], 0, 0, width, height);
		spriteUp = new Sprite(sheet[0][1], 0, 0, height, width);
		spriteLeft = new Sprite(sheet[0][2], 0, 0, width, height);
		spriteDown = new Sprite(sheet[0][3], 0, 0, height, width);
		spriteInit = true;
	}
	
	/**
	 * Get the sprite corresponding to the passed in direction
	 * @param direction Desired direction of sprite
	 * @return Sprite for that direction
	 */
	protected Sprite getItemSprite(Direction direction)	{
		switch(direction) {
			case UP:
				return spriteUp;
			case DOWN:
				return spriteDown;
			case LEFT:
				return spriteLeft;
			case RIGHT:
				return spriteRight;
		}
		throw new RuntimeException("Invalid direction attempted to retrieve for item: " + direction);
	}
	
	public String getName()
	{
		return itemName;
	}
	
	/**
	 * Attempts to equip the item to the given character
	 * @param c Character trying to equip item
	 * @return indicates success or failure.
	 */
	public boolean tryEquip(GameCharacter c) {
		boolean result = canEquip(c);
		if (result)
			equippedBy(c);
		return result;
	}
	
	/**
	 * Indicates whether the current item can be equipped by the given character
	 * @return indicates yes or no
	 */
	protected abstract boolean canEquip(GameCharacter c);
	
	private void equippedBy(GameCharacter c) {
		if (owner != null)
		{
			String error = String.format("Cannot equip %s.  It is already equippd by %s.", this, owner);
			throw new RuntimeException(error);
		}
		owner = c;
	}
	
	public void unequip() {
		if (owner == null)
		{
			String error = String.format("Attempting to unequip %s when it is not equipped.", this);
			throw new RuntimeException(error);
		}
		lastOwner = owner;
		owner = null;
	}
	
	public GameCharacter getOwner() {
		return owner;
	}
	
	public GameCharacter getLatestOwner() {
		GameCharacter c = getOwner();
		if (c == null)
			c = lastOwner;
		return c;
	}
	
	public Object clone() {
		try {
			Equippable e = (Equippable)super.clone();
			e.itemName = itemName;
			e.owner = null;
			e.lastOwner = null;
			
			e.spriteInit = true;
			e.spriteRight = new Sprite(spriteRight);
			e.spriteUp = new Sprite(spriteUp);
			e.spriteLeft = new Sprite(spriteLeft);
			e.spriteDown = new Sprite(spriteDown);
			return e;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Error cloning Equippable");
		}
	}
}
