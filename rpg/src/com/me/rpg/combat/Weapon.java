package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Direction;
import com.me.rpg.Map;

public abstract class Weapon implements Cloneable {
	
	protected float speed;			// how long it takes for the weapon to reach the end of its stroke, in seconds
	protected float fireRate; 		// how long the user must wait after attacking to attack or switch weapons, in seconds 
	protected int range; 			// how far the weapon can reach, in pixels?
	protected int power; 			// how much damage the weapon does, 1:1 with health
	
	private boolean attacking;	// indicates whether the weapon is currently being used to attack
	protected float stateTime;		// helps determine animation parts

	private Sprite weaponRight, weaponUp, weaponLeft, weaponDown;
	
	public Weapon(String string, Texture weaponSprite, int width, int height, int tileWidth,
			int tileHeight) {
		// IMPORTANT: Expects images to be in order "right up left down"
		//			AND dimensions for right -- up are reversed
		//			AND that given dimensions are for right
		TextureRegion[][] sheet = TextureRegion.split(weaponSprite, tileWidth, tileHeight);
		weaponRight = new Sprite(sheet[0][0], 0, 0, width, height);
		weaponUp = new Sprite(sheet[0][1], 0, 0, height, width);
		weaponLeft = new Sprite(sheet[0][2], 0, 0, width, height);
		weaponDown = new Sprite(sheet[0][3], 0, 0, height, width);
	}
	
	protected Sprite getWeaponRight() {
		return weaponRight;
	}

	protected void setWeaponRight(Sprite weaponRight) {
		this.weaponRight = weaponRight;
	}

	protected Sprite getWeaponUp() {
		return weaponUp;
	}

	protected void setWeaponUp(Sprite weaponUp) {
		this.weaponUp = weaponUp;
	}

	protected Sprite getWeaponLeft() {
		return weaponLeft;
	}

	protected void setWeaponLeft(Sprite weaponLeft) {
		this.weaponLeft = weaponLeft;
	}

	protected Sprite getWeaponDown() {
		return weaponDown;
	}

	protected void setWeaponDown(Sprite weaponDown) {
		this.weaponDown = weaponDown;
	}
	
	protected Sprite getWeapon(Direction direction) {
		switch(direction) {
			case UP:
				return getWeaponUp();
			case DOWN:
				return getWeaponDown();
			case LEFT:
				return getWeaponLeft();
			case RIGHT:
				return getWeaponRight();
		}
		throw new RuntimeException("Invalid direction attempted to retrieve for weapon: " + direction);
	}
	
	protected boolean isAttacking() {
		return attacking;
	}
	
	protected void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
	
	public void render(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {
		if (!attacking)
			return;
		
		doRender(charRectangle, direction, batch);
	}
	
	protected abstract void doRender(Rectangle charRectangle, Direction direction,
			SpriteBatch batch);
	
	public void attack(Map map, Direction direction, Rectangle attackOrigin) {
		if(attacking) {
			return;
		}
		if (stateTime < fireRate) {
			return; // must wait until fireRate allows attack
		}
		attacking = true;
		stateTime = 0f;
		doAttack(map, direction, attackOrigin);
	}
	
	protected abstract void doAttack(Map map, Direction direction, Rectangle attackOrigin);
	
	public void update(float deltaTime) {
		stateTime += deltaTime;
		if (!attacking) {
			return;
		}
		if (stateTime > fireRate) {
			stateTime = 0f;
			attacking = false;
			return;
		}
		doUpdate();
	}
	
	protected abstract void doUpdate();

	public abstract void switchStyle();
	
	protected Object clone() throws CloneNotSupportedException {
		Weapon w = (Weapon)super.clone();
		w.weaponUp = new Sprite(weaponUp);
		w.weaponDown = new Sprite(weaponDown);
		w.weaponLeft = new Sprite(weaponLeft);
		w.weaponRight = new Sprite(weaponRight);
		return w;
	}
	
}
