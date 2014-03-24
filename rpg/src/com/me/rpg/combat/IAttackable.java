package com.me.rpg.combat;

import java.io.Serializable;

public interface IAttackable extends Serializable
{

	public void receiveAttack(Weapon weapon);
	public void receiveAttack(Projectile projectile);
	public void receiveDamage(int damage);
	public int getHealth();

}
