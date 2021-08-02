package com.wonzii.flappy.level;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.Texture;
import com.wonzii.flappy.graphics.VertexArray;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;

public class Pipe {
	
	private float width = 5f, height = 20f;
	
	private Vector3f position = new Vector3f();
	private Texture texture;
	private VertexArray mesh;
	
	
//	public static create()
//	{
//		
//	}
	
	public Pipe(float x, float y)
	{
		position.x = x;
		position.y = y;
		
		float[] vertices = new float[] {
				0, 0, 0.1f,
				0, height , 0.1f,
				width, height , 0.1f,
				width, 0, 0.1f,
				
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
		
		mesh = new VertexArray(vertices, indices, tcs);
		texture = new Texture("res/pipe.png");
		
	}
	public void render() {

		Shader.PIPE.enable();
		Shader.PIPE.setUniform4f("ml_matrix", Matrix4f.translate(position));
		texture.bind();
		mesh.render();

		Shader.PIPE.disable();
		mesh.unbind();
		texture.unbind();	
		
	}
	
}
