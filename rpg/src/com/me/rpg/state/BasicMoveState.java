package com.me.rpg.state;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.ai.FollowPathAI;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.state.transition.BooleanTransition;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.MutableBoolean;

public class BasicMoveState extends AtomicState {
	
	// We will aim such that mainCharacter's center is near to target
	private Coordinate target;
	private MutableBoolean atLocation;
	private MutableBoolean seePeople;
	private FollowPathAI walkAI;
	
	public BasicMoveState(Coordinate target, GameCharacter mainCharacter) {
		super("Move toward target location.", mainCharacter);
		this.target = target;
		atLocation = new MutableBoolean();
		seePeople = new MutableBoolean();
		walkAI = new FollowPathAI(mainCharacter, new Rectangle[]{new Rectangle(target.getX(), target.getY(), 1, 1)});
	}
	
	@Override
	public void doUpdateBeforeTransition(float deltaTime) {
		walkAI.update(deltaTime, getMainCharacter().getCurrentMap());
		GameCharacter main = getMainCharacter();
		Coordinate center = main.getCenter();
		float diffx = center.getX() - target.getX();
		float diffy = center.getY() - target.getY();
		
		if ( diffx*diffx + diffy*diffy > 2.000001) {
			atLocation.setValue(false);
		} else {
			atLocation.setValue(true);
		}
		
		Map map = main.getCurrentMap();
		if (!map.canSeeCharacters(main, 200).isEmpty()) {
			seePeople.setValue(true);
		} else {
			seePeople.setValue(false);
		}
	}
	
	@Override
	public BooleanTransition getBooleanTransition(String key, boolean target) {
		if (key.equals("atLocation")) {
			return new BooleanTransition(atLocation, target);
		} else if (key.equals("seePeople")) {
			return new BooleanTransition(seePeople, target);
		}
		return super.getBooleanTransition(key, target);
	}
}
