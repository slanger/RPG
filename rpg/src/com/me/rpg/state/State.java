package com.me.rpg.state;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.transition.BooleanTransition;
import com.me.rpg.state.transition.FloatTransition;
import com.me.rpg.state.transition.IntTransition;
import com.me.rpg.utils.Comparison;
import com.me.rpg.utils.MutableFloat;


/**
 * Main driver of this class is through the update(float) method.
 * @author Alex
 *
 */
public abstract class State {
	
	private String goal;
	private State currentState;
	private GameCharacter mainCharacter;
	
	private MutableFloat timeInState;
	private boolean calledTransition;
	private boolean transitionResult;
	
	public State() {
		this("");
	}
	
	public State(String goal) {
		this(goal, null);
	}
	
	public State(String goal, GameCharacter mainCharacter) {
		this.goal = goal;
		this.mainCharacter = mainCharacter;
		timeInState = new MutableFloat();
	}
	
	/**
	 * Sets the mainCharacter if it is not already set.
	 * @param mainCharacter The main character for this state - the one who is in this state
	 */
	public final void setMainCharacter(GameCharacter mainCharacter) {
		if (this.mainCharacter != null)
			throw new RuntimeException("MainCharacter already exists for this state.");
		if (mainCharacter == null)
			throw new NullPointerException("You are not allowed to set the mainCharacter to be null.");
		this.mainCharacter = mainCharacter;
	}
	
	protected final GameCharacter getMainCharacter() {
		return mainCharacter;
	}
	
	/**
	 * Called when we enter the state.  Will only be called again after leaveState is called
	 */
	public void enterState() {}
	
	/**
	 * Called when we leave a state.
	 */
	public void leaveState() {}
	
	
	/**
	 * This is what will be called every frame update
	 * It will call the child's update(float) method first.
	 * Then call the doUpdateBeforeTransition(float) method
	 * Then the transition() method
	 * 		within the transition() method, the doTransition() method is called
	 * 		The impl. of transition() guarantees doTransition will be called once per update()
	 * Lastly, the doUpdateAfterTransition(float) method
	 * 
	 * update() and transition() are final.
	 * Hooks provided are doUpdateBeforeTransition(), doTransition(), doUpdateAfterTransition()
	 * @param deltaTime Time in seconds which have passed since last update
	 */
	public final void update(float deltaTime) {
		calledTransition = false;
		if (currentState != this)
			currentState.update(deltaTime);
		
		timeInState.incrementValue(deltaTime);
		doUpdateBeforeTransition(deltaTime);
		transition();
		if (transitionResult) {
			timeInState.setValue(0f);
		}
		doUpdateAfterTransition(deltaTime);
	}
	
	public final boolean transition() {
		if (calledTransition)
			return transitionResult;
		
		calledTransition = true;
		transitionResult = doTransition();
		return transitionResult;
	}
	
	public abstract void doUpdateBeforeTransition(float deltaTime);
	
	public void doUpdateAfterTransition(float deltaTime) {}
	
	public boolean doTransition() {
		// default implementation does nothing.
		return false;
	}
	public BooleanTransition getBooleanTransition(String key, boolean target) {
		throw new UnsupportedOperationException("getBooleanTransition function not supported.");
	}
	public IntTransition getIntTransition(String key, int target, Comparison type) {
		throw new UnsupportedOperationException("getIntTransition function not supported.");
	}
	public FloatTransition getFloatTransition(String key, float target, Comparison type) {
		if (key.equals("timeInState")) {
			return new FloatTransition(timeInState, target, type);
		}
		throw new RuntimeException("Attempt to get FloatTransition from improper key: " + key);
	}
	
	public final void setCurrentState(State nextState) {
		currentState = nextState;
	}
	
	public String getGoal() {
		return goal;
	}
}
