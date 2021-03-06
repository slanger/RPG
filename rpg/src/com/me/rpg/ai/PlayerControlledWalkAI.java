package com.me.rpg.ai;

import static com.me.rpg.utils.Direction.DOWN;
import static com.me.rpg.utils.Direction.LEFT;
import static com.me.rpg.utils.Direction.RIGHT;
import static com.me.rpg.utils.Direction.UP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.characters.GameCharacter;
import com.me.rpg.maps.Map;
import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.utils.Waypoint;

public class PlayerControlledWalkAI
	implements WalkAI
{

	private static final long serialVersionUID = 2647691917043351571L;

	private final GameCharacter character;
	private KeyMap keyMap = KeyMap.DEFAULT;
	private boolean strafeEnabler = false;

	public PlayerControlledWalkAI(GameCharacter character)
	{
		this.character = character;
	}

	@Override
	public void update(float deltaTime)
	{
		Rectangle oldBoundingRectangle = character.getBoundingRectangle();
		float spriteWidth = oldBoundingRectangle.width;
		float spriteHeight = oldBoundingRectangle.height;
		float oldX = oldBoundingRectangle.x;
		float oldY = oldBoundingRectangle.y;
		float newX = oldX;
		float newY = oldY;
		float speed = character.getSpeed();
		int dx = 0;
		int dy = 0;
		if (Gdx.input.isKeyPressed(keyMap.getKey(LEFT)))
		{
			dx += LEFT.getDx();
			dy += LEFT.getDy();
		}
		if (Gdx.input.isKeyPressed(keyMap.getKey(RIGHT)))
		{
			dx += RIGHT.getDx();
			dy += RIGHT.getDy();
		}
		if (Gdx.input.isKeyPressed(keyMap.getKey(UP)))
		{
			dx += UP.getDx();
			dy += UP.getDy();
		}
		if (Gdx.input.isKeyPressed(keyMap.getKey(DOWN)))
		{
			dx += DOWN.getDx();
			dy += DOWN.getDy();
		}
		if (Gdx.input.isKeyPressed(Keys.U))
		{
			if (strafeEnabler)
			{
				strafeEnabler = false;
				character.usingShield(!character.isUsingShield());
			}
		}
		else
		{
			strafeEnabler = true;
		}

		// decode Direction from input
		Direction ret = null;
		boolean moving = false;
		int diff = Math.abs(dx) + Math.abs(dy);
		if (diff == 2)
		{
			// moving diagonally, slow down movement in x and y
			newX += (dx * speed * deltaTime) / Math.sqrt(2);
			newY += (dy * speed * deltaTime) / Math.sqrt(2);
			ret = Direction.getDirectionByDiff(0, dy);
			moving = true;
		}
		else if (diff == 1)
		{
			// moving in 1 direction
			newX += dx * speed * deltaTime;
			newY += dy * speed * deltaTime;
			ret = Direction.getDirectionByDiff(dx, dy);
			moving = true;
		}

		// update x and y
		Coordinate newCoordinate = new Coordinate(oldX, oldY);
		if (moving)
		{
			// collision detection with objects on map
			Map currentMap = character.getCurrentMap();
			boolean didMove = currentMap.checkCollision(newX, newY, oldX, oldY,
					character, newCoordinate);

			if (!didMove)
			{
				// check if we are stuck inside another character/object
				// check characters
				GameCharacter collidedChar = currentMap
						.checkCollisionWithCharacters(oldBoundingRectangle,
								character);
				boolean areStuck = (collidedChar != null);
				if (!areStuck)
				{
					// check objects
					areStuck = !currentMap
							.checkCollisionWithObjects(oldBoundingRectangle);
				}

				if (areStuck)
				{
					// we are stuck, so ignore collision detection
					didMove = true;
					newCoordinate.setX(newX);
					newCoordinate.setY(newY);
				}
			}

			moving = didMove;

			// check warp point collision
			if (didMove)
			{
				Rectangle newBoundingRectangle = new Rectangle(
						newCoordinate.getX(), newCoordinate.getY(),
						spriteWidth, spriteHeight);
				Waypoint warpWaypoint = currentMap
						.checkCollisionWithWarpPoints(newBoundingRectangle);
				if (warpWaypoint != null)
				{
					character.moveToOtherMap(warpWaypoint.location);
				}
			}
		}

		character.setMoving(moving);
		if (ret != null)
		{
			character.setMoveDirection(ret);
		}
		character.setBottomLeftCorner(newCoordinate);
	}

	@Override
	public void start()
	{

	}

	@Override
	public void stop()
	{

	}

}
