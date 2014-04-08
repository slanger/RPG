package com.me.rpg.maps;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.me.rpg.World;

public class ExampleMap extends Map
{

	private static final long serialVersionUID = -8785472881082229152L;

	public static final String MAP_TMX_PATH = "maps/example/example.tmx";

	public ExampleMap(World world)
	{
		super(world, MapType.EXAMPLE, MAP_TMX_PATH);
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		setup();
	}

}
