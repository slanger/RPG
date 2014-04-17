package com.me.rpg.combat;

import com.me.rpg.characters.GameCharacter;

public class HealthPotion extends UsableItem
{
	int healingPower;
	
	public HealthPotion(String itemName, String itemSpritePath, int width,
			int height, int tileWidth, int tileHeight, int healingPower)
	{
		super(itemName, itemSpritePath, width, height, tileWidth, tileHeight);
		this.healingPower = healingPower;
	}

	@Override
	public void use(GameCharacter user)
	{
		user.heal(healingPower);
	}

	@Override
	public int getPower()
	{
		return healingPower;
	}
	
}
