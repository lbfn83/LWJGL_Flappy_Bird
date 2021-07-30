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
		
	}
	
	public void update() {
		//the degree of movement the meshes makes in the leftward of the screen

		xScroll--;
		
		// 335 and xScroll * 0.03 => 
		// the width of display is 10 so translation matrix vector starts with i*10
		// every multiple of 335 will increase the count of map 
		// it is because xScroll won't be reset but still counting down while game is running
		
		if (-xScroll%335 == 0)
		{
			map++;
		}
	}
	
	public void render() {
		bgTexture.bind();
		Shader.BG.enable();
		background.bind();
		
		// 3 is used to put together three copies of images
		// The Model matrix 
		// http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
		// View matrix is not the right terminology
		// Coordination rule :  
		for(int i = map; i < map + 3 ; i++ )
		{
			Shader.BG.setUniform4f("vw_matrix", Matrix4f.translate(new Vector3f(i * 10 + xScroll*0.03f, 0.0f, 0.0f)));
			background.draw();
		}
		Shader.BG.disable();
		bgTexture.unbind();
			
		
	}
}
