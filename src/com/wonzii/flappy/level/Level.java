package com.wonzii.flappy.level;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.VertexArray;

public class Level {

	private VertexArray background;
	
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
		
	}
	
	public void render() {
		Shader.BG.enable();
		background.render();
		Shader.BG.disable();
	}
}
