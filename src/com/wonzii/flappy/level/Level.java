package com.wonzii.flappy.level;

import java.util.Random;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.Texture;
import com.wonzii.flappy.graphics.VertexArray;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;

public class Level {

	
	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	private boolean gameOver;
	private VertexArray background;
	private Texture bgTexture;
	private int xScroll = 0;
	private int map = 0;
	private int index = 0;
	private Bird bird;
	private Pipe[] pipes = new Pipe[5*2];
	/*random number*/
	private Random r;
	/*possible y coordination of the upper pipe*/
	private final float randomMax = 6f;
	private final float randomMin = -0.0f;
	private final float startOffset = 5.0f;
	private final float pipeCreaRate = 3.0f;
	private float pipeMovingDistance;
	
	
	private float randomNumGen()
	{
		return randomMin + r.nextFloat() * (randomMax - randomMin);
	}
	
	public Level() {
		float[] vertices = new float[] {
			-10.0f, -10.0f * 9f / 16f, 0.0f,
			-10.0f, 10.0f * 9f / 16f, 0.0f,
			0.0f, 10.0f * 9f / 16f, 0.0f,
			0.0f, -10.0f * 9f / 16f, 0.0f
		};
		
		byte[] indices = new byte[] {
				0, 1, 2,
				2, 3, 0
		};
		
		float[] tcs = new float[] {
				0f ,1f ,
				0f ,0f , 
				1f ,0f ,
				1f ,1f
		};
		
		background = new VertexArray(vertices, indices, tcs);
		bgTexture = new Texture("res/bg.jpeg");
		bird = new Bird();

		createPipes();
		  
	}
	
	private boolean collision()
	{
		// To be able to compare the real position of bird against the pipe, one way to do it is to know 
		// the output of vertex shader formula after view and model matrices applied, which is hard
		// Instead, by reflecting the pipe moving distance calculated from the tick ( xscroll ) into bird, 
		// the input data of both shader programs can be comparable. 
		// Pipe has the position configured initially before view matrix and Bird has the position that
		// reflects the movement of ticks
		
		float bx0 = -pipeMovingDistance - bird.getSize()/2;
		float bx1 = -pipeMovingDistance + bird.getSize()/2;
		float by0 = bird.getY() - bird.getSize()/2;
		float by1 =	bird.getY() + bird.getSize()/2;
		
		// pipe array position : left lower point is 0, 0 
		for ( int i = 0; i < 5*2 ; i++)
		{
			float px0 = pipes[i].getPosition().x ;
			float px1 = pipes[i].getPosition().x + Pipe.getWidth();
			float py0 = pipes[i].getPosition().y;
			float py1 = pipes[i].getPosition().y + Pipe.getHeight();
		
			if( px0 < bx1 && px1 > bx0)
			{
				if(py1 > by0 && by1 > py0)
					System.out.println("collision");
			}
		}
		return false;
	}
	
	private void createPipes() {
		//create vertex array, texture of Pipe class
		Pipe.createPipes();
		
		r = new Random();
		
		//coordination of each pipe element
		for( int i=0; i<2*5; i+=2)
		{
			float yCoord = randomNumGen();
			float weight = 1f;//r.nextFloat()+0.01f;
			
			pipes[i] = new Pipe(startOffset +(float)i*pipeCreaRate*weight, yCoord);
			// 4 is a gap between up and down / 8 is the length of pipe
			pipes[i+1] = new Pipe(pipes[i].getPosition().x, yCoord - 5 - 8);
			
			
			index = index + 2;
		}
		
	}
	
	
	// Recycle the pipes 
	public void updatePipes() 
	{
		float yCoord = randomNumGen();
		pipes[index % 10] = new Pipe(startOffset + (index)*pipeCreaRate, yCoord);
		pipes[(index % 10) + 1] = new Pipe(pipes[index % 10].getPosition().x,  yCoord - 5 - 8);
		System.out.println( "Pips's moving weight of x coodrination: " + xScroll*0.05f);
		System.out.println( "Pipe's " +index +"th initial  x coodrination: " + pipes[index%10].getPosition().x);
		index = index +2;
	}
	          
	public void update() {
		//the degree of movement the meshes makes in the leftward of the screen

		xScroll--;
		System.out.println("xScroll : " + xScroll);
		// 335 and xScroll * 0.03 => 
		// the width of display is 10 so translation matrix vector starts with i*10
		// every multiple of 335 will increase the count of map 
		// it is because xScroll won't be reset but still counting down while game is running
		//When map is increased, xscroll*0.03f should be -10 to offset i * 10
		if (-xScroll%334 == 0)
		{
			map++;
		}
  		if( -xScroll > 250 && -xScroll % 120 == 0 )
		{
			updatePipes();
		}
		//This is for calculating tick 
		bird.update();
	}
	

	
	
	
	
	public void renderPipe() {

		pipeMovingDistance = xScroll*0.05f;
		Shader.PIPE.enable();
		Shader.PIPE.setUniform2f("bird", 0, bird.getY());
		
		Shader.PIPE.setUniform4fv("vw_matrix", Matrix4f.translate(new Vector3f( pipeMovingDistance, 0.0f, 0.0f)));
		
		Pipe.getTexture().bind();
		Pipe.getMesh().bind();
		
		for(int i=0; i<2*5;i++)
		{
			Shader.PIPE.setUniform1i("top", (i % 2)==0? 1: 0);
			
			
			Shader.PIPE.setUniform4fv("ml_matrix", pipes[i].getMl_Matrix());
			
			Pipe.getMesh().draw();
		}
		
		Shader.PIPE.disable();
		Pipe.getMesh().unbind();
		Pipe.getTexture().unbind();	
		
	}
	
	// since render's cycle is way faster than update. for this game, it is not really necessary to render cycle this fast. 
	// there must be split seconds where shaders rendering meshes at the same coordination for hundreds of times but it is less than milliseconds job
	// so eyes can only perceive background flowing smoothly.
	public void render() {
		collision();
		bgTexture.bind();
		Shader.BG.enable();
		background.bind();
		
		
		// 3 is used to put together three copies of images
		// The Model matrix 
		// http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
		// View matrix is not the right terminology
		// Coordination rule :  
		for(int i = map; i < map + 4 ; i++ )
		{
			Shader.BG.setUniform4fv("vw_matrix", Matrix4f.translate(new Vector3f(i * 10 + xScroll*0.03f, 0.0f, 0.0f)));
			background.draw();
		}

			
		renderPipe();
		
		Shader.BG.disable();
		bgTexture.unbind();
		bird.render();
		
		
	}
	public void initBirdPosition()
	{
		bird.setPosition(new Vector3f(0f, 0f, 0f));
	}
	public void initRender(boolean runBird)
	{
		bgTexture.bind();
		Shader.BG.enable();
		background.bind();
		
		
		// 3 is used to put together three copies of images
		// The Model matrix 
		// http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
		// View matrix is not the right terminology
		// Coordination rule :  
		for(int i = 0; i < 2; i++ )
		{
			Shader.BG.setUniform4fv("vw_matrix", Matrix4f.translate(new Vector3f(i * 10, 0.0f, 0.0f)));
			background.draw();
		}
		Shader.BG.disable();
		bgTexture.unbind();
		if(runBird)
		{ 
			bird.renderBirdInit(3);
		}
		

	}
}
