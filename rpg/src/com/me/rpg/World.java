package com.me.rpg;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Disposable;
import com.me.rpg.ai.Dialogue;
import com.me.rpg.maps.ExampleMap;
import com.me.rpg.maps.Map;
import com.me.rpg.reputation.ReputationEvent;
import com.me.rpg.reputation.ReputationSystem;

import com.me.rpg.utils.Coordinate;
import com.me.rpg.utils.Direction;
import com.me.rpg.ai.Dialogue;
import com.me.rpg.characters.GameCharacter;

import com.me.rpg.utils.Task;
import com.me.rpg.utils.Timer;


public class World implements Disposable
{

	public static String WARP_SOUND_PATH = "music/ALTTP_warp_sound.mp3";
	public static String WHITE_DOT_PATH = "white_dot.png";
	public static String FADED_RED_DOT_PATH = "faded_red_dot.png";

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private BitmapFont debugFont;
	private Map map;

	private Dialogue dialogue;
	private ReputationSystem reputationSystem;

	private boolean warping = false;
	private float warpingAlpha;
	private Sound warpSound;
	private Sprite whiteScreen;

	private Timer timer = new Timer();

	public Map getMap()
	{
		return map;
	}

	public void setMap(Map map)
	{
		if (this.map != null)
		{
			this.map.dispose();
		}
		this.map = map;
	}

	public void warpToAnotherMap(Map map)
	{
		this.map.close();
		warping = true;
		warpingAlpha = 0f;
		warpSound.play();
		timer.scheduleTask(new Task()
		{

			@Override
			public void run()
			{
				warpingAlpha += 0.1f;
				if (warpingAlpha > 1f)
				{
					warpingAlpha = 1f;
					this.cancel();
				}
			}

		}, 0f, 0.1f);
		timer.scheduleTask(new WarpToAnotherMapTask(map), 3.0f);
	}

	private class WarpToAnotherMapTask extends Task
	{

		private Map newMap;

		WarpToAnotherMapTask(Map newMap)
		{
			this.newMap = newMap;
		}

		@Override
		public void run()
		{
			setMap(newMap);

			timer.scheduleTask(new Task()
			{

				@Override
				public void run()
				{
					warpingAlpha -= 0.1f;
				}

			}, 0f, 0.1f, 10);

			timer.scheduleTask(new Task()
			{

				@Override
				public void run()
				{
					warping = false;
					newMap.open();
				}

			}, 1.0f);
		}

	}

	public Dialogue getDialogue()
	{
		return dialogue;
	}

	public ReputationSystem getReputationSystem()
	{
		return reputationSystem;
	}

	public World(SpriteBatch batch, OrthographicCamera camera)
	{
		this.batch = batch;
		this.camera = camera;

		// create map
		dialogue = new Dialogue(batch, camera);
		reputationSystem = new ReputationSystem(this);

		map = new ExampleMap(this, batch, camera);

		// create debug font
		debugFont = new BitmapFont();
		debugFont.setColor(0.95f, 0f, 0.23f, 1f); // "Munsell" red

		// warp resources
		warpSound = RPG.manager.get(WARP_SOUND_PATH, Sound.class);
		whiteScreen = new Sprite(RPG.manager.get(WHITE_DOT_PATH, Texture.class));
	}

	public void render()
	{
		map.render();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		temporaryVisionConeTest();
		reputationSystem.CheckForWitnesses(null,null);
		// temporary dialogue stuff
		if (dialogue.getInDialogue())
		{
			dialogue.render();
		}

		if (warping)
		{
			whiteScreen.setSize(camera.viewportWidth, camera.viewportHeight);
			whiteScreen.setPosition(camera.position.x - camera.viewportWidth
					/ 2, camera.position.y - camera.viewportHeight / 2);
			whiteScreen.draw(batch, warpingAlpha);
		}

		// render HUD and overlays
		float fpsX = camera.position.x - camera.viewportWidth / 2 + 15;
		float fpsY = camera.position.y + camera.viewportHeight / 2 - 15;
		debugFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(),
				fpsX, fpsY);

		batch.end();
	}
	
	public boolean isGameOver()
	{
		return map.isGameOver();
	}

	public void update(float deltaTime)
	{
		timer.update(deltaTime);
		map.update(deltaTime);
	}

	@Override
	public void dispose()
	{
		map.dispose();
		debugFont.dispose();
	}
	
	
	public void temporaryVisionConeTest()  
	{
				float tempX=0.0f;
				float tempY=0.0f;
				float tempSightDistance = 0.0f;
				Direction tempDirection = null;
				float visionFieldPoints[] = new float[8];
		
		
				ShapeRenderer shapeRenderer = new ShapeRenderer();
				
				
			    shapeRenderer.setProjectionMatrix(camera.combined);
			    shapeRenderer.begin(ShapeType.Line);
				
				ArrayList<GameCharacter> charactersOnMap = map.getCharactersOnMap();
				Iterator<GameCharacter> iterator1 = charactersOnMap.iterator();
				while (iterator1.hasNext())
				{
					GameCharacter tempCharacter = iterator1.next();
					if(tempCharacter.getName() != "Player")
					{
						tempSightDistance = tempCharacter.getSightDistance();
						tempDirection = tempCharacter.getFaceDirection();
						tempX = tempCharacter.getCenterX();
						tempY = tempCharacter.getCenterY();
						if(tempDirection.name().equalsIgnoreCase("up"))
						{
							visionFieldPoints[0] = tempX; //x value of point centered on NPC
							visionFieldPoints[1] = tempY; //y value of point centered on NPC
							visionFieldPoints[2] = tempX - 0.8f*(tempSightDistance); 
							visionFieldPoints[3] = tempY + 0.8f*(tempSightDistance);
							visionFieldPoints[4] = tempX;
							visionFieldPoints[5] = tempY + tempSightDistance;
							visionFieldPoints[6] = tempX +  0.8f*(tempSightDistance);
							visionFieldPoints[7] = tempY +  0.8f*(tempSightDistance); 
						}
						else if(tempDirection.name().equalsIgnoreCase("down"))
						{
							visionFieldPoints[0] = tempX; //x value of point centered on NPC
							visionFieldPoints[1] = tempY; //y value of point centered on NPC
							visionFieldPoints[2] = tempX -  0.8f*(tempSightDistance); 
							visionFieldPoints[3] = tempY -  0.8f*(tempSightDistance);
							visionFieldPoints[4] = tempX;
							visionFieldPoints[5] = tempY - tempSightDistance;
							visionFieldPoints[6] = tempX +  0.8f*(tempSightDistance);
							visionFieldPoints[7] = tempY -  0.8f*(tempSightDistance); 
						}
						else if(tempDirection.name().equalsIgnoreCase("left"))
						{
							visionFieldPoints[0] = tempX; //x value of point centered on NPC
							visionFieldPoints[1] = tempY; //y value of point centered on NPC
							visionFieldPoints[2] = tempX -  0.8f*(tempSightDistance); 
							visionFieldPoints[3] = tempY - 0.8f*(tempSightDistance);
							visionFieldPoints[4] = tempX - tempSightDistance;
							visionFieldPoints[5] = tempY;
							visionFieldPoints[6] = tempX - 0.8f*(tempSightDistance);
							visionFieldPoints[7] = tempY + 0.8f*(tempSightDistance); 
						}
						else //facing right
						{
							visionFieldPoints[0] = tempX; //x value of point centered on NPC
							visionFieldPoints[1] = tempY; //y value of point centered on NPC
							visionFieldPoints[2] = tempX + 0.8f*(tempSightDistance); 
							visionFieldPoints[3] = tempY - 0.8f*(tempSightDistance);
							visionFieldPoints[4] = tempX + tempSightDistance;
							visionFieldPoints[5] = tempY;
							visionFieldPoints[6] = tempX + 0.8f*(tempSightDistance);
							visionFieldPoints[7] = tempY + 0.8f*(tempSightDistance); 
						}
						
						shapeRenderer.setColor(0, 1, 0, 0.025f);
						shapeRenderer.polygon(visionFieldPoints);
					}
				}
				shapeRenderer.end();
	}

}
