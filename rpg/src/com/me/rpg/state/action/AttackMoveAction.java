package com.me.rpg.state.action;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Coordinate;

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
		Coordinate center = target.getCenter();
		Rectangle cen = new Rectangle(center.getX(), center.getY(), Coordinate.EPS, Coordinate.EPS);
		walkAI = new FollowPathAI(character, cen, character.getCurrentMap());
		walkAI.update(delta);
	}

}
