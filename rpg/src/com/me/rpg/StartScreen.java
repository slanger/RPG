package com.me.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class StartScreen implements Screen, InputProcessor
{

	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Table table;
	private ScreenHandler screenHandler;
	private SpriteBatch spriteBatch = new SpriteBatch();
	private Texture mainScreenTexture;
	private int width, height;
	private TextButton buttonPlay, buttonSettings;
	private BitmapFont white;
	private Label heading;

	// CONSTRUCTOR
	public StartScreen(ScreenHandler screenHandler)
	{
		this.screenHandler = screenHandler;
	}

	@Override
	public void show()
	{
		mainScreenTexture = new Texture(
				Gdx.files.internal("images/MainScreen_new.png"));
		stage = new Stage();

		// INPUT PROCESSOR
		Gdx.input.setInputProcessor(stage);

		// CREATE ATLAS, TABLE, SKIN
		atlas = new TextureAtlas("UI/buttons/button.pack");
		skin = new Skin(atlas);
		table = new Table(skin);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		white = new BitmapFont(Gdx.files.internal("font/white32.fnt.txt"),
				false);

		// CREATE BUTTONS, BUTTONSTYLES
		TextButtonStyle textPlayButtonStyle = new TextButtonStyle();
		textPlayButtonStyle.up = skin.getDrawable("playUP");
		textPlayButtonStyle.down = skin.getDrawable("playDOWN");
		textPlayButtonStyle.pressedOffsetX = 1;
		textPlayButtonStyle.pressedOffsetY = -1;
		textPlayButtonStyle.font = white;
		textPlayButtonStyle.fontColor = Color.BLACK;

		TextButtonStyle textScoreButtonStyle = new TextButtonStyle();
		textScoreButtonStyle.up = skin.getDrawable("scoreUP");
		textScoreButtonStyle.down = skin.getDrawable("ScoreDOWN");
		textScoreButtonStyle.pressedOffsetX = 1;
		textScoreButtonStyle.pressedOffsetY = -1;
		textScoreButtonStyle.font = white;
		textScoreButtonStyle.fontColor = Color.BLACK;
		buttonPlay = new TextButton("", textPlayButtonStyle);
		buttonSettings = new TextButton("", textScoreButtonStyle);

		// if Enter is pressed, go to game screen
		// TODO add keyboard functionality to start menu -Mark
		stage.addListener(new InputListener()
		{
			@Override
			public boolean keyUp(InputEvent event, int keycode)
			{
				if (keycode == Keys.ENTER)
				{
					stage.removeListener(this);
					screenHandler.moveToOtherScreen(screenHandler.rpgScreen);
				}
				return true;
			}
		});

		// PAD MARGINS BUTTONS
		buttonPlay.pad(20);
		buttonSettings.pad(20);

		// *****ADD EVERYTHING TO TABLE******
		table.add(heading).spaceBottom(15).row();
		table.add(buttonPlay).spaceBottom(15).row();
		table.add(buttonSettings).spaceBottom(15).row();

		table.debug();

		// ADD TABLE (aka ACTOR) TO STAGE
		stage.addActor(table);
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		spriteBatch.begin();
		spriteBatch.draw(mainScreenTexture, 0, 0, width, height);
		spriteBatch.end();

		// RENDER STAGE
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	@Override
	public void hide()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose()
	{
		stage.dispose();
		skin.dispose();
		atlas.dispose();
		spriteBatch.dispose();
		mainScreenTexture.dispose();
		white.dispose();
	}

	@Override
	public boolean keyDown(int keycode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
