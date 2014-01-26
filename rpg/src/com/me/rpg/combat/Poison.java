package com.me.rpg.combat;

public class Poison extends StatusEffect {

	private float damageRate;	// time in seconds between negative effects of the poison
	private int power;			// how much damage the poison does, 1:1 with health
	private float effectLimit; 	// time in seconds until effect wears off naturally
	
	public Poison(float damageRate, int power, float effectLimit) {
		super("Poison", "Poisoned"); // effectName + verbForm
		this.damageRate = damageRate;
		this.power = power;
		this.effectLimit = effectLimit;
	}

	@Override
	public boolean applyStatusEffect(float deltaTime, Character victim) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected Object clone() {
		
	}
}
