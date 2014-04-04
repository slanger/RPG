package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableInt;

public class HearPeopleCondition implements Condition {

	private static final long serialVersionUID = 6473945699587610769L;

	private GameCharacter character;
	private IntCondition condition;
	private MutableInt hearPeople;
	
	public HearPeopleCondition(GameCharacter character, int target, Comparison type) {
		this.character = character;
		hearPeople = new MutableInt();
		condition = new IntCondition(hearPeople, target, type);
	}
	
	@Override
	public boolean test() {
		hearPeople.setValue(character.getCurrentMap().canHearCharacters(character).size());
		return condition.test();
	}

}
