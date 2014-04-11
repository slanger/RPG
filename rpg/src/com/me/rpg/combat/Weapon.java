package com.me.rpg.combat;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Direction;

public abstract class Weapon extends Equippable
{

	private static final long serialVersionUID = -8054453214554512536L;

	protected float speed;			// how long it takes for the weapon to reach the end of its stroke, in seconds
	protected float fireRate; 		// how long the user must wait after attacking to attack or switch weapons, in seconds 
	protected int range; 			// how far the weapon can reach, in pixels?
	protected int power; 			// how much damage the weapon does, 1:1 with health
	
	private boolean attacking;		// indicates whether the weapon is currently being used to attack
	protected float stateTime;		// helps determine animation parts
	private Direction lastDirection; // last direction weapon was facing when used
	
	protected StatusEffect[] effects; // list of status effects this weapon applies
	
	public int getPower() {
		return power;
	}
	
	public void setPower(int power)
	{
		this.power = power;
	}
	
	public Direction getLastDirection() {
		return lastDirection;
	}
	
	protected Weapon(String weaponName, String weaponSpritePath, int width,
			int height, int tileWidth, int tileHeight, float speed,
			float fireRate, int range, int power)
	{
		super(weaponName, weaponSpritePath, width, height, tileWidth, tileHeight);
		
		this.speed = speed;
		this.fireRate = fireRate;
		this.range = range;
		this.power = power;
		
		stateTime = 0f;
		lastDirection = Direction.DOWN;
		effects = new StatusEffect[0];
		
		setAttacking(false);
	}
	
	public float getSpeed() {
		return speed;
	}
	
	/**
	 * Returns a copy of the status effects for this weapon
	 * @param size Desired size of output array
	 * @return Array of effects. May be empty.
	 */
	public StatusEffect[] getEffects() {
		StatusEffect[] ret = new StatusEffect[effects.length];
		for (int i = 0; i < effects.length; ++i) {
			ret[i] = (StatusEffect)effects[i].clone();
		}
		return ret;
	}
	
	public void addEffect(StatusEffect effect) {
		if (effect == null)
			throw new RuntimeException("Don't add null effects, you punk.");
		StatusEffect[] temp = new StatusEffect[effects.length+1];
		System.arraycopy(effects, 0, temp, 0, effects.length);
		temp[effects.length] = effect;
		effects = temp;
	}
	
	protected Sprite getWeaponSprite() {
		return getItemSprite(lastDirection);
	}
	
	public Rectangle getSpriteBounds() {
		return getWeaponSprite().getBoundingRectangle();
	}
	
	public Rectangle getHitBox() {
		doUpdate(0f);
		return getWeaponSprite().getBoundingRectangle();
	}
	
	public boolean isAttacking() {
		return attacking;
	}
	
	protected void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
	
	public void quickFinishAttack() {
		attacking = false;
	}
	
	public void render(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {
		if (!attacking)
			return;
		// TODO: this needs to be set elsewhere, maybe also fix the hacky call in MeleeWeapon
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
	
	protected abstract void doAttackCleanup();
	
	public void update(float deltaTime) {
		stateTime += deltaTime;
		doUpdate(deltaTime);
		if (!attacking) {
			return;
		}
		if (stateTime > doGetWait()) {
			stateTime = 0f;
			attacking = false;
			doAttackCleanup();
			return;
		}
	}
	
	protected abstract void doUpdate(float deltaTime);

	public abstract void switchStyle();
	
	public Object clone() {
		Weapon w = (Weapon)super.clone();
		w.lastDirection = lastDirection;
		w.effects = getEffects();
		return w;
	}

	public int getRange() {
		return range;
	}
}
