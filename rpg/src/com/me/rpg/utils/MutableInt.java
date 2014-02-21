package com.me.rpg.utils;

public class MutableInt {
	
	private int value;
	
	public MutableInt() {
		this(0);
	}
	
	public MutableInt(int initialValue) {
		value = initialValue;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int newValue) {
		value = newValue;
	}
	
	public void incrementValue(int increment) {
		value += increment;
	}
}
