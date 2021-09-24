package com.wonzii.flappy;


import static org.lwjgl.glfw.Callbacks.*;
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
	
	StateMachine gameState;
	
	/*Parameters for StartScreen*/
	private long startTime = System.currentTimeMillis();;
	private long elapsedTime = 0L;
	private boolean updateBirdStartScreen = false;
	/* -------------------------------- */
	
	/*Parameters for Running*/
	/* The purpose of below codes is to slow down the update cycle*/
	double delta = 0.0;
	// why divided by 60? to be able to invoke update() 60 times per every second
	// so UPS will be around 60
	double ns = 1000000000.0/60 ;
	long timer ;
	int updates = 0;
	int frames = 0;
	long lastTime;
	/* -------------------------------- */
	
	private int width = 1280;
	private int height = 720;

	private Thread thread;
	
	//gameover flag is defined in level
	//private boolean gameover = false;
	private boolean started = false;
	private boolean aborted = false;
	
	private long window;
	
	private Level level;
	
	public void start() throws InterruptedException {
		
		thread = new Thread(this, "Game");
		thread.start();
		
	}
	
	//invoked by thread start()
	public void run() {
		// init should be run in the same thread as where level, shader or other classes are instantiated
		init();
		
		gameState = StateMachine.StartScreen;
		
		while(gameState != StateMachine.Aborted)
		{
			switch(gameState)
			{
			case StartScreen:
				stateStartScreen();
				gameState = gameState.nextState(started, aborted);
				
				break;
			case Running:
				stateRunning();
				gameState = gameState.nextState(level.isGameOver(), aborted);
				break;
				
			case GameOver:
				stateGameOver();
				gameState = gameState.nextState(level.isGameOver(), aborted);
				break;

			default :
				
			}
		}
		
		
		/*State4 : Abort State*/
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		// Terminate GLFW and free the error callback
		glfwTerminate();
		
	}
	
	// Window, Shader, and level are initialized 
	private void init() {
		
		if (glfwInit() == false)
		{
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		/*
		 * 4th param : monitor the monitor to use for fullscreen mode, or NULL for windowed mode
		 * 5th param : share the window whose context to share resources with, or NULL to not share resources
		 * */
		window = glfwCreateWindow(width, height, "Flappy", NULL, NULL);
		
		if(window == NULL)
		{
			throw new IllegalStateException("Window's handle is Null");
		}
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		//Alternative to the below
		//if window object is defined in the separate class, use window.setPos() to set position  
		glfwSetWindowPos(window, (vidmode.width() - width)/2,( vidmode.height()-height) /2);
		
		
		glfwSetKeyCallback(window, new Input());
		
		/*
		 * KeyScancode experiment
		 * 
			Scan code is platform specific, so it is different from the default key binding 
			int scancode = glfwGetKeyScancode(GLFW_KEY_X);
			System.out.println("Scancode : " + scancode + ", " + GLFW_KEY_X);
		*/
		
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		
		// Create a new {@link GLCapabilities} instance for the OpenGL context that is current in the current thread.
		GL.createCapabilities();
		
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);

		//System.out.println("OpenGL: " + glGetString(GL_VERSION));
		
		Shader.loadAll();
		
		//	Projection matrix of Background, Bird, and Pipe
		Matrix4f pr_matrix = Matrix4f.orthographic(-10f, 10f, -10f * 9f / 16f, 10f * 9f / 16f, -1.0f, 1.0f); 
		
		/*
		 * deviation of size between pr_matrix1 and 2
		 *  
			 Why Text Shader requires bigger orthographic than level Shader?? 
			 
			 First the size of window itself is already fixed as 1280 by 720
			 so, bigger orthographic projection only means more detailed positioning of objects within the same window
			 
			 Second, should factor in the size of text ( 32 by 64 ) 
			 Bigger size of orthographic projection leads to smaller text object displayed in the fixed size window 
		*/
		// Projection matrix of Texture
		Matrix4f pr_matrix2 = Matrix4f.orthographic(-400f, 400f, -400f, 400f, -1.0f, 1.0f); 	
		
		glActiveTexture(GL_TEXTURE1);

		Shader.BG.setUniform4fv("pr_matrix", pr_matrix);
		//The value should be corresponding with the index number defined in glActiveTexture
		Shader.BG.setUniform1i("tex", 1);
		
		Shader.BIRD.setUniform4fv("pr_matrix", pr_matrix);
		Shader.BIRD.setUniform1i("tex", 1);
		
		Shader.PIPE.setUniform4fv("pr_matrix", pr_matrix);
		Shader.PIPE.setUniform1i("tex", 1);
		
		//
		Shader.TEXT.setUniform4fv("pr_matrix", pr_matrix2);
		Shader.TEXT.setUniform1i("tex", 1);		
		
		level = new Level();
	}
	
	 
	private void stateStartScreen()
	{
		/* State1: Start Screen */
		if(elapsedTime > 1000)
		{
			updateBirdStartScreen = true;
			startTime += 1000;
			elapsedTime = 0L;
		}else
		{
			updateBirdStartScreen = false;
			elapsedTime = System.currentTimeMillis() - startTime;
		}


		//Signal to start the game
		if(Input.isKeyDown(GLFW_KEY_SPACE))
		{
			//running = true;
			started = true;
		}
		// Signal to the state transition into Abort State
		if(Input.isKeyDown(GLFW_KEY_ESCAPE))
		{
			glfwSetWindowShouldClose(window, true);
			aborted = true;
		}
		// Hit 'x' in window
		if(glfwWindowShouldClose(window) == true )
		{
			aborted = true;
		}

		glfwPollEvents();
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		level.renderStartScreen(updateBirdStartScreen);
		glfwSwapBuffers(window);
	}
	
	private void stateRunning()
	{
		if(gameState.getStateJustChanged())
		{	
			level.initForRunningState();
			timer = System.currentTimeMillis();
			lastTime = System.nanoTime();
		}
		long now = System.nanoTime();
		delta += (now - lastTime) / ns;
		lastTime = now;

		if(delta >= 1.0)
		{
			delta--;
			update();
			updates++;
		}
		render();
		frames++;
		// to measure UPS and FPS
		if ( System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
			//				System.out.println(updates + " ups, "+frames + " fps");
			updates = 0;
			frames = 0;
		}

		if(Input.isKeyDown(GLFW_KEY_ESCAPE))
		{
			glfwSetWindowShouldClose(window, true);
			aborted = true;
		}
		// Hit 'x' in window
		if(glfwWindowShouldClose(window) == true )
		{
			aborted = true;
		}
	}
	private void stateGameOver() {
		//  Reset the parameters for objects ( pipe, bird ) in Running State 
//		if(gameState.getStateJustChanged())
//		{
//			possibly post process for bird collision
//		}
		if(Input.isKeyDown(GLFW_KEY_SPACE))
		{
			level.setGameOver(false);
		}
		if(Input.isKeyDown(GLFW_KEY_ESCAPE))
		{
			glfwSetWindowShouldClose(window, true);
			aborted = true;
		}
		if(glfwWindowShouldClose(window) == true )
		{
			aborted = true;
		}
		glfwPollEvents();
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		//First argument is redundant for Gameover state
		level.renderGameOver();
		glfwSwapBuffers(window);
	}

	private void update()
	{
		glfwPollEvents();
		level.update();
		/* Input testing 
		
		if(Input.isKeyDown(GLFW_KEY_SPACE))
		{
			 System.out.println("SPACE key is pressed and its binding is :"+GLFW_KEY_SPACE);
		}
		
		*/
		if(Input.isKeyDown(GLFW_KEY_ESCAPE))
		{
			  glfwSetWindowShouldClose(window, true);
		}
		
	}
	
	private void render()
	{
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		
		level.render(gameState);
		
		int i = glGetError();
		if ( i != GL_NO_ERROR)
		{
			System.out.println("LWJGL Error Code :" + i);
		}
		
		glfwSwapBuffers(window); 
	}
	
	public static void main(String[] args) {
		try {
			new Main().start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
