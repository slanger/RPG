package com.me.rpg.utils;

public class MutableBoolean {
	
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
