package com.wonzii.flappy.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

//https://www.glfw.org/docs/latest/input_guide.html#input_key
public class Input extends GLFWKeyCallback{
	
	// All the key bindings are defined in GLFW.class
	
	public static boolean[] keys = new boolean[65536]; 
	/*
	 * action : 
		#define GLFW_RELEASE   0
		The key or mouse button was released.
	
		#define GLFW_PRESS   1
		The key or mouse button was pressed.
	
		#define GLFW_REPEAT   2
		The key was held down until it repeated.
	*/
	
	
	public void invoke(long window, int key, int scancode, int action, int mods) {
		
		// press and repeat are counted as true
		
		keys[key] = action == GLFW.GLFW_RELEASE ? false:true ;
		//	System.out.println("Input Class: key => " + key + ", action" + action);
	
	}

	public static boolean isKeyDown(int keycode)
	{
		return keys[keycode];
	}
}
