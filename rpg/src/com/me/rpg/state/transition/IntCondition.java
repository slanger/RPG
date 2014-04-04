package com.me.rpg.state.transition;

import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableInt;

public class IntCondition implements Condition {

	private static final long serialVersionUID = -8403029194641936078L;

	private MutableInt mutable;
	private int target;
	private Comparison type;
	
	public IntCondition(MutableInt mutable, int target, Comparison type) {
		this.mutable = mutable;
		this.target = target;
		this.type = type;
	}
	
	
	@Override
	public boolean test() {
		int value = mutable.getValue();
		switch (type) {
		case EQUALS:
			return value == target;
		case NOTEQUALS:
			return value != target;
		case LESS:
			return value < target;
		case LESSEQ:
			return value <= target;
		case GREATER:
			return value > target;
		case GREATEREQ:
			return value >= target;
		}
		throw new RuntimeException("Transition error: " + type + " is not a valid comparison type.");
	}

}
