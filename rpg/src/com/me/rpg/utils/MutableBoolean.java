package com.me.rpg.utils;

import java.io.Serializable;

public class MutableBoolean implements Serializable
{

	private static final long serialVersionUID = 5010924541185139237L;

	private boolean value;
	
	public MutableBoolean() {
		this(false);
	}
	
	public MutableBoolean(boolean initialValue) {
		value = initialValue;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean newValue) {
		value = newValue;
	}
}
