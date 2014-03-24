package com.me.rpg.maps;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.me.rpg.World;

public class WestTownMap extends Map
{

	private static final long serialVersionUID = -8881198591017547955L;

	public static final String MAP_TMX_PATH = "maps/west_town/west_town.tmx";

	public WestTownMap(World world)
	{
		super(world, MapType.WEST_TOWN, MAP_TMX_PATH);

		// map setup
		setup();
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		setup();
	}

}
