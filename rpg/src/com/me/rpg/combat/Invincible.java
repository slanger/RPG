package com.me.rpg.combat;

import com.me.rpg.characters.GameCharacter;

public class Invincible extends StatusEffect
{

	private static final long serialVersionUID = -6556348928153296852L;

	protected Invincible(String effectName, String verbForm, float effectLength) {
		super(effectName, verbForm, effectLength);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doApplyBeforeActive(GameCharacter victim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doApplyAfterActive(GameCharacter victim) {
		// TODO Auto-generated method stub
		
	}

}
