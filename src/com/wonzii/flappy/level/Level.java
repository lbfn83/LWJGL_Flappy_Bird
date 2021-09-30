package com.wonzii.flappy.level;

import java.util.Random;

import org.joml.Vector2f;

import static org.joml.Math.*;

import com.wonzii.flappy.Direction;
import com.wonzii.flappy.StateMachine;
import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.Texture;
import com.wonzii.flappy.graphics.VertexArray;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;

public class Level {

	
	private boolean[] birdIsInPipearea;
	private float prevBx0, prevBx1;
	private int score = 0;
	
	private VertexArray background;
	private Texture bgTexture;
	private int xScroll = 0;
	private int map = 0;
	private int index = 0;
	private Bird bird;
	private Pipe[] pipes = new Pipe[5*2];
	private Hud startText;
	private Hud gameoverText;
	private Hud scoreText1;
	private Hud scoreText2;
	/*random number*/
	private Random r;
	/* the range of y coordination of the upper pipe*/
	private final float randomMax = 5.5f;
	private final float randomMin = 0.0f;
	private final float startOffset = 10.0f;
	private final float pipeCreaRate = 3.0f;
	private float pipeMovingDistance;
	private Direction collisionDir = Direction.None;
	private float pipeVelocity = 0.05f;
	private int prevScore;
	private boolean updatePipeFlag;
	private double pipeTargetVelocity;
	private Direction persistentCollisionDir = Direction.None;
	private Direction prevPostCollision;
	
	
	public boolean isGameOver() throws Exception {
		if(bird instanceof Bird )
			return bird.isGameOver();
		throw new Exception("bird is not initialized yet");
	}
	
	public void setGameOver(boolean gameOver) throws Exception {
		if(bird instanceof Bird )
		{
			bird.setGameOver(gameOver);
		}
		else
		{
			throw new Exception("bird is not initialized yet");
		}
	}
	
	private float randomNumGen()
	{
		return randomMin + r.nextFloat() * (randomMax - randomMin);
	}

	public Level() {
		//First Texture for Background will cover only the half of screen (-10f / refer to orthographic)
		float[] vertices = new float[] {
			-10f, -10.0f * 9f / 16f, 0.0f,
			-10f, 10.0f * 9f / 16f, 0.0f,
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
		
		try {
			startText = new Hud("Click to Start");
			gameoverText = new Hud("Game Over");
			scoreText1 = new Hud("Score : ");
			scoreText2 = new Hud("0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		birdIsInPipearea = new boolean[2*5];
		
	}
	private void createPipes() {
		//create mesh for PIPE class
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
			
			System.out.println("pipe's "+i +"th x coord is " +pipes[i].getPosition().x);
			index = index + 2;
		}
		
	}
	
	private void scoreCount(StateMachine gameState)
	{
		//좌표가 계속 다이나믹하게 움직이는 건 bird네. 
		float bx0 = -pipeMovingDistance - bird.getSize()/2;
		float bx1 = -pipeMovingDistance + bird.getSize()/2;
		/*if game has just started skip the below code*/
		if (!gameState.getStateJustChanged())
		{
			// only count upper pipe?
			for ( int i = 0; i < 2*5 ; i= i+2)
			{ 
				float px0 = pipes[i].getPosition().x ;
				float px1 = pipes[i].getPosition().x + Pipe.getWidth();
 				if((px0 < prevBx1 && px1 > prevBx0 ) &&  (px0 < bx1 && px1 > bx0) && !birdIsInPipearea[i])
				{
   					score++;
					birdIsInPipearea[i] = true;
					//TODO 이 아래 있는건... score 더할 때만 해주기로 하자
					try {
						scoreText2 = new Hud(String.valueOf(score));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(!(px0 < bx1 && px1 > bx0))
				{
					birdIsInPipearea[i] = false;
				}
 				if ((prevScore != score) && (score%2 == 0) )
 				{
 					pipeTargetVelocity = pipeVelocity*1.2;
 				}
					
			}
			
		}
		prevScore = score;
		prevBx0 = bx0;
		prevBx1 = bx1;
		
	}
	
	private Direction collisionDetection()
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

		if(persistentCollisionDir != Direction.None)
		{
			bx0 += bird.getX();
			bx1 += bird.getX();
		}
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
				{	
					System.out.println("collision");
					// Center point of pipe is required for the resolution calculation
					
					return collisionResolution(-pipeMovingDistance, bird.getY(),(px0 + px1)/2f, (py0+py1)/2f);
				}
			}
		}

		return Direction.None;
	}
	
	private Direction collisionResolution(float birdX, float birdY,float pipeX, float pipeY)
	{
		Vector2f aabb2_halfExtent = new Vector2f(Pipe.getWidth()/2f , Pipe.getHeight()/2f);
		// get difference vector between both centers
		Vector2f diffCenters = new Vector2f( birdX - pipeX, birdY - pipeY);
		
		//Closest point on the edge of colliding pipe
		float closestX = pipeX + clamp(-(aabb2_halfExtent.x), (aabb2_halfExtent.x) , diffCenters.x); 
		float closestY = pipeY + clamp(-(aabb2_halfExtent.y), (aabb2_halfExtent.y) , diffCenters.y); 
		
		aabb2_halfExtent = null;
		diffCenters  = null;
		
		Direction a = vectorDirection(new Vector2f(closestX - birdX, closestY - birdY) );
		
		System.out.println(a);
		switch (a) {
		case Right:
			
			break;
		case Bottom:
			
			break;
			
			
		default:
			break;
		}
		return a;
	}
	private Direction vectorDirection(Vector2f distVect)
	{
		Vector2f[] dirVector = new Vector2f[4];
		dirVector[0] = new Vector2f(0.0f, 1.0f); // top in perspective of Bird
		dirVector[1] = new Vector2f(0.0f, -1.0f); // bottom in perspective of Bird
		dirVector[2] = new Vector2f(-1.0f, 0.0f); // left
		dirVector[3] = new Vector2f(1.0f, 0.0f); // right
		
		float max = 0.0f;
		int best_match = -1;
		for(int i = 0; i < 4; i++ )
		{
 			float dot_product = DotProduct(distVect.normalize(), dirVector[i]);
			
			if(dot_product > max)
			{
				max = dot_product;
				best_match = i;
			}
		} 
 		Direction.values();
		
		return Direction.getValue(best_match);
		
	}
	private float DotProduct(Vector2f a, Vector2f b)
	{
		return a.x * b.x + a.y * b.y;
	}
	
	/******************************************************/
	//Get the game screen ready for new game play
	public void initForRunningState()
	{
		resetScore();
		repositionPipes();
		resetxScroll();
		resetmap();
		resetpostCollisionControl();
		initBirdPosition();
		resetPipeMovingParameters();
	}
	private void resetPipeMovingParameters()
	{
		pipeTargetVelocity = 0.0f;
		pipeVelocity = 0.05f;
	}
	private void resetpostCollisionControl() {
		collisionDir = Direction.None;
		persistentCollisionDir = Direction.None;
	}

	private void resetScore()
	{
		score = 0;
		try {
			
			scoreText2 = new Hud(String.valueOf(score));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void repositionPipes()
	{
		// reset required; Pipe's positioning is based on index
		index = 0;
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
	private void resetxScroll() {
		this.xScroll = 0;
	}
	private void resetmap() {
		this.map = 0;
	}
	private void initBirdPosition()
	{
		bird.setPosition(new Vector3f(0f, 0f, 0f));
	}
	
	/******************************************************/
	
	          
	public void update()  {
		collisionDir = collisionDetection();
		// Need a variable 'persistentCollisionDir' which can hold the collision direction persistently once it is detected
		if((collisionDir != Direction.None) && (prevPostCollision != collisionDir) )
		{
			
			persistentCollisionDir = collisionDir;
			
			//When the bottom of a bird hit the pipe, bird should spring up a little
			if(collisionDir == Direction.Bottom)
			{	
				bird.bottomCollisionOnce();
			}
		}
		if(persistentCollisionDir == Direction.None)
		{
			xScroll--; // Tick
			
			// 334 is a divisor used for modulo calculation and 0.03 is used for translation movement of background in render() 
			// As each background image should be placed side by side, the offset of each image is 10*i in translation matrix calculation
			// When map is increased, xscroll*0.03f should be -10 to offset i * 10
			if (-xScroll%334 == 0)
			{
				map++;
			} 
			
			/*When any one of pipes( must be leftmost pipe ) is reached outside of screen, then updatePipe should be called*/
			if(updatePipeFlag)
			{
				updatePipes();
				updatePipeFlag = false;
			}
		}

		bird.update(persistentCollisionDir, pipeVelocity); 
		
		prevPostCollision = collisionDir;
	}
	

	// relocate the leftmost pipe images
	private void updatePipes() 
	{
   		float yCoord = randomNumGen();
		pipes[index % 10] = new Pipe(startOffset + (index)*pipeCreaRate, yCoord);
		pipes[(index % 10) + 1] = new Pipe(pipes[index % 10].getPosition().x,  yCoord - 5 - 8);
		/*debug
		System.out.println( "Game's moving tick is : " + xScroll);
		System.out.println( "Pipe's " +index +"th initial  x coodrination: " + pipes[index%10].getPosition().x);
		 */
		index = index +2;
	}
	
	/****************************************/
	
	// render's cycle is way faster than update. it is not really necessary to render cycle this fast. 
	public void render(StateMachine gameState) {

		scoreCount(gameState);
		
		bgTexture.bind();
		Shader.BG.enable();
		background.bind();
		
		// 3 is the number of same image copies put together side by side
		// map will be counted up to +1 when xScroll(- value) is counted up to every 334 so that it can offset the increase of xScroll value   
		for(int i = map; i < map + 3 ; i++ )
		{
			Shader.BG.setUniform4fv("vw_matrix", Matrix4f.translate(new Vector3f(i * 10 + xScroll*0.03f, 0.0f, 0.0f)));
			background.draw();
		}

			
		background.unbind();
		Shader.BG.disable();
		bgTexture.unbind();

		renderPipe();

		bird.render();
		
		scoreText1.getStatusTextItem().renderText(StateMachine.Running);
		scoreText2.getStatusTextItem().renderText(StateMachine.Running);
	}
	
	private void renderPipe() {
		/*Incremental speed up / pipeVel should be incremented slowly for smooth rendering. */
		if(pipeTargetVelocity > pipeVelocity && (collisionDir == Direction.None))
		{
			pipeVelocity +=0.000001f;
		}
		pipeMovingDistance = xScroll*pipeVelocity;
		
		Shader.PIPE.enable();
		Shader.PIPE.setUniform2f("bird", 0, bird.getY());
		
		Shader.PIPE.setUniform4fv("vw_matrix", Matrix4f.translate(new Vector3f( pipeMovingDistance, 0.0f, 0.0f)));
		
		/*Check each pipe's x coordination to determine the timing of update on pipe object*/
		Matrix4f[] pipeXCoordCheck = new Matrix4f[5]; 
				
		for(int k=0; k<5;k++)
		{
			pipeXCoordCheck[k] = Matrix4f.translate(new Vector3f( pipeMovingDistance, 0.0f, 0.0f)).multiply(pipes[k*2].getMl_Matrix());
			
			/*When the leftmost pipes( must be leftmost pipe ) reach outside of window, pipe should be updated to relocate the leftmost to the rightmost*/
			/*Column major matrix's 12th element has x coordination value*/
			if(pipeXCoordCheck[k].elements[12] < -10f - Pipe.getWidth())
			{
				updatePipeFlag = true;
				break;
			}
		}
		Pipe.getTexture().bind();
		Pipe.getMesh().bind();
		
		for(int i=0; i<2*5;i++)
		{
			Shader.PIPE.setUniform1i("top", (i % 2)==0? 1: 0);
			
			Shader.PIPE.setUniform4fv("ml_matrix", pipes[i].getMl_Matrix());
			
			Pipe.getMesh().draw();
		}
		
		Pipe.getMesh().unbind();
		Pipe.getTexture().unbind();	
		Shader.PIPE.disable();
	}
	
	/************************************/	
	
	public void renderStartScreen(boolean updateBirdStartScreen)
	{
		bgTexture.bind();
		Shader.BG.enable();
		background.bind();
		
		// 2 background images are wide enough to cover the whole window as background is not moving at StartScreen state
		for(int i = 0; i < 2; i++ )
		{
			Shader.BG.setUniform4fv("vw_matrix", Matrix4f.translate(new Vector3f(i * 10, 0.0f, 0.0f)));
			background.draw();
		}
		Shader.BG.disable();
		bgTexture.unbind();
		background.unbind();
		bird.renderBirdStartSceen(updateBirdStartScreen);
		startText.getStatusTextItem().renderText(StateMachine.StartScreen);
			
	}
	
	
	public void renderGameOver()
	{
		bgTexture.bind();
		Shader.BG.enable();
		background.bind();
		
		//recycle the value of map and xScroll as they were at Running State
		//so Gameover background images are rendered smoothly as it is extended from the moment when the bird collided 
		for(int i = map; i < map+4; i++ )
		{
			Shader.BG.setUniform4fv("vw_matrix", Matrix4f.translate(new Vector3f(i * 10 + xScroll*0.03f, 0.0f, 0.0f)));
			background.draw();
		}
		Shader.BG.disable();
		bgTexture.unbind();
		gameoverText.getStatusTextItem().renderText(StateMachine.GameOver);
	
	}
}
