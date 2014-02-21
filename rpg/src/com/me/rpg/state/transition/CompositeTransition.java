package com.me.rpg.state.transition;


public class CompositeTransition extends Transition {

	private Transition[] parts;
	private boolean isOR;
	
	public CompositeTransition(boolean isOR, Transition ... parts) {
		this.isOR = isOR;
		this.parts = new Transition[parts.length];
		System.arraycopy(parts, 0, this.parts, 0, parts.length);
		
		for (int i = 0; i < parts.length; ++i) {
			if (parts[i] == null) {
				throw new NullPointerException("Can't make a composite out of nulls.");
			}
		}
	}
	
	@Override
	public boolean doTransition() {
		boolean result = false;
		if (isOR) {
			for (int i = 0; i < parts.length; ++i) {
				result |= parts[i].doTransition();
				if (result)
					break;
			}
		} else {
			result = true;
			for (int i = 0; i < parts.length; ++i) {
				result &= parts[i].doTransition();
				if (!result)
					break;
			}
		}
		return result;
	}
}
