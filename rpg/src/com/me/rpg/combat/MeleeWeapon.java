package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MeleeWeapon extends Weapon {
	
	public MeleeWeapon(String string, Texture swordSprite, int width, int height, int tileWidth,
			int tileHeight, float animationDuration) {
		
		TextureRegion[][] sheet = TextureRegion.split(swordSprite, tileWidth, tileHeight);
		Sprite sword = new Sprite(sheet[0][0], 0, 0, width, height);
		setSprite(sword);
		
		setStatsDefault();
	}
	
	protected void setStatsDefault() {
		speed = 0.3f;
		fireRate = 0.3f;
		range = 15;
		power = 50;
		setAttacking(false);
		stateTime = 0f;
	}
	
}
