package com.me.rpg.combat;

import com.me.rpg.characters.GameCharacter;

public abstract class RepeatingEffect extends StatusEffect {
	
	private int appliedCount;
	private float repeatRate;
	
	protected RepeatingEffect(String effectName, String verbForm, int totalReptitions,
			float repeatRate) {
		super(effectName, verbForm, repeatRate*totalReptitions + repeatRate/2);
		
		this.repeatRate = repeatRate;
		appliedCount = 0;
	}

	@Override
	protected void doApplyBeforeActive(GameCharacter victim) {
		// TODO Auto-generated method stub

	}

	@Override
	protected final void doApplyAfterActive(GameCharacter victim) {
		float timePassed = getTimePassed();
		while (timePassed > (appliedCount + 1) * repeatRate) {
			++appliedCount;
			applyRepeatEffect(victim);
		}
	}
	
	protected abstract void applyRepeatEffect(GameCharacter victim);

}
