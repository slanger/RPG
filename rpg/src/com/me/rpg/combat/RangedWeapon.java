package com.me.rpg.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.Coordinate;
import com.me.rpg.Direction;
import com.me.rpg.maps.Map;

public class RangedWeapon extends Weapon {
	
	private Projectile equippedAmmo;
	private int ammoCount;
	
	public RangedWeapon(String string, Texture rangedSprite, int width, int height, int tileWidth,
			int tileHeight) {
		super(string, rangedSprite, width, height, tileWidth, tileHeight);
		setStatsDefault();
	}
	
	protected void setStatsDefault() {
		// note:  pixel/second is range/speed
		// this should probably be faster than unit movement
		speed = 0.5f;
		fireRate = 0.2f;
		range = 250;
		power = 50;
		setAttacking(false);
		stateTime = 0f;
	}
	
	public void switchStyle() {
		
	}
	
	public void equipProjectile(Projectile projectile, int ammoCount) {
		if (ammoCount < 0)
			throw new RuntimeException("You can't equip negative ammo, you punk."); // LOL
		if (projectile == null)
			throw new RuntimeException("You can't equip a NULL reference, you punkish punky punk"); // Double LOL
		
		this.equippedAmmo = projectile;
		this.ammoCount = ammoCount;
	}

	@Override
	protected void doRender(Rectangle charRectangle, Direction direction,
			SpriteBatch batch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doUpdate() {
		
	}

	@Override
	protected void doAttack(Map map, Direction direction, Rectangle attackOrigin) {
		if (ammoCount == 0) {
			return; // can't fire with no ammo
		}
		--ammoCount;
		float originX = attackOrigin.getX() + attackOrigin.getWidth()/2 * (direction.getDx() + 1);
		float originY = attackOrigin.getY() + attackOrigin.getHeight()/2 * (direction.getDy() + 1);
		Coordinate origin = new Coordinate(originX, originY);
		Projectile firedCopy = equippedAmmo.makeAttackingCopy(direction, origin, this);
		map.addProjectile(firedCopy);
	}

	@Override
	protected float doGetWait() {
		return 0f;
	}

}
