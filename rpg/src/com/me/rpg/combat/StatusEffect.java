package com.me.rpg.combat;

public abstract class StatusEffect implements Cloneable {

	private String effectName;
	private String verbForm;
	
	public StatusEffect(String effectName, String verbForm) {
		this.effectName = effectName;
		this.verbForm = verbForm;
	}
	
	public String getEffectName() {
		return effectName;
	}
	
	public String getVerbForm() {
		return verbForm;
	}
	
	public abstract boolean applyStatusEffect(float deltaTime, Character victim);
}
