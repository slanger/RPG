package com.me.rpg.state.transition;

public abstract class Transition {
	
	private int nextStateIdx;
	
	public abstract boolean doTransition();
	
	public int getNextStateIdx() {
		return nextStateIdx;
	}
	
	public void setNextState(int nextStateIdx) {
		this.nextStateIdx = nextStateIdx;
	}
}
