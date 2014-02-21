package com.me.rpg.state;

import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.state.transition.BooleanTransition;
import com.me.rpg.utils.MutableBoolean;

public class StandStill extends AtomicState {
	
	private MutableBoolean seePeople;
	
	public StandStill(GameCharacter mainCharacter) {
		super("Stand Still.", mainCharacter);
		seePeople = new MutableBoolean();
	}

	@Override
	public void doUpdateBeforeTransition(float deltaTime) {
		GameCharacter main = getMainCharacter();
		Map map = main.getCurrentMap();
		if (!map.canSeeCharacters(main, 100).isEmpty()) {
			seePeople.setValue(true);
		} else {
			seePeople.setValue(false);
		}
	}
	
	@Override
	public BooleanTransition getBooleanTransition(String key, boolean target) {
		if (key.equals("seePeople")) {
			return new BooleanTransition(seePeople, target);
		}
		return super.getBooleanTransition(key, target);
	}
}
