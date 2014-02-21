package com.me.rpg.state.transition;

import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableFloat;

public class FloatTransition extends Transition {
	
	private MutableFloat mutable;
	private float target;
	private Comparison type;
	private static final float EPS = 0.0000001f;
	
	public FloatTransition(MutableFloat mutable, float target, Comparison type) {
		this.mutable = mutable;
		this.target = target;
		this.type = type;
	}
	
	@Override
	public boolean doTransition() {
		float value = mutable.getValue();
		switch (type) {
		case EQUALS:
			return Math.abs(value - target) < EPS;
		case NOTEQUALS:
			return Math.abs(value - target) > EPS;
		case LESS:
		case LESSEQ:
			return value+EPS < target;
		case GREATER:
		case GREATEREQ:
			return value > target+EPS;
		}
		throw new RuntimeException("Transition error: " + type + " is not a valid comparison type.");
	}

}
