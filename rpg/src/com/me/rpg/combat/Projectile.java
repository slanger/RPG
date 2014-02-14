package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public class Projectile implements Cloneable {
	
	private RangedWeapon firedFrom;
	private Direction firedDirection;
	private boolean fired;
	private boolean finished;
	private int piercing;
	private Coordinate origin;
	private float stateTime;
	
	private Sprite weaponRight, weaponUp, weaponLeft, weaponDown;
	
	private float speedMultiplier;
	private float degreeRotation;
	private int range;
	private int power;
	
	private StatusEffect[] effects;
	
	public Projectile(String name, Texture projectile, int width, int height, int tileWidth, int tileHeight) {
		TextureRegion[][] sheet = TextureRegion.split(projectile, tileWidth, tileHeight);
		weaponRight = new Sprite(sheet[0][0], 0, 0, width, height);
		weaponUp = new Sprite(sheet[0][1], 0, 0, height, width);
		weaponLeft = new Sprite(sheet[0][2], 0, 0, width, height);
		weaponDown = new Sprite(sheet[0][3], 0, 0, height, width);
		
		fired = false;
		finished = false;
		piercing = 1;
		origin = null;
		firedDirection = Direction.RIGHT;
		firedFrom = null;
		stateTime = 0f;
		
		power = 0;
		speedMultiplier = 1.0f;
		range = 0;
		
		effects = new StatusEffect[0];
	}
	
	public StatusEffect[] getEffects() {
		StatusEffect[] partial = firedFrom.getEffects();
		StatusEffect[] ret = new StatusEffect[effects.length + partial.length];
		for (int i = 0; i < effects.length; ++i)
			ret[i] = (StatusEffect)effects[i].clone();
		System.arraycopy(partial, 0, ret, effects.length, partial.length);
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
	
	public void setFiredWeapon(RangedWeapon firedFrom) {
		this.firedFrom = firedFrom;
	}
	
	public RangedWeapon getFiredWeapon() {
		return firedFrom;
	}
	
	public Direction getFiredDirection() {
		return firedDirection;
	}
	
	public void setHasHit() {
		--piercing;
		if (piercing <= 0)
			finished = true;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public boolean isFired() {
		return fired;
	}
	
	public void update(float deltaTime) {
		if (!fired) {
			return;
		}
		if (stateTime > getSpeed() + 0.01) {
			fired = false;
			finished = true;
			return;
		}
		stateTime += deltaTime;
		
		float movement = getRange() * stateTime / getSpeed();
		Sprite sprite = getWeapon();
		sprite.setPosition(origin.getX() + movement * firedDirection.getDx(), origin.getY() + movement * firedDirection.getDy());
		sprite.setRotation(getDegreeRotation() * stateTime / getSpeed());
	}
	
	public int getPower() {
		return power + firedFrom.power;
	}
	
	protected int getRange() {
		return range + firedFrom.range;
	}
	
	protected float getDegreeRotation() {
		return degreeRotation + firedFrom.degreeRotation;
	}
	
	public float getSpeed() {
		return firedFrom.speed * speedMultiplier;
	}
	
	public void render(SpriteBatch batch) {
		if (!fired) {
			return;
		}
		Sprite sprite = getWeapon();
		sprite.draw(batch);
	}
	
	public Sprite getWeapon() {
		switch(firedDirection) {
			case UP:
				return weaponUp;
			case DOWN:
				return weaponDown;
			case LEFT:
				return weaponLeft;
			case RIGHT:
				return weaponRight;
		}
		throw new RuntimeException("Invalid direction attempted to retrieve for projectile: " + firedDirection);
	}
	
	public Projectile makeAttackingCopy(Direction firedDirection, Coordinate origin, RangedWeapon firedFrom) {
		Projectile p = null;
		try {
			p = (Projectile)super.clone();
			p.weaponUp = new Sprite(weaponUp);
			p.weaponDown = new Sprite(weaponDown);
			p.weaponLeft = new Sprite(weaponLeft);
			p.weaponRight = new Sprite(weaponRight);
			p.firedFrom = firedFrom;
			p.firedDirection = firedDirection;
			p.origin = Coordinate.copy(origin);
			p.fired = true;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException("Something really unexpected happened :(");
		}
		return p;
	}

	public Rectangle getSpriteBounds() {
		// TODO Auto-generated method stub
		return getWeapon().getBoundingRectangle();
	}
}
