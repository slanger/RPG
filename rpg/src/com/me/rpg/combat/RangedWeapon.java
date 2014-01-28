package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RangedWeapon extends Weapon {
	
	Projectile equippedAmmo;
	
	public RangedWeapon(String string, Texture rangedSprite, int width, int height, int tileWidth,
			int tileHeight, float animationDuration) {
		
		TextureRegion[][] sheet = TextureRegion.split(rangedSprite, tileWidth, tileHeight);
		Sprite bow = new Sprite(sheet[0][0], 0, 0, width, height);
		setSprite(bow);
		
		setStatsDefault();
	}
	
	protected void setStatsDefault() {
		speed = 0.3f;
		fireRate = 0.3f;
		range = 100;
		power = 50;
		setAttacking(false);
		stateTime = 0f;
	}
}
