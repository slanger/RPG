package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.me.rpg.Coordinate;
import com.me.rpg.Direction;

public class Projectile {
	
	private Sprite sprite;
	private RangedWeapon firedFrom;
	private Direction direction;
	private float stateTime;
	private boolean fired;
	private Coordinate origin;
	
	public Projectile(Texture projectile, int width, int height, int tileWidth, int tileHeight) {
		TextureRegion[][] parts = TextureRegion.split(projectile, tileWidth, tileHeight);
		sprite = new Sprite(projectile, 0, 0, width, height);
	}
	
	public boolean isFired() {
		return fired;
	}
	
	public void render(SpriteBatch batch) {
		if (!isFired()) {
			return;
		}
		
	}
	
	public void update(float deltaTime) {
		if (!isFired()) {
			return;
		}
		stateTime += deltaTime;
	}
}
