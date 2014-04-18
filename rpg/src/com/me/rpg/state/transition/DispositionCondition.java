package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableInt;

public class DispositionCondition implements Condition {

	private static final long serialVersionUID = 8860595982328307737L;
	
	private GameCharacter character;
	private IntCondition condition;
	private MutableInt disposition;
	
	public DispositionCondition(GameCharacter character, int target, Comparison type) {
		this.character = character;
		disposition = new MutableInt();
		condition = new IntCondition(disposition, target, type);
	}
	
	@Override
	public boolean test() {
		disposition.setValue(character.getDispositionValue());
		return condition.test();
	}

}
