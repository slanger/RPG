package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Coordinate;
import com.me.rpg.Direction;
import com.me.rpg.Map;

public class Projectile implements Cloneable {
	
	private RangedWeapon firedFrom;
	private Direction firedDirection;
	private boolean fired;
	private boolean finished;
	private Coordinate origin;
	private float stateTime;
	private float degreeRotation;
	
	private Sprite weaponRight, weaponUp, weaponLeft, weaponDown;
	
	// obtained from passed in RangedWeapon
	private float speed;
	private int range;
	
	public Projectile(String name, Texture projectile, int width, int height, int tileWidth, int tileHeight, RangedWeapon weapon) {
		TextureRegion[][] sheet = TextureRegion.split(projectile, tileWidth, tileHeight);
		weaponRight = new Sprite(sheet[0][0], 0, 0, width, height);
		weaponUp = new Sprite(sheet[0][1], 0, 0, height, width);
		weaponLeft = new Sprite(sheet[0][2], 0, 0, width, height);
		weaponDown = new Sprite(sheet[0][3], 0, 0, height, width);
		
		fired = false;
		finished = false;
		origin = null;
		firedDirection = Direction.RIGHT;
		firedFrom = weapon;
		stateTime = 0f;
		
		speed = -1f;
		range = -1;
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
		if (stateTime > speed + 0.01) {
			fired = false;
			finished = true;
			return;
		}
		stateTime += deltaTime;
		
		float movement = range * stateTime / speed;
		Sprite sprite = getWeapon();
		sprite.setPosition(origin.getX() + movement * firedDirection.getDx(), origin.getY() + movement * firedDirection.getDy());
		sprite.setRotation(degreeRotation * stateTime / speed);
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
			p.speed = firedFrom.speed;
			p.range = firedFrom.range;
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
