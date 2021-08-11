package com.wonzii.flappy.level;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.Texture;
import com.wonzii.flappy.graphics.VertexArray;
import com.wonzii.flappy.input.Input;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;

public class Bird {
	
	private ArrayList<Vector3f> InitBirdCache = new ArrayList<Vector3f>();
	
	
	private VertexArray mesh;
	private Texture texture;
	private final float size = 1.0f;
	
	private Vector3f position = new Vector3f();
	private float rot;
	private float delta = 0.0f;
	
	/*Init screen*/
	private Random r;
	/*possible y coordination of the upper pipe*/
	private final float randomMax = 8.0f;
	private final float randomMin = -8.0f;
	
	public Bird() {
		
		float[] vertices = new float[] {
				-size/2.0f, -size/2.0f, 0.2f,
				-size/2.0f, size/2.0f , 0.2f,
				size/2.0f, size/2.0f , 0.2f,
				size/2.0f, -size/2.0f, 0.2f,
				
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
		r = new Random();
		
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
//		System.out.println("delta : " + delta);
		position.y -= delta;
		
		if( Input.isKeyDown(GLFW_KEY_SPACE))
		{
			delta =	-0.15f ;
		}else
		{
			delta += 0.01f;                         
		}
		// when delta is minus, bird's face up
		// when delta is plus, bird's face down
		rot = -delta * 90.0f;

	}

	public void render() {

		Shader.BIRD.enable();
//		System.out.println("bird's position : "+ position.x );
		Shader.BIRD.setUniform4fv("vw_matrix", Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
		texture.bind();
		mesh.render();

		Shader.BIRD.disable();
		mesh.unbind();
		texture.unbind();	
		
	}
	
	
	private float randomNumGen()
	{
		return randomMin + r.nextFloat() * (randomMax - randomMin);
	}
	
	public void renderBirdInit(boolean addBird) {


		if(addBird)
		{
			float xcoord = randomNumGen();
			float ycoord = randomNumGen();
			position.x = xcoord;
			position.y = ycoord;
			
			
			if( InitBirdCache.size() > 10)
			{
				InitBirdCache.remove(0);
			}
			InitBirdCache.add(new Vector3f(xcoord, ycoord, 0f));
		}
		
		Shader.BIRD.enable();
//		System.out.println("bird's position : "+ position.x );
		texture.bind();
		mesh.bind();
		
		for(int i = 0 ; i < InitBirdCache.size(); i++)
		{
			Shader.BIRD.setUniform4fv("vw_matrix", Matrix4f.translate(InitBirdCache.get(i)));
			mesh.draw();
		}
		

		Shader.BIRD.disable();
		mesh.unbind();
		texture.unbind();	
		
	}
	
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public float getY() {
		// TODO Auto-generated method stub
		return position.y;
	}
	public float getX() {
		// TODO Auto-generated method stub
		return position.x;
	}
	public float getSize() {
		// TODO Auto-generated method stub
		return size;
	}
}
