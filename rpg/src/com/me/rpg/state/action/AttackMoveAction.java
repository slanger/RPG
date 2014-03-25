package com.me.rpg.state.action;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.combat.Weapon;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Coordinate;

public class AttackMoveAction implements Action {
	
	private GameCharacter character;
	private FollowPathAI walkAI;
	
	public AttackMoveAction(GameCharacter character) {
		this.character = character;
		walkAI = new FollowPathAI(character, new Rectangle[0]);
	}
	
	@Override
	public void doAction(float delta) {
		GameCharacter main = character;
		Weapon weapon = main.getEquippedWeapon();
		//int range = weapon.getRange();
		Map map = main.getCurrentMap();
		ArrayList<GameCharacter> enemy = map.canSeeOrHearCharacters(main);
		//enemyCount.setValue(enemy.size());
		if (enemy.size() == 0)
			return;
		GameCharacter target = enemy.get(0);
		Coordinate center = target.getCenter();
		Rectangle cen = new Rectangle(center.getX(), center.getY(), Coordinate.EPS, Coordinate.EPS);
		walkAI.setNewPath(cen);
		walkAI.update(delta, main.getCurrentMap());
	}

}
