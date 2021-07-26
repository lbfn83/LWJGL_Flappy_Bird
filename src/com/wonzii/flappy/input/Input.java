package com.wonzii.flappy.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

//This will be tied to glfwSetKeyCallback
public class Input extends GLFWKeyCallback{

	// int 대신에 boolean을 사용하는 이유는 ?
	// static인 이유는 instance로 만들지 않아도 이 변수에 접근이 가능해야 하기 때문
	public static boolean[] keys = new boolean[65536]; 
	// key corresponds with key bindings defined in GLFW.class
	// action : the key action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link GLFW#GLFW_RELEASE RELEASE}</td><td>{@link GLFW#GLFW_REPEAT REPEAT}</td></tr></table>
	public void invoke(long window, int key, int scancode, int action, int mods) {
		
		// key action has three states : release, press, repeat
		// the states that can be count as true is... press and repeat... 
		// so whole logic is other than RELEASE it should be true
		
		keys[key] = action == GLFW.GLFW_RELEASE ? false:true ;
	
	}

	public static boolean isKeyDown(int keycode)
	{
		return keys[keycode];
	}
}
