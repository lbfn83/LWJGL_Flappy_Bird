package com.wonzii.flappy.math;

public class Vector3f {
	//this is 2D game , z will be rendering order, which is related to depth test
	// bird will have the highest order
	// bird might be 1, background -5
	public float x, y, z;
	
	public Vector3f()
	{
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}
	public Vector3f(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		 
	}
}
