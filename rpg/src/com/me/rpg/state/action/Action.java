package com.me.rpg.state.action;

import java.io.Serializable;

public interface Action extends Serializable
{

	public void doAction(float delta);

}
