package com.me.rpg.utils;

import java.io.Serializable;

import com.me.rpg.utils.Timer.Task;

public class GlobalTimerTask extends Task {

	private static final long serialVersionUID = 1L;
	private static GlobalTimerTask instance;
	
	private GlobalTimerTask() {}
	
	public static GlobalTimerTask getInstance() {
		if (instance != null)
			return instance;
		instance = new GlobalTimerTask();
		return instance;
	}
	
	
	@Override
	// Do nothing
	public void run() {}
	
	/**
	 * Saves the current time
	 * @return
	 */
	public static Time getCurrentTime() {
		return instance.new Time();
	}
	
	/**
	 * @param past A past time, retrieved from the GlobalTimerTask
	 * @return The difference in the past time with the current time, in seconds
	 */
	public static float getTimeDifference(Time past) {
		return (past.getMillis() - instance.timeLeftBeforeExecute) / 1000.0f;
	}
	
	/**
	 * Encapsulates a Time from the GlobalTimerTask
	 * @author Alex
	 *
	 */
	public class Time implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private long milliRemaining;
		
		public Time() {
			milliRemaining = timeLeftBeforeExecute;
		}
		
		public long getMillis() { 
			return milliRemaining;
		}
		
		public boolean equals(Object o) {
			Time t = null;
			try {
				t = (Time)o;
			} catch (Exception e) {}
			if (t == null) return false;
			return t.milliRemaining == milliRemaining;
		}
	}
}
