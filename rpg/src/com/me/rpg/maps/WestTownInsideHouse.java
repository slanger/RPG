package com.me.rpg.maps;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.me.rpg.World;

public class WestTownInsideHouse extends Map
{

	private static final long serialVersionUID = 3457569492167907359L;

	public static final String MAP_TMX_PATH = "maps/west_town/inside_house/inside_house.tmx";

	public WestTownInsideHouse(World world)
	{
		super(world, MapType.WEST_TOWN_INSIDE_HOUSE, MAP_TMX_PATH);

		// map setup
		setup();
	}

	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
	{
		inputStream.defaultReadObject();
		setup();
	}

}
