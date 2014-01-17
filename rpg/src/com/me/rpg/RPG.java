package com.me.rpg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RPG implements ApplicationListener
{
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Sprite sprite;
	private TextureRegion rightIdle, leftIdle, upIdle, downIdle;
	private Animation rightWalkAnimation, leftWalkAnimation, upWalkAnimation, downWalkAnimation;
	private Direction direction;
	private boolean moving = false;
	private float stateTime = 0f;
	
	// camera scrolling
	private boolean cameraScrollEnable = false;
	private Texture background;
	private float cameraX, cameraY;
	private float selectDelta = 0.0f;
	
	private enum Direction
	{
		RIGHT	(0),
		LEFT	(1),
		UP		(2),
		DOWN	(3);
		
		int index;
		
		Direction(int index)
		{
			this.index = index;
		}
		
		public int getIndex()
		{
			return index;
		}
	}
	
	@Override
	public void create()
	{
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		batch = new SpriteBatch();
		
		// background setup
		background = new Texture(Gdx.files.internal("ALTTP_bigmap.png"));
		cameraX = background.getWidth()/2;
		cameraY = background.getHeight()/2;
		
		// sprite + animation setup begin 
		
		TextureRegion[][] spritesheet = TextureRegion.split(new Texture(Gdx.files.internal("hero.png")), 16, 16);
		TextureRegion[] rightWalkFrames = new TextureRegion[2];
		TextureRegion[] leftWalkFrames = new TextureRegion[2];
		TextureRegion[] upWalkFrames = new TextureRegion[2];
		TextureRegion[] downWalkFrames = new TextureRegion[2];
		for (int i = 0; i < 2; i++)
		{
			rightWalkFrames[i] = spritesheet[Direction.RIGHT.getIndex()][i];
			leftWalkFrames[i] = spritesheet[Direction.LEFT.getIndex()][i];
			upWalkFrames[i] = spritesheet[Direction.UP.getIndex()][i];
			downWalkFrames[i] = spritesheet[Direction.DOWN.getIndex()][i];
		}
		float duration = 0.15f;
		rightWalkAnimation = new Animation(duration, rightWalkFrames);
		leftWalkAnimation = new Animation(duration, leftWalkFrames);
		upWalkAnimation = new Animation(duration, upWalkFrames);
		downWalkAnimation = new Animation(duration, downWalkFrames);
		rightIdle = spritesheet[Direction.RIGHT.getIndex()][0];
		leftIdle = spritesheet[Direction.LEFT.getIndex()][0];
		upIdle = spritesheet[Direction.UP.getIndex()][0];
		downIdle = spritesheet[Direction.DOWN.getIndex()][0];
		// start sprite facing downward
		sprite = new Sprite(spritesheet[Direction.DOWN.getIndex()][0]);
		direction = Direction.DOWN;
		sprite.setPosition(camera.viewportWidth / 2, camera.viewportHeight / 2);
		
		// sprite + animation setup end
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		sprite.getTexture().dispose();
		// TODO not sure what else we need to dispose of.  maybe all textures, texture regions?
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		// draw stuff here
		batch.draw(background, 0, 0, (int)(cameraX - camera.viewportWidth/2), (int)(cameraY - camera.viewportHeight/2), (int)camera.viewportWidth, (int)camera.viewportHeight);

		sprite.draw(batch);
		batch.end();
		
		update();
	}

	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		float oldX = sprite.getX();
		float oldY = sprite.getY();
		float x = oldX;
		float y = oldY;
		moving = false;
		
		selectDelta += deltaTime;
		
		// enable/disable camera scrolling
		if (Gdx.input.isKeyPressed(Keys.C) && selectDelta > 0.5f) {
			cameraScrollEnable = !cameraScrollEnable;
			selectDelta = 0.0f;
		}
		
		stateTime += deltaTime;
		
		// update x
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			x -= 100 * deltaTime;
			direction = Direction.LEFT;
			moving = true;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			x += 100 * deltaTime;
			direction = Direction.RIGHT;
			moving = true;
		}
		if (x < 0)
		{
			x = 0;
		}
		if (x > camera.viewportWidth - sprite.getWidth())
		{
			x = camera.viewportWidth - sprite.getWidth();
		}
		
		// update y
		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			y += 100 * deltaTime;
			direction = Direction.UP;
			moving = true;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			y -= 100 * deltaTime;
			direction = Direction.DOWN;
			moving = true;
		}
		if (y < 0)
		{
			y = 0;
		}
		if (y > camera.viewportHeight - sprite.getHeight())
		{
			y = camera.viewportHeight - sprite.getHeight();
		}
		
		if (cameraScrollEnable) {
			cameraX += (x - oldX);
			cameraY -= (y - oldY);
			if (cameraX < 0)
				cameraX = 0;
			else if (cameraX > background.getWidth() - camera.viewportWidth) {
				cameraX = background.getWidth() - camera.viewportWidth;
			}
			if (cameraY < 0) {
				cameraY = 0;
			} else if (cameraY > background.getHeight() - camera.viewportHeight) {
				cameraY = background.getHeight() - camera.viewportHeight;
			}
			
			x = oldX;
			y = oldY;
		}
		
		if (moving)
		{
			sprite.setPosition(x, y);
			TextureRegion currentFrame = null;
			switch (direction)
			{
			case RIGHT:
				currentFrame = rightWalkAnimation.getKeyFrame(stateTime, true);
				break;
			case LEFT:
				currentFrame = leftWalkAnimation.getKeyFrame(stateTime, true);
				break;
			case UP:
				currentFrame = upWalkAnimation.getKeyFrame(stateTime, true);
				break;
			case DOWN:
				currentFrame = downWalkAnimation.getKeyFrame(stateTime, true);
				break;
			}
			if (currentFrame != null)
			{
				sprite.setRegion(currentFrame);
			}
		}
		else
		{
			TextureRegion currentFrame = null;
			switch (direction)
			{
			case RIGHT:
				currentFrame = rightIdle;
				break;
			case LEFT:
				currentFrame = leftIdle;
				break;
			case UP:
				currentFrame = upIdle;
				break;
			case DOWN:
				currentFrame = downIdle;
				break;
			}
			if (currentFrame != null)
			{
				sprite.setRegion(currentFrame);
			}
		}
	}

	@Override
	public void resize(int width, int height)
	{
		
	}

	@Override
	public void pause()
	{
		
	}

	@Override
	public void resume()
	{
		
	}
	
}
