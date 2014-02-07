package com.me.rpg.combat;

import com.me.rpg.Character;

public abstract class StatusEffect implements Cloneable {
	
	private StatusEffect parent;
	private boolean isPrototype;
	
	private String effectName;
	private String verbForm;
	
	private float timeLeft; 	// in seconds
	private float immunePeriod; // in seconds
	private float timePassed; 	// in seconds
	private boolean active; 	// enables the status effect to be applied
	
	protected StatusEffect(String effectName, String verbForm, float timeLeft) {
		this(effectName, verbForm, timeLeft, timeLeft);
	}
	
	protected StatusEffect(String effectName, String verbForm, float timeLeft,
			float immunePeriod) {
		this.effectName = effectName;
		this.verbForm = verbForm;
		this.timeLeft = timeLeft;
		this.immunePeriod = immunePeriod;
		timePassed = 0f;
		
		parent = null;
		active = false;
		isPrototype = true;
	}
	
	public String getEffectName() {
		return effectName;
	}
	
	public String getVerbForm() {
		return verbForm;
	}
	
	public float getImmunePeriod() {
		return immunePeriod;
	}
	
	/**
	 * Returns a reference to the parent of this cloned status effect
	 * @return Parent of this clone.
	 */
	public StatusEffect getParentRef() {
		return parent;
	}
	
	public boolean hasWornOff() {
		return timeLeft <= 0f;
	}
	
	protected float getTimeLeft() {
		return timeLeft;
	}
	
	protected float getTimePassed() {
		return timePassed;
	}
	
	public final void applyStatusEffect(float deltaTime, Character victim) {
		prototypeCheck();
		if (!active) {
			doApplyBeforeActive(victim);
			return;
		}
		timeLeft -= deltaTime;
		timePassed += deltaTime;
		if (timeLeft < 0)
			active = false;
		doApplyAfterActive(victim);
	}
	
	private void prototypeCheck() {
		if (isPrototype)
			throw new RuntimeException("You cannot use this function in prototypical instances.");
	}
	
	/**
	 * Hook for code to be applied to victim before effect is active
	 * @param victim Character that effect is applied to
	 */
	protected abstract void doApplyBeforeActive(Character victim);
	
	/**
	 * Hook for code to be applied to victim while effect is active
	 * @param victim Character that effect is applied to
	 */
	protected abstract void doApplyAfterActive(Character victim);
	
	public Object clone() {
		try {
			StatusEffect effect = (StatusEffect)super.clone();
			effect.parent = this;
			effect.isPrototype = false;
			effect.active = true;
			return effect;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("StatusEffect clone failed" + e.toString());
		}
	}
}
