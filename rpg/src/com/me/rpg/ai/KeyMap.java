package com.me.rpg.ai;

import java.io.Serializable;

import com.badlogic.gdx.Input.Keys;
import com.me.rpg.utils.Direction;

public class KeyMap implements Serializable
{

	private static final long serialVersionUID = -6564686280577008184L;

	public static final KeyMap DEFAULT = new KeyMap();
	
	private int[] map;
	private static Direction[] directions = {Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN};
	
	protected KeyMap() {
		map = new int[] {Keys.RIGHT, Keys.LEFT, Keys.UP, Keys.DOWN};
	}
	
	public int getKey(Direction direction) {
		return map[direction.getIndex()];
	}
	
	/**
	 * Returns the direction that this KeyMap has in its mapping
	 * @param key Use the Input.Keys instance here
	 * @return Direction if it exists, else null
	 */
	public Direction getDirectionFromKey(int key) {
		for (int i = 0; i < map.length; ++i) {
			if (map[i] == key)
				return directions[i];
		}
		return null;
	}
}
