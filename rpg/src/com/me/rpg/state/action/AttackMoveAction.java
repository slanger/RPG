package com.me.rpg.state.action;

import java.util.ArrayList;

import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Location;

public class AttackMoveAction implements Action {

	private static final long serialVersionUID = -6512021677482609335L;

	private GameCharacter character;
	private FollowPathAI walkAI;
	
	public AttackMoveAction(GameCharacter character) {
		this.character = character;
	}
	
	@Override
	public void doAction(float delta) {
		// Weapon weapon = character.getEquippedWeapon();
		// int range = weapon.getRange();
		Map map = character.getCurrentMap();
		ArrayList<GameCharacter> enemy = map.canSeeOrHearCharacters(character);
		// enemyCount.setValue(enemy.size());
		if (enemy.size() == 0)
			return;
		GameCharacter target = enemy.get(0);
		Location targetLocation = new Location(character.getCurrentMap(), target.getCenter());
		walkAI = new FollowPathAI(character, targetLocation);
		walkAI.update(delta);
	}

}
