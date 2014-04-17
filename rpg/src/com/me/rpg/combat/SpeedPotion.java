package com.me.rpg.combat;

import com.me.rpg.characters.GameCharacter;

public class SpeedPotion extends UsableItem
{
	int potionPower = 0;
	
	public SpeedPotion(String itemName, String itemSpritePath, int width,
			int height, int tileWidth, int tileHeight, int potionPower)
	{
		super(itemName, itemSpritePath, width, height, tileWidth, tileHeight);
		this.potionPower = potionPower;
	}

	@Override
	public void use(GameCharacter user)
	{
		user.setSpeedModifier((float)potionPower);
	}
	
	public int getPower()
	{
		return potionPower;
	}
}
