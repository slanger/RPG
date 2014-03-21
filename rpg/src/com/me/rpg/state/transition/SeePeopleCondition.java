package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableInt;

public class SeePeopleCondition implements Condition {
	
	private GameCharacter character;
	private IntCondition condition;
	private MutableInt seePeople;
	
	public SeePeopleCondition(GameCharacter character, int target, Comparison type) {
		this.character = character;
		seePeople = new MutableInt();
		condition = new IntCondition(seePeople, target, type);
	}
	
	@Override
	public boolean test() {
		seePeople.setValue(character.getCurrentMap().canSeeCharacters(character, 200).size());
		return condition.test();
	}

}
