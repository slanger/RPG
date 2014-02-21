package com.me.rpg.state;

import com.me.rpg.state.transition.FloatTransition;
import com.me.rpg.state.transition.Transition;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableFloat;

public class CompositeState extends State {
	
	private int currentStateIdx;
	private Transition[][] transitions;
	private State[] substates;
	
	private MutableFloat timeInSubstate;
	
	public CompositeState(Transition[][] transitions, State[] substates, String goal) {
		super(goal);
		this.transitions = transitions;
		this.substates = substates;
		setCurrentState(0);
		timeInSubstate = new MutableFloat();
	}
	
	@Override
	public void enterState() {
		setCurrentState(0);
		timeInSubstate.setValue(0f);
	}
	
	@Override
	public void doUpdateBeforeTransition(float deltaTime) {
		timeInSubstate.incrementValue(deltaTime);
	}
	
	@Override
	public void doUpdateAfterTransition(float deltaTime) {
		if(transition()) {
			timeInSubstate.setValue(0f);
		}
	}
	
	@Override
	public boolean doTransition() {
		for (int i = 0; i < transitions[currentStateIdx].length; ++i) {
			if (transitions[currentStateIdx][i].doTransition()) {
				int nextIdx = transitions[currentStateIdx][i].getNextStateIdx();
				super.setCurrentState(substates[nextIdx]);
				
				substates[currentStateIdx].leaveState();
				substates[nextIdx].enterState();
				
				setCurrentState(nextIdx);
				return true;
			}
		}
		return false;
	}
	
	private void setCurrentState(int idx) {
		this.currentStateIdx = idx;
		setCurrentState(substates[idx]);
	}
	
	@Override
	public FloatTransition getFloatTransition(String key, float target, Comparison type) {
		if (key.equals("timeInSubstate")) {
			return new FloatTransition(timeInSubstate, target, type);
		}
		return super.getFloatTransition(key, target, type);
	}
}
