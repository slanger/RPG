package com.me.rpg.combat;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Direction;
import com.me.rpg.characters.GameCharacter;

public class Shield extends Equippable implements IAttackable {
	
	private int health;
	
	private Direction lastDirection; // last direction shield was facing when used
	
	public Shield(String shieldName) {
		super(shieldName);
		health = 100;
	}
	
	protected Sprite getShieldSprite() {
		return getItemSprite(lastDirection);
	}
	
	public void render(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {
/*
		Sprite sprite = getShield(direction);
		
		float centerX = charRectangle.getX() + charRectangle.getWidth()/2;
		float centerY = charRectangle.getY() + charRectangle.getHeight()/2;
		float bottomLeftX = centerX - sprite.getWidth() / 2;
		float bottomLeftY = centerY - sprite.getHeight() / 2;
		
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
		sprite.draw(batch);*/
	}
	
	/**
	 * Returns whether the shield should be rendered 
	 * @return
	 */
	public boolean renderForeground() {
		return false;
	}
	
	@Override
	public void receiveAttack(Weapon weapon) {
		// TODO Auto-generated method stub
		System.err.printf("shield of %s blocks weapon attack by %s\n", getOwner().getName(), weapon.getOwner().getName());
	}

	@Override
	public void receiveAttack(Projectile projectile) {
		// TODO Auto-generated method stub
		System.err.printf("shield of %s blocks projectile shot by %s\n", getOwner().getName(), projectile.getFiredWeapon().getOwner().getName());
	}

	@Override
	protected boolean canEquip(GameCharacter c) {
		return health != 0;
	}

	@Override
	public void receiveDamage(int damage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}
}
