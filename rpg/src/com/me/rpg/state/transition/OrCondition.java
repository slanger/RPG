package com.me.rpg.state.transition;


public class OrCondition implements Condition {

	private static final long serialVersionUID = -2005208250832936863L;

	private Condition[] parts;
	
	public OrCondition(Condition ... parts) {
		this.parts = new Condition[parts.length];
		System.arraycopy(parts, 0, this.parts, 0, parts.length);
		
		for (int i = 0; i < parts.length; ++i) {
			if (parts[i] == null) {
				throw new NullPointerException("Can't make a composite with nulls.");
			}
		}
	}
	
	@Override
	public boolean test() {
		boolean result = false;
		for (int i = 0; i < parts.length; ++i) {
			result |= parts[i].test();
			if (result)
				break;
		}
		return result;
	}
}
