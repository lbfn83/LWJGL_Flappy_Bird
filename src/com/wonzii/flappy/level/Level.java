package com.wonzii.flappy.level;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.Texture;
import com.wonzii.flappy.graphics.VertexArray;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;

public class Level {

	private VertexArray background;
	private Texture bgTexture;
	private int xScroll = 0;
	private int map = 0;
	private int index = 0;
	private Bird bird;
	private Pipe[] pipes = new Pipe[5*2];
	
	
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
		//create vertex array, texture of Pipe class
		Pipe.createPipes();
		for( int i=0; i<2*5; i+=2)
		{
			pipes[i] = new Pipe(0f +(float)i*2f, 1f);
			
			pipes[i+1] = new Pipe(0f +(float)i*2f, -5f);
			
			index = index + 2;
		}
		
		
	}
	// Recycle the pipes 
	public void updatePipes() 
	{
		pipes[index % 10] = new Pipe(pipes[(index-1) % 10].getPosition().x + 5f, 1f);
		pipes[(index % 10) + 1] = new Pipe(pipes[(index-1) % 10].getPosition().x + 5f, -5f);
		
		index = index +2;
	}
	
	public void update() {
		//the degree of movement the meshes makes in the leftward of the screen

		xScroll--;
		
		// 335 and xScroll * 0.03 => 
		// the width of display is 10 so translation matrix vector starts with i*10
		// every multiple of 335 will increase the count of map 
		// it is because xScroll won't be reset but still counting down while game is running
		//When map is increased, xscroll*0.03f should be -10 to offset i * 10
		if (-xScroll%334 == 0)
		{
			map++;
		}
  		if( -xScroll > 300 && -xScroll % 120 == 0 )
		{
			updatePipes();
		}
		
		System.out.println(xScroll);
		bird.update();
	}
	
	
	
	
	
	
	public void renderPipe(int index) {

		Shader.PIPE.enable();
		Shader.PIPE.setUniform4f("vw_matrix", Matrix4f.translate(pipes[index].getPosition()));
		
		if(index % 2 == 0)
			Shader.PIPE.setUniform1i("top", 1);
		else
			Shader.PIPE.setUniform1i("top", 0);
		
		Pipe.getTexture().bind();
		Pipe.getMesh().render();

		Shader.PIPE.disable();
		Pipe.getMesh().unbind();
		Pipe.getTexture().unbind();	
		
	}
	
	// since render's cycle is way faster than update. for this game, it is not really necessary to render cycle this fast. 
	// there must be split seconds where shaders rendering meshes at the same coordination for hundreds of times but it is less than milliseconds job
	// so eyes can only perceive background flowing smoothly.
	public void render() {
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
			Shader.BG.setUniform4f("ml_matrix", Matrix4f.translate(new Vector3f(i * 10 + xScroll*0.03f, 0.0f, 0.0f)));
			background.draw();
		}

		Shader.PIPE.setUniform4f("ml_matrix", Matrix4f.translate(new Vector3f( xScroll*0.03f, 0.0f, 0.0f)));
		for(int i=0 ; i <5*2; i++)
			renderPipe(i);
		
		Shader.BG.disable();
		bgTexture.unbind();
		bird.render();
		
		
	}
}
