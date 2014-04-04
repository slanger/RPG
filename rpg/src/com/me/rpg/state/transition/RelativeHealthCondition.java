package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableFloat;

public class RelativeHealthCondition implements Condition {
	
	private static final long serialVersionUID = 1L;
	
	private MutableFloat healthRatio;
	private GameCharacter targetCharacter;
	private FloatCondition condition;
	
	/**
	 * Creates a condition which tests whether the relative health of the targetCharacter
	 * compares to the targetRatio in the desired way.
	 * @param targetCharacter GameCharacter whose health ratio we care about
	 * @param targetRatio Critical value of the health ratio
	 * @param type How we will compare the true value with the targetRatio
	 */
	public RelativeHealthCondition(GameCharacter targetCharacter, float targetRatio, Comparison type) {
		healthRatio = new MutableFloat();
		this.targetCharacter = targetCharacter;
		condition = new FloatCondition(healthRatio, targetRatio, type);
	}
	
	@Override
	public boolean test() {
		// update healthRatio
		float newRatio = (float)targetCharacter.getHealth() / (float)targetCharacter.getMaxHealth();
		healthRatio.setValue(newRatio);
		return condition.test();
	}

}
