package com.me.rpg.utils;

import java.util.ArrayList;

public class Timer
{

	static final int CANCELLED = -1;
	static final int FOREVER = -2;

	ArrayList<Task> tasks;

	public Timer()
	{
		tasks = new ArrayList<Task>();
	}

	public void update(float deltaTime)
	{
		int length = tasks.size();
		for (int i = 0; i < length; i++)
		{
			boolean isCancelled = tasks.get(i).update(deltaTime);
			if (isCancelled)
			{
				tasks.remove(i--);
				length--;
			}
		}
	}

	/**
	 * Cancels all Tasks
	 */
	public void clear()
	{
		for (Task task : tasks)
		{
			task.cancel();
		}
		tasks.clear();
	}

	public void scheduleTask(Task task, float delaySeconds,
			float intervalSeconds, int repeatCount)
	{
		if (task.repeatCount != CANCELLED)
		{
			throw new IllegalArgumentException(
					"The same Task may not be scheduled twice.");
		}
		task.timeLeftBeforeExecute = (long) (delaySeconds * 1000);
		task.intervalMillis = (long) (intervalSeconds * 1000);
		task.repeatCount = repeatCount;
		tasks.add(task);
	}

	public void scheduleTask(Task task, float delaySeconds)
	{
		scheduleTask(task, delaySeconds, 0, 1);
	}

	public void scheduleTask(Task task, float delaySeconds,
			float intervalSeconds)
	{
		scheduleTask(task, delaySeconds, intervalSeconds, FOREVER);
	}

}
