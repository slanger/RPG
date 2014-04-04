package com.me.rpg.state.transition;

import com.me.rpg.utils.MutableBoolean;

public class BooleanCondition implements Condition {

	private static final long serialVersionUID = -8103535443832275754L;

	private MutableBoolean mutable;
	private boolean target;
	
	public BooleanCondition(MutableBoolean mutable, boolean target) {
		this.mutable = mutable;
		this.target = target;
	}
	
	@Override
	public boolean test() {
		return mutable.getValue() == target;
	}

}
