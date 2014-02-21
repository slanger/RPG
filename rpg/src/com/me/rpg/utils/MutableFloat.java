package com.me.rpg.utils;

public class MutableFloat {
	
	private float value;
	
	public MutableFloat() {
		this(0f);
	}
	
	public MutableFloat(float initialValue) {
		value = initialValue;
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float newValue) {
		value = newValue;
	}
	
	public void incrementValue(float increment) {
		value += increment;
	}
}
