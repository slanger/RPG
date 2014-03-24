package com.me.rpg.state.transition;

import java.io.Serializable;

public interface Condition extends Serializable
{

	public boolean test();

}
