package com.me.rpg.utils;

public abstract class Task implements Runnable
{

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
