package com.wonzii.flappy;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.input.Input;
import com.wonzii.flappy.level.Level;
import com.wonzii.flappy.math.Matrix4f;

public class Main implements Runnable {

	private int width = 1280;
	private int height = 720;

	private Thread thread;
	
	private boolean running = true;
	
	// identifier 
	private long window;
	
	private Level level;
	
	public void start() {
		thread = new Thread(this, "Game");
		thread.start();
	}
	// from thread class
	private void init() {
		if (glfwInit() == false)
		{
			//TODO: handle 
			
		}
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		// NULL should be library specific defined variable
		// import static org.lwjgl.system.MemoryUtil.*;
		window = glfwCreateWindow(width, height, "Flappy", NULL, NULL);
		
		if(window == NULL)
		{
			return;
		}
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		//when we created the object => This is how set position -> window.setPos()
		glfwSetWindowPos(window, (vidmode.width() - width)/2,( vidmode.height()-height) /2);
		
		
		glfwSetKeyCallback(window, new Input());
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		//* Creates a new {@link GLCapabilities} instance for the OpenGL context that is current in the current thread.
		GL.createCapabilities();
		
		// * Enables the specified OpenGL state.
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE1);
		
		System.out.println("OpenGL: " + glGetString(GL_VERSION));
		
		Shader.loadAll();
		
	
		Matrix4f pr_matrix = Matrix4f.orthographic(-10f, 10f, -10f * 9f / 16f, 10f * 9f / 16f, -1.0f, 1.0f); 
		Shader.BG.setUniform4f("pr_matrix", pr_matrix);
		Shader.BG.setUniform1i("tex", 1);
		
		level = new Level();
	}
	// from runnable interface / invoked by thread start()
	public void run() {
		
		// make sure init() can't run on the standard thread. because we have to do everything on this thread
		init();
		while(running)
		{
			update();
			render();
			
			if(glfwWindowShouldClose(window) == true )
			{
				running = false;
			}
		}
	}
	private void update()
	{
		glfwPollEvents();
		if(Input.keys[GLFW_KEY_SPACE])
		{
//			  System.out.println("oh crap");
		}
	}
	private void render()
	{
		//* Sets portions of every pixel in a particular buffer to the same value. The value to which each buffer is cleared depends on the setting of the clear
	     //* value for that buffer.
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		level.render();
		
		int i = glGetError();
		if ( i != GL_NO_ERROR)
		{
			System.out.println(i);
		}
		
		
		glfwSwapBuffers(window);
	}
	public static void main(String[] args) {
		// TODO : understand the below statement
		// new is for instantiation
		// yet I saw class itself is instantiated but never seen the case instantiate the method
		new Main().start();
		
		
	}

}
