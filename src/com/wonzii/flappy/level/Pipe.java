package com.wonzii.flappy.level;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.Texture;
import com.wonzii.flappy.graphics.VertexArray;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;

public class Pipe {
	
	private static float width = 1.5f, height = 8.0f;
	
	private Vector3f position = new Vector3f();
	private Matrix4f ml_Matrix;
	private static Texture texture;
	private static VertexArray mesh;
	
	
	public static void createPipes()
	{
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
	
	public Pipe(float x, float y)
	{
		position.x = x;
		position.y = y;
		ml_Matrix = Matrix4f.translate(new Vector3f(x,y,0.0f));
	}
	

	public Matrix4f getMl_Matrix() {
		return ml_Matrix;
	}

	public void setMl_Matrix(Matrix4f ml_Matrix) {
		this.ml_Matrix = ml_Matrix;
	}

	public Vector3f getPosition() {
		return position;
	}

	public static Texture getTexture() {
		return texture;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public static VertexArray getMesh() {
		return mesh;
	}

	
}
