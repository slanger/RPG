package com.me.rpg.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class Timer implements Serializable
{

	private static final long serialVersionUID = -7457628628332157767L;

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
					"The same Task may not be scheduled twice." + task.repeatCount);
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

	public static abstract class Task implements Runnable, Serializable
	{

		private static final long serialVersionUID = -4675247770364747791L;

		long timeLeftBeforeExecute; // in milliseconds
		long intervalMillis; // in milliseconds
		int repeatCount = Timer.CANCELLED;

		/**
		 * This method is to be implemented by subclasses of Task. If this is the
		 * last time the Task will be ran or the Task is first cancelled, it may be
		 * scheduled again in this method.
		 */
		@Override
		public abstract void run();

		/**
		 * Cancels the Task. It will not be executed until it is scheduled again.
		 * This method can be called at any time.
		 */
		public void cancel()
		{
			timeLeftBeforeExecute = 0;
			repeatCount = Timer.CANCELLED;
		}

		/**
		 * Returns true if this Task is scheduled to be executed in the future by a
		 * Timer.
		 */
		public boolean isScheduled()
		{
			return (repeatCount != Timer.CANCELLED);
		}

		/**
		 * Return true if the Task has been cancelled.
		 */
		boolean update(float deltaTimeInSeconds)
		{
			if (repeatCount == Timer.CANCELLED)
			{
				return true;
			}
			boolean isCancelled = false;
			timeLeftBeforeExecute -= (long) (deltaTimeInSeconds * 1000);
			if (timeLeftBeforeExecute <= 0)
			{
				timeLeftBeforeExecute = intervalMillis;
				if (repeatCount > 0)
				{
					repeatCount--;
				}
				if (repeatCount == 0)
				{
					cancel();
					isCancelled = true;
				}
				run();
			}
			return isCancelled;
		}

	}

}
