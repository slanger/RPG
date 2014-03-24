package com.me.rpg.utils;

import java.io.Serializable;

public class MutableFloat implements Serializable
{

	private static final long serialVersionUID = 4666746116789970306L;

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
