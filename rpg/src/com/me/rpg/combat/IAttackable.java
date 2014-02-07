package com.me.rpg.combat;

public interface IAttackable {
	public void receiveAttack(Weapon weapon);
	public void receiveAttack(Projectile projectile);
	public void receiveDamage(int damage);
	public int getHealth();
}
