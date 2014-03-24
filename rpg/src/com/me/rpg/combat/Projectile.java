package com.me.rpg.combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ScreenHandler;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;

public class Projectile implements Cloneable, Serializable
{

	private static final long serialVersionUID = -7980922753062226654L;

	private String name;
	private String spritePath;
	private int width, height;
	private int tileWidth, tileHeight;

	private RangedWeapon firedFrom = null;
	private Direction firedDirection = Direction.RIGHT;
	private boolean fired = false;
	private boolean finished = false;
	private int piercing = 1;
	private Coordinate origin = null;
	private float stateTime = 0f;
	
	private transient Sprite weaponRight, weaponUp, weaponLeft, weaponDown;
	
	private float speedMultiplier = 1.0f;
	private float degreeRotation;
	private int range = 0;
	private int power = 0;
	
	private StatusEffect[] effects = new StatusEffect[0];
	
	public Projectile(String name, String spritePath, int width, int height, int tileWidth, int tileHeight)
	{
		this.name = name;
		this.spritePath = spritePath;
		this.width = width;
		this.height = height;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;

		create();
	}

	private void create()
	{
		Texture projectile = ScreenHandler.manager.get(spritePath, Texture.class);
		TextureRegion[][] sheet = TextureRegion.split(projectile, tileWidth, tileHeight);
		weaponRight = new Sprite(sheet[0][0], 0, 0, width, height);
		weaponUp = new Sprite(sheet[0][1], 0, 0, height, width);
		weaponLeft = new Sprite(sheet[0][2], 0, 0, width, height);
		weaponDown = new Sprite(sheet[0][3], 0, 0, height, width);
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		create();
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
