package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Direction;
import com.me.rpg.maps.Map;
import com.me.rpg.Character;

public abstract class Weapon implements Cloneable {
	
	protected Character owner;
	
	protected float speed;			// how long it takes for the weapon to reach the end of its stroke, in seconds
	protected float fireRate; 		// how long the user must wait after attacking to attack or switch weapons, in seconds 
	protected int range; 			// how far the weapon can reach, in pixels?
	protected int power; 			// how much damage the weapon does, 1:1 with health
	
	private boolean attacking;	// indicates whether the weapon is currently being used to attack
	protected float stateTime;		// helps determine animation parts
	private Direction lastDirection; // last direction weapon was facing when used

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
		lastDirection = Direction.DOWN;
	}
	
	public void equippedBy(Character c) {
		if (owner != null)
			throw new RuntimeException("Cannot equip weapon.  It is already equippd by " + owner.toString());
		owner = c;
	}
	
	public void unequip() {
		owner = null;
	}
	
	public Character getOwner() {
		return owner;
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
	
	public Rectangle getSpriteBounds() {
		return getWeapon(lastDirection).getBoundingRectangle();
	}
	
	public boolean isAttacking() {
		return attacking;
	}
	
	protected void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
	
	public void render(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {
		if (!attacking)
			return;
		lastDirection = direction;
		doRender(charRectangle, direction, batch);
	}
	
	protected abstract void doRender(Rectangle charRectangle, Direction direction,
			SpriteBatch batch);
	
	public void attack(Map map, Direction direction, Rectangle attackOrigin) {
		if (attacking) {
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
	
	protected abstract float doGetWait();
	
	public void update(float deltaTime) {
		stateTime += deltaTime;
		if (!attacking) {
			return;
		}
		if (stateTime > doGetWait()) {
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
