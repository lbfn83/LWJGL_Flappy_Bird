package com.wonzii.flappy;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.glfw.Callbacks.*;
import java.util.Date;
import java.util.Timer;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.input.Input;
import com.wonzii.flappy.level.Bird;
import com.wonzii.flappy.level.Level;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;

public class Main implements Runnable {
	
	StateMachine gameState;
	
	/*Parameters for StartScreen*/
	private long startTime = System.currentTimeMillis();;
	private long elapsedTime = 0L;
	private boolean addBird = false;
	/* -------------------------------- */
	
	/*Parameters for Running*/
	/* Below codes are to slow down the update cycle*/
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
//	private boolean gameover = false;
	private boolean started = false;
	private boolean aborted = false;
	
	private long window;
	
	private Level level;
	
	public void start() throws InterruptedException {
		
		thread = new Thread(this, "Game");
		thread.start();
//		thread.join();
	}
	
	private void init() {
		if (glfwInit() == false)
		{
			//TODO: create handle 
			
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
		
		//when we created the window object => using window.setPos() to set position  
		glfwSetWindowPos(window, (vidmode.width() - width)/2,( vidmode.height()-height) /2);
		
		
		glfwSetKeyCallback(window, new Input());
		
		/*Scan code is different from default key binding since it is platform specific*/
		int scancode = glfwGetKeyScancode(GLFW_KEY_X);
//		System.out.println("Scancode : " + scancode + ", " + GLFW_KEY_X);
		
		
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		
		//* Creates a new {@link GLCapabilities} instance for the OpenGL context that is current in the current thread.
		GL.createCapabilities();
		
		// * Enables the specified OpenGL state.
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE1);
//		System.out.println("OpenGL: " + glGetString(GL_VERSION));
		
		//Is it created as an instance? 
		//the singleton pattern : restricts the instantiation of a class to one "single" instance.
		//examine Shader class
		Shader.loadAll();
		

		//	Projection matrix. since it is used universally among all the Shader instances, defined in Main class
		// https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter06/chapter6.html
		//  perspective projection and  orthographic projection
		Matrix4f pr_matrix = Matrix4f.orthographic(-10f, 10f, -10f * 9f / 16f, 10f * 9f / 16f, -1.0f, 1.0f); 
		
		// Why Text Shader requires bigger orthographic than level Shader?? 
		// First the size of window itself is already fixed as 1280 by 720
		// so, bigger orthographic projection only means more detailed positioning of objects inside the projection
		// Second, should factor in the size of text ( 32 by 64 ) 
		// Bigger you configure orthographic projection, Smaller the text object inside that projection
		// In this regard, the projection should be big enough to accomodate an array of text objects
		Matrix4f pr_matrix2 = Matrix4f.orthographic(-400f, 400f, -400f, 400f, -1.0f, 1.0f); 	
		Shader.BG.setUniform4fv("pr_matrix", pr_matrix);
		//The value should be corresponding with the Texture number defined in glActiveTexture
		Shader.BG.setUniform1i("tex", 1);
		
		Shader.BIRD.setUniform4fv("pr_matrix", pr_matrix);
		//The value should be corresponding with the Texture number defined in glActiveTexture
		Shader.BIRD.setUniform1i("tex", 1);
		
		Shader.PIPE.setUniform4fv("pr_matrix", pr_matrix);
		//The value should be corresponding with the Texture number defined in glActiveTexture
		Shader.PIPE.setUniform1i("tex", 1);
		
		Shader.TEXT.setUniform4fv("pr_matrix", pr_matrix2);
		//The value should be corresponding with the Texture number defined in glActiveTexture
		Shader.TEXT.setUniform1i("tex", 1);		
		
		//TODO : 이건 game over가 되었을 때... 다시 한 번 불러와서.. 초기화해줘야 할  
		level = new Level();
		
	}
	

	 
	
//from runnable interface / invoked by thread start()
	public void run() {
		
		//make sure init() can't run on the standard thread. 
		//because all the parameters derived from level, Shader, and other classes 
		//defined in this thread
		// Init the window
		init();
		
		gameState = StateMachine.StartScreen;
		
		while(gameState != StateMachine.Aborted)
		{
			switch(gameState)
			{
			case StartScreen:
				System.out.println("StartScreen");
				stateStartScreen();
				gameState = gameState.nextState(started, aborted);
				
				break;
			case Running:
				System.out.println("Running");
				stateRunning();
				gameState = gameState.nextState(level.isGameOver(), aborted);
				break;

			case GameOver:
				System.out.println("GameOver");
				stateGameOver();
				gameState = gameState.nextState(level.isGameOver(), aborted);
				break;
			}
		}
		

		/*State4 : Abort State*/
		
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		// Terminate GLFW and free the error callback
		glfwTerminate();
		
	}
	private void stateStartScreen()
	{
		/* State1: Start Screen */
		if(elapsedTime > 1000)
		{
			addBird = true;
			startTime += 1000;
			elapsedTime = 0L;
		}else
		{
			addBird = false;
			elapsedTime = System.currentTimeMillis() - startTime;
		}


		//Signal to start the game
		if(Input.isKeyDown(GLFW_KEY_SPACE))
		{
			//				running = true;
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
		level.renderStartScreen(addBird);
		glfwSwapBuffers(window);
	}
	
	private void stateRunning()
	{
		if(gameState.getStateJustChanged())
		{	
			level.repositionPipes();
			level.initBirdPosition();
			level.resetxScroll();
			level.resetmap();
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
//			level.initBirdPosition();
//			level.setxScroll(0);
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
		// Hit 'x' in window
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
		
		
		/* Input testing */
		if(Input.isKeyDown(GLFW_KEY_SPACE))
		{
			 // System.out.println("SPACE key binidng is "+GLFW_KEY_SPACE);
		}
		if(Input.isKeyDown(GLFW_KEY_ESCAPE))
		{
			  glfwSetWindowShouldClose(window, true);
		}
		
	}
	
	private void render()
	{
		//* Sets portions of every pixel in a particular buffer to the same value. 
		//The value to which each buffer is cleared depends on the setting of the clear
	    //* value for that buffer.
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
		level.render();
		
		int i = glGetError();
		if ( i != GL_NO_ERROR)
		{
			System.out.println("LWJGL Error Code :" + i);
		}
		
		
		glfwSwapBuffers(window); 
	}
	public static void main(String[] args) {
		// 'new' => instantiate Main class implicitly
		try {
			new Main().start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
