package com.me.rpg.combat;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Direction;

public class Shield extends Equippable implements IAttackable
{

	private static final long serialVersionUID = -7805024725475369090L;

	private int health;
	
	private Direction lastDirection; // last direction shield was facing when used
	
	public Shield(String shieldName, String shieldSpritePath, int width,
			int height, int tileWidth, int tileHeight)
	{
		super(shieldName, shieldSpritePath, width, height, tileWidth, tileHeight);
		health = 100;
	}
	
	protected Sprite getShieldSprite() {
		return getItemSprite(lastDirection);
	}
	
	public void render(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {
		
		Sprite sprite = getItemSprite(direction);

		if(direction == Direction.LEFT)
		{
			sprite = getItemSprite(Direction.RIGHT);
		}
		if(direction == Direction.RIGHT)
		{
			sprite = getItemSprite(Direction.LEFT);
		}
		
		float centerX = charRectangle.getX() + charRectangle.getWidth()/2;
		float centerY = charRectangle.getY() + charRectangle.getHeight()/2;
		float bottomLeftX = centerX - sprite.getWidth() / 2;
		float bottomLeftY = centerY - sprite.getHeight() / 2;
		
		// TODO: come up with a better value for protrusion --> the amount that the shield sticks out from character
		// float protrusion = sprite.getWidth()/6;
		
		// This line is safe because Shield is always rendered relative to equipping character
		sprite.setPosition(bottomLeftX, bottomLeftY);
		sprite.draw(batch);
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
