package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;

public class CustomState extends State {

	private ArrayList<Action> actions;
	private ArrayList<Action> entryActions;
	private ArrayList<Action> exitActions;
	
	public CustomState(HierarchicalState parent, GameCharacter character) {
		super(parent, character);
	}
	
	public void setActions(Action ... actions) {
		if (this.actions == null) {
			this.actions = new ArrayList<Action>();
			for(Action a: actions)
				this.actions.add(a);
		} else {
			throw new RuntimeException("You cannot set the actions more than once.");
		}
	}
	
	public void setEntryActions(Action ... entryActions) {
		if (this.entryActions == null) {
			this.entryActions = new ArrayList<Action>();
			for(Action a: entryActions)
				this.entryActions.add(a);
		} else {
			throw new RuntimeException("You cannot set the entryActions more than once.");
		}
	}
	
	public void setExitActions(Action ... exitActions) {
		if (this.exitActions == null) {
			this.exitActions = new ArrayList<Action>();
			for(Action a: exitActions)
				this.exitActions.add(a);
		} else {
			throw new RuntimeException("You cannot set the exitActions more than once.");
		}
	}
	
	public ArrayList<Action> doGetActions() {
		if (actions == null)
			return super.doGetActions();
		return actions;
	}
	
	public ArrayList<Action> doGetEntryActions() {
		if (entryActions == null)
			return super.doGetEntryActions();
		return entryActions;
	}
	
	public ArrayList<Action> doGetExitActions() {
		if (exitActions == null)
			return super.doGetExitActions();
		return exitActions;
	}
}
