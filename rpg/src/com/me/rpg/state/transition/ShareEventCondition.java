package com.me.rpg.state.transition;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableBoolean;

public class ShareEventCondition implements Condition {

	private static final long serialVersionUID = -7711002184449548848L;
	private GameCharacter character;
	private BooleanCondition condition;
	private MutableBoolean shareEvent;
	
	public ShareEventCondition(GameCharacter character, boolean target, Comparison type) {
		this.character = character;
		shareEvent = new MutableBoolean();
		condition = new BooleanCondition(shareEvent, target);
	}
	
	@Override
	public boolean test() {
		shareEvent.setValue(character.getWantsToShareKnowledge());
		return condition.test();
	}

}
