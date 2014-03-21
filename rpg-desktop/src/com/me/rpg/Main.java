package com.me.rpg;

import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main
{

	public static void main(String[] args)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "The Legend of the Diforce";
		cfg.useGL20 = true;
		
		
		cfg.width = 700;
		cfg.height = 700;
		new LwjglApplication(new ScreenHandler(), cfg);
	}

}
