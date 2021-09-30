package com.wonzii.flappy;

//This enum is used for collision Resolution

public enum Direction {
	Top(0), 
	Bottom(1), 
	Left(2), 
	Right(3),
	None(100);
	int id;
	private Direction(int i)
	{
		id = i;
	}
	private boolean compare(int i)
	{
		return id == i;
	}
	public static Direction getValue(int id)
	{
		Direction[] values = Direction.values();
		for(int i = 0; i < values.length ; i++)
		{
			if(values[i].compare(id))
			{
				return values[i];
			}
		}
		return Direction.None;
	}
}
