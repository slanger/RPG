package com.me.rpg.state.transition;

public class AndCondition implements Condition {
	
	private Condition[] conditions;
	
	public AndCondition(Condition ... conditions) {
		this.conditions = new Condition[conditions.length];
		System.arraycopy(conditions, 0, this.conditions, 0, conditions.length);
		
		for (int i = 0; i < conditions.length; ++i) {
			if (conditions[i] == null) {
				throw new NullPointerException("Can't make a composite with nulls.");
			}
		}
	}
	
	@Override
	public boolean test() {
		boolean result = true;
		for (int i = 0; i < conditions.length; ++i) {
			result &= conditions[i].test();
			if (!result)
				break;
		}
		return result;
	}

}
