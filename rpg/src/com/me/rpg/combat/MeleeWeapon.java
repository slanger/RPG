package com.me.rpg.combat;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Direction;

public class MeleeWeapon extends Weapon
{

	private static final long serialVersionUID = 9043712495403662726L;

	private enum AttackStyle {
		POKING,
		SLASHING
	}
	
	private AttackStyle currentStyle;
	
	public MeleeWeapon(String weaponName, String weaponSpritePath, int width,
			int height, int tileWidth, int tileHeight)
	{
		super(weaponName, weaponSpritePath, width, height, tileWidth, tileHeight, 0.2f, 0.2f, 12, 2);
		// range of 12 is good for a melee weapon of length 32

		currentStyle = AttackStyle.POKING;
	}
	
	public void switchStyle() {
		switch (currentStyle) {
			case POKING:
				currentStyle = AttackStyle.SLASHING;
				break;
			case SLASHING:
				currentStyle = AttackStyle.POKING;
				break;
		}
	}

	@Override
	protected void doRender(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {

		Sprite sprite = getWeaponSprite();
		doUpdate(0f);
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
	
	protected void doUpdate(float deltaTime) {
		GameCharacter owner = getOwner();
		Rectangle charRectangle = owner.getHitBox();
		Sprite sprite = getWeaponSprite();
		Direction direction = getLastDirection();
		
		float centerX = charRectangle.getX() + charRectangle.getWidth()/2;
		float centerY = charRectangle.getY() + charRectangle.getHeight()/2;
		float bottomLeftX = centerX - sprite.getWidth() / 2;
		float bottomLeftY = centerY - sprite.getHeight() / 2;
		
		switch (currentStyle) {
			// straight poking
			case POKING:
				sprite.setPosition(bottomLeftX + (getRenderOffset() + charRectangle.getWidth() - 8)*direction.getDx(), bottomLeftY + (getRenderOffset() + charRectangle.getHeight() - 8)*direction.getDy());
				break;
			
			// slashing
			case SLASHING:
				float strangeMultiplier = (direction == Direction.RIGHT ? -1 : 1);
				sprite.setRotation(getDegreeOffset() * strangeMultiplier);
				sprite.setPosition(bottomLeftX + (charRectangle.getWidth() - 4)*direction.getDx(), bottomLeftY + (charRectangle.getHeight() - 4)*direction.getDy());
				sprite.setOrigin(sprite.getWidth()/2 * (-direction.getDx() + 1), sprite.getHeight()/2 * (-direction.getDy() + 1));
				break;
		}
	}
	
	protected void doAttackCleanup() {
		getItemSprite(Direction.UP).setRotation(0);
		getItemSprite(Direction.LEFT).setRotation(0);
		getItemSprite(Direction.RIGHT).setRotation(0);
		getItemSprite(Direction.DOWN).setRotation(0);
	}
	
	protected void doAttack(Map map, Direction direction, Rectangle attackOrigin) {
		
	}

	@Override
	protected float doGetWait() {
		return speed;
	}

	@Override
	/*
	 * (non-Javadoc) TODO: Determine if we want to have 'levels' or some way to limit weapon equipping
	 * @see com.me.rpg.combat.Equippable#canEquip(com.me.rpg.Character)
	 */
	protected boolean canEquip(GameCharacter c) {
		return true;
	}
}
