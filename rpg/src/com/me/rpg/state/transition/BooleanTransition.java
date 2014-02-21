package com.me.rpg.state.transition;

import com.me.rpg.utils.MutableBoolean;

public class BooleanTransition extends Transition {
	
	private MutableBoolean mutable;
	private boolean target;
	
	public BooleanTransition(MutableBoolean mutable, boolean target) {
		this.mutable = mutable;
		this.target = target;
	}
	
	@Override
	public boolean doTransition() {
		return mutable.getValue() == target;
	}

}
