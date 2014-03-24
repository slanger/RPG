package com.me.rpg.utils;

import java.io.Serializable;

public class MutableInt implements Serializable
{

	private static final long serialVersionUID = 5949395848784442914L;

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
