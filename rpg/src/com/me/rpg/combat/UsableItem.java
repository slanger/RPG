package com.me.rpg.combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.me.rpg.ScreenHandler;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Direction;

public abstract class UsableItem implements Serializable
{

	private static final long serialVersionUID = -6832532611374862887L;
	
	private String itemName;
	private String itemSpritePath;
	private int width, height;
	private int tileWidth, tileHeight;


	private transient Sprite sprite;

	protected UsableItem(String itemName, String itemSpritePath, int width,
			int height, int tileWidth, int tileHeight)
	{
		this.itemName = itemName;
		this.itemSpritePath = itemSpritePath;
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		create();
	}

	public void create()
	{
		Texture itemTexture = ScreenHandler.manager.get(itemSpritePath, Texture.class);
		sprite = new Sprite(itemTexture);
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		create();
	}

	/**
	 * Get the sprite corresponding to the passed in direction
	 * @param direction Desired direction of sprite
	 * @return Sprite for that direction
	 */
	public Sprite getItemSprite()
	{
		return sprite;
	}
	
	public String getName()
	{
		return itemName;
	}

	public abstract void use(GameCharacter user);
	
	public abstract int getPower();
}
