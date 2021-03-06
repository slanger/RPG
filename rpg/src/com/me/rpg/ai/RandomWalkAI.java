package com.me.rpg.ai;

import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.utils.Timer;

public class RandomWalkAI implements WalkAI
{

	private static final long serialVersionUID = 1422490159208848504L;

	private GameCharacter character;
	private float delaySeconds;
	private float intervalSeconds;
	private Rectangle walkingBounds;
	private MoveTask moveTask;
	private boolean toggleWalking = false;

	private class MoveTask extends Timer.Task
	{

		private static final long serialVersionUID = 8358425947248410536L;

		@Override
		public void run()
		{
			if (toggleWalking)
			{
				character.setMoving(false);
				toggleWalking = !toggleWalking;
				return;
			}
			// generate a random int in the range [0, 4] and convert to a
			// direction
			// 4 means the NPC won't move this update
			int randomInt = (int) (Math.random() * 5);
			if (randomInt > 3)
			{
				character.setMoving(false);
			}
			else
			{
				character.setMoving(true);
				character.setMoveDirection(Direction
						.getDirectionByIndex(randomInt));
			}
			toggleWalking = !toggleWalking;
		}

	}

	public RandomWalkAI(GameCharacter character, float delaySeconds,
			float intervalSeconds, Rectangle walkingBounds)
	{
		this.character = character;
		this.delaySeconds = delaySeconds;
		this.intervalSeconds = intervalSeconds;
		this.walkingBounds = walkingBounds;
		this.moveTask = new MoveTask();
	}

	@Override
	public void start()
	{
		character.getCurrentMap().getTimer()
				.scheduleTask(moveTask, delaySeconds, intervalSeconds);
	}

	@Override
	public void stop()
	{
		character.setMoving(false);
		moveTask.cancel();
	}

	@Override
	public void update(float deltaTime)
	{
		if (!character.isMoving())
		{
			return;
		}

		float spriteWidth = character.getSpriteWidth();
		float spriteHeight = character.getSpriteHeight();
		float oldX = character.getBottomLeftX();
		float oldY = character.getBottomLeftY();
		float x = oldX;
		float y = oldY;
		float speed = character.getSpeed();

		Direction direction = character.getMoveDirection();
		x += speed * deltaTime * direction.getDx();
		y += speed * deltaTime * direction.getDy();

		// clamp x
		float minX = walkingBounds.getX();
		float maxX = minX + walkingBounds.getWidth() - spriteWidth;
		if (x < minX)
		{
			x = minX;
		}
		if (x > maxX)
		{
			x = maxX;
		}

		// clamp y
		float minY = walkingBounds.getY();
		float maxY = minY + walkingBounds.getHeight() - spriteHeight;
		if (y < minY)
		{
			y = minY;
		}
		if (y > maxY)
		{
			y = maxY;
		}

		// collision detection with objects on map
		Coordinate newCoordinate = new Coordinate();
		boolean didMove = character.getCurrentMap().checkCollision(x, y, oldX,
				oldY, character, newCoordinate);

		character.setMoving(didMove);
		character.setMoveDirection(direction);
		character.setBottomLeftCorner(newCoordinate);
	}

}
