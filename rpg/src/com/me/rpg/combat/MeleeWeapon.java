package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Direction;
import com.me.rpg.Map;

public class MeleeWeapon extends Weapon {
	
	private enum AttackStyle {
		POKING,
		SLASHING
	}
	
	private AttackStyle currentStyle;
	
	public MeleeWeapon(String string, Texture swordSprite, int width, int height, int tileWidth,
			int tileHeight) {
		super(string, swordSprite, width, height, tileWidth, tileHeight);
		setStatsDefault();
	}
	
	protected void setStatsDefault() {
		speed = 0.2f;
		fireRate = 0.2f;
		range = 12; // 12 is good for a melee weapon of length 32
		power = 50;
		setAttacking(false);
		stateTime = 0f;
		currentStyle = AttackStyle.POKING;
	}
	
	public void switchStyle() {
		switch (currentStyle) {
			case POKING:
				currentStyle = AttackStyle.SLASHING;
				break;
			case SLASHING:
				currentStyle = AttackStyle.POKING;
				getWeaponUp().setRotation(0);
				getWeaponDown().setRotation(0);
				getWeaponRight().setRotation(0);
				getWeaponLeft().setRotation(0);
				break;
		}
	}

	@Override
	protected void doRender(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {

		Sprite sprite = getWeapon(direction);
		
		float centerX = charRectangle.getX() + charRectangle.getWidth()/2;
		float centerY = charRectangle.getY() + charRectangle.getHeight()/2;
		float leftX = centerX - sprite.getWidth() / 2;
		float leftY = centerY - sprite.getHeight() / 2;
		
		switch (currentStyle) {
			// straight poking
			case POKING:
				sprite.setPosition(leftX + (getRenderOffset() + charRectangle.getWidth() - 8)*direction.getDx(), leftY + (getRenderOffset() + charRectangle.getHeight() - 8)*direction.getDy());
				break;
			
			// slashing
			case SLASHING:
				float strangeMultiplier = (direction == Direction.RIGHT ? -1 : 1);
				sprite.setRotation(getDegreeOffset() * strangeMultiplier);
				sprite.setPosition(leftX + (charRectangle.getWidth() - 4)*direction.getDx(), leftY + (charRectangle.getHeight() - 4)*direction.getDy());
				sprite.setOrigin(sprite.getWidth()/2 * (-direction.getDx() + 1), sprite.getHeight()/2 * (-direction.getDy() + 1));
				break;
		}
		sprite.draw(batch);
	}
	
	private float getRenderOffset() {
		if (!isAttacking()) {
			return 0f;
		}
		if (stateTime < speed/2) {
			return range*(stateTime / (speed/2));
		} else {
			return Math.max(0f, range*( (speed - stateTime) / (speed / 2)));
		}
	}
	
	private float getDegreeOffset() {
		if (!isAttacking())
			return 0f;
		// hardcoded range of 100 degrees
		float degreeRange = 100f;
		return Math.min(degreeRange/2, (degreeRange * stateTime / speed) - degreeRange/2);
	}
	
	protected void doUpdate() {
		
	}
	
	protected void doAttack(Map map, Direction direction, Rectangle attackOrigin) {
		
	}

	@Override
	protected float doGetWait() {
		return speed;
	}
	
}
