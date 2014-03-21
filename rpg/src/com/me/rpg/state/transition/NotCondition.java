package com.me.rpg.state.transition;

public class NotCondition implements Condition {
	
	private Condition condition;
	
	public NotCondition(Condition condition) {
		this.condition = condition;
	}
	
	@Override
	public boolean test() {
		return !condition.test();
	}

}
