package com.me.rpg.combat;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Direction;

public abstract class Weapon {
	
	protected float speed;			// how long it takes for the weapon to reach the end of its stroke, in seconds
	protected float fireRate; 		// how long the user must wait after attacking to attack or switch weapons, in seconds 
	protected int range; 			// how far the weapon can reach, in pixels?
	protected int power; 			// how much damage the weapon does, 1:1 with health
	
	protected boolean attacking;	// indicates whether the weapon is currently being used to attack
	protected float stateTime;		// helps determine 
	
	private Sprite sprite;
	
	public void render(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {
		if (!attacking)
			return;
		
		float centerX = charRectangle.getX() + charRectangle.getWidth()/2;
		float centerY = charRectangle.getY() + charRectangle.getHeight()/2;
		float leftX = centerX - sprite.getWidth() / 2;
		float leftY = centerY - sprite.getHeight() / 2;
		
		sprite.setPosition(leftX, leftY);
		//sprite.setOrigin(charRectangle.getWidth()/2, charRectangle.getHeight()/2);
		sprite.setRotation(direction.getDegrees());
		sprite.setPosition(leftX + (getRenderOffset() + charRectangle.getWidth() - 8)*direction.getDx(), leftY + (getRenderOffset() + charRectangle.getHeight() - 8)*direction.getDy());
		sprite.draw(batch);
	}
	
	public void doAttack() {
		if(attacking) {
			return;
		}
		if (stateTime < fireRate) {
			return; // must wait until fireRate allows attack
		}
		attacking = true;
		stateTime = 0f;
	}
	
	public void update(float deltaTime) {
		stateTime += deltaTime;
		if (!attacking) {
			return;
		}
		if (stateTime > speed) {
			stateTime = 0f;
			attacking = false;
			return;
		}
	}
	
	protected float getRenderOffset() {
		if (!attacking) {
			return 0f;
		}
		if (stateTime < speed/2) {
			return range*(stateTime / (speed/2));
		} else {
			return Math.max(0f, range*( (speed - stateTime) / (speed / 2)));
		}
	}
	
	protected void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
