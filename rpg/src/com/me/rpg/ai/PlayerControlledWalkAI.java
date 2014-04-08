package com.me.rpg.ai;

import static com.me.rpg.utils.Direction.DOWN;
import static com.me.rpg.utils.Direction.LEFT;
import static com.me.rpg.utils.Direction.RIGHT;
import static com.me.rpg.utils.Direction.UP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.me.rpg.characters.GameCharacter;
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
		float spriteWidth = character.getSpriteWidth();
		float spriteHeight = character.getSpriteHeight();
		float oldX = character.getBottomLeftX();
		float oldY = character.getBottomLeftY();
		float x = oldX;
		float y = oldY;
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
			x += (dx * speed * deltaTime) / Math.sqrt(2);
			y += (dy * speed * deltaTime) / Math.sqrt(2);
			ret = Direction.getDirectionByDiff(0, dy);
			moving = true;
		}
		else if (diff == 1)
		{
			// moving in 1 direction
			x += dx * speed * deltaTime;
			y += dy * speed * deltaTime;
			ret = Direction.getDirectionByDiff(dx, dy);
			moving = true;
		}

		// update x and y
		Coordinate newCoordinate = new Coordinate(oldX, oldY);
		if (moving)
		{
			// collision detection with objects on map
			boolean didMove = character.getCurrentMap().checkCollision(x, y,
					oldX, oldY, character, newCoordinate);
			moving = didMove;
			x = newCoordinate.getX();
			y = newCoordinate.getY();

			// check warp point collision
			if (!character.warpEnable())
			{
				Waypoint warpWaypoint = character.getCurrentMap()
						.checkCollisionWithWarpPoints(
								new Rectangle(x, y, spriteWidth, spriteHeight));
				if (warpWaypoint == null)
				{
					// character moved off of warp point, enable warping again
					character.setWarpEnable(true);
				}
			}
			else
			{
				if (didMove)
				{
					Waypoint warpWaypoint = character.getCurrentMap()
							.checkCollisionWithWarpPoints(
									new Rectangle(x, y, spriteWidth,
											spriteHeight));
					if (warpWaypoint != null)
					{
						// character.warpToOtherMap(warpWaypoint.location);
						character.moveToOtherMap(warpWaypoint.location);
					}
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
