package com.me.rpg.state;

import java.util.ArrayList;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.state.action.Action;
import com.me.rpg.state.action.WalkAction;
import com.me.rpg.utils.Coordinate;

public class PatrolState extends State {
	
	private int idx = 0;
	private ArrayList<Action> actions;
	private WalkAction patrolLoc;
	private Coordinate[] patrol;
	
	public PatrolState(HierarchicalState parent, GameCharacter character, Coordinate[] patrol) {
		super(parent, character);
		if (patrol == null || patrol.length == 0)
			throw new NullPointerException("Can't pass in a null or empty patrol list.");
		for (int i = 0; i < patrol.length; ++i) {
			if (patrol[i] == null)
				throw new NullPointerException("Can't have a null location for patrol list: " + i + " " + patrol);
		}
		this.patrol = new Coordinate[patrol.length];
		System.arraycopy(patrol, 0, this.patrol, 0, patrol.length);
		patrolLoc = new WalkAction(character, patrol[0]);
		actions = new ArrayList<Action>();
		actions.add(patrolLoc);
	}
	
	public ArrayList<Action> doGetEntryActions() {
		// find closest patrol location.
		float min = Float.MAX_VALUE;
		Coordinate center = character.getCenter();
		int oldIdx = idx;
		for (int i = 0; i < patrol.length; ++i) {
			float dist2 = center.distance2(patrol[i]);
			if (dist2 < min) {
				min = dist2;
				idx = i;
				patrolLoc.setNewLoc(patrol[idx]);
			}
		}
		return super.doGetEntryActions();
	}
	
	public ArrayList<Action> doGetActions() {
		if (character.isCenterNear(patrol[idx])) {
			++idx;
			idx %= patrol.length;
			patrolLoc.setNewLoc(patrol[idx]);
		}
		return actions;
	}
}
