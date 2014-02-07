package com.me.rpg.combat;

import com.me.rpg.Character;

public class Poison extends RepeatingEffect {

	private int power;			// how much damage the poison does, 1:1 with health
	
	/**
	 * Initialize a Poison status
	 * @param damageRate How often the damage is applied, in seconds.
	 * @param power How much damage is applied, 1:1 ratio with health points.
	 * @param effectLength How long the effect will last before it wears off naturally
	 */
	public Poison(int power, int totalReptitions, float repeatRate) {
		super("Poison", "Poisoned", totalReptitions, repeatRate); // effectName + verbForm
		this.power = power;
	}

	@Override
	protected void doApplyBeforeActive(Character victim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void applyRepeatEffect(Character victim) {
		victim.receiveDamage(power);
		System.err.printf("%s took %d damage from poison\n", victim.getName(), power);
	}
	
}
