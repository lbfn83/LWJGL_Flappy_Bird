package com.wonzii.flappy.level;

import static org.lwjgl.glfw.GLFW.*;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.Texture;
import com.wonzii.flappy.graphics.VertexArray;
import com.wonzii.flappy.input.Input;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;

public class Bird {
	private VertexArray mesh;
	private Texture texture;
	private final float size = 1.0f;
	
	private Vector3f position = new Vector3f();
	private float rot;
	private float delta = 0.0f;
	
	
	public Bird() {
		
	float[] vertices = new float[] {
			-size/2.0f, -size/2.0f, 0.1f,
			-size/2.0f, size/2.0f , 0.1f,
			size/2.0f, size/2.0f , 0.1f,
			size/2.0f, -size/2.0f, 0.1f,
			
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
		texture = new Texture("res/bird.png");
		
	}
	public void update() {
//		System.out.println(Input.keys[GLFW.GLFW_KEY_UP]);
//		if(Input.isKeyDown(GLFW_KEY_UP))
//		{
//			position.y += 0.1f;
//		}
//		if(Input.isKeyDown(GLFW_KEY_DOWN))
//		{
//			position.y -= 0.1f;
//		}
//		if(Input.isKeyDown(GLFW_KEY_LEFT))
//		{
//			position.x -= 0.1f;    
//		}
//		if(Input.isKeyDown(GLFW_KEY_RIGHT))
//		{
//			position.x += 0.1f;			;
//		}
		System.out.println("delta : " + delta);
		position.y -= delta;
		
		if( Input.isKeyDown(GLFW_KEY_SPACE))
		{
			delta -=
					0.015f ;
		}else
		{
			delta +=0.001f;                         
		}
	}
	private void fall()
	{
		delta = -0.15f;
	}
	
	public void render() {

		Shader.BIRD.enable();
		Shader.BIRD.setUniform4f("ml_matrix", Matrix4f.translate(position));
		texture.bind();
		mesh.render();

		Shader.BIRD.disable();
		mesh.unbind();
		texture.unbind();	
		
	}
}
