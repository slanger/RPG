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
	
	private boolean attacking;	// indicates whether the weapon is currently being used to attack
	protected float stateTime;		// helps determine 
	
	private Sprite sprite;
	
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
		
		float centerX = charRectangle.getX() + charRectangle.getWidth()/2;
		float centerY = charRectangle.getY() + charRectangle.getHeight()/2;
		float leftX = centerX - sprite.getWidth() / 2;
		float leftY = centerY - sprite.getHeight() / 2;
		float originX = sprite.getOriginX();
		float originY = sprite.getOriginY();
		
		// straight poking
		sprite.setRotation(direction.getDegrees());
		sprite.setPosition(leftX + (getRenderOffset() + charRectangle.getWidth() - 8)*direction.getDx(), leftY + (getRenderOffset() + charRectangle.getHeight() - 8)*direction.getDy());
		
		// slashing
		//float strangeMultiplier = (direction == Direction.RIGHT ? -1 : 1);
		//float extraMove = getRotateMovement(originX+8, originY) * strangeMultiplier;
		//sprite.setRotation(direction.getDegrees() + getDegreeOffset() * strangeMultiplier);
		//sprite.setPosition(leftX + (charRectangle.getWidth() - 4)*direction.getDx() - extraMove*direction.getDy(), leftY + (charRectangle.getHeight() - 4)*direction.getDy() + extraMove*direction.getDx());
		//sprite.setOrigin(0f, sprite.getOriginY());
		
		//System.out.printf("origin: %f, %f\n", sprite.getOriginX(), sprite.getOriginY());
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
	
	protected float getDegreeOffset() {
		if (!attacking)
			return 0f;
		// hardcoded range of 100 degrees
		float degreeRange = 60f;
		return Math.min(degreeRange/2, (degreeRange * stateTime / speed) - degreeRange/2);
	}
	
	protected float getRotateMovement(float originX, float originY) {
		float degree = getDegreeOffset();
		//sin(degree) = opposite / hypo
		float ret = (float)(originX * Math.sin(Math.toRadians(degree)));
		return ret;
	}
	
	protected void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
