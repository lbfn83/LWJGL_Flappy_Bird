package com.wonzii.flappy.graphics;

import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL20.*;

import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;
import com.wonzii.flappy.utils.ShaderUtils;

//the singleton pattern : restricts the instantiation of a class to one "single" instance.
//in this case, restricts it to the number of members ( BG, BIRD, PIPE, FADE )

public class Shader {

	public static final int VERTEX_ATTRIB = 0;
	public static final int TCOORD_ATTRIB = 1;
	
	public static Shader BG, BIRD, PIPE, FADE;
	
	private boolean enabled = false;
	
	private final int ID;
	
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	private Shader(String vertex, String fragment) {
		ID = ShaderUtils.load(vertex, fragment);
	}
	
	public static void loadAll()
	{
		BG = new Shader("shader/bg.vert", "shader/bg.frag");
//		BIRD = new Shader("shader/bird.vert", "shader/bird.frag");
//		PIPE = new Shader("shader/pipe.vert", "shader/pipe.frag");
//		FADE = new Shader("shader/fade.vert", "shader/fade.frag");
	}
	//Uniform 변수의 location을 알려주는 거다. uniform 변수의 값 자체는 get할 이유는 없다.. set 만 있을 뿐
	public int getUniform(String name)
	{
		if(locationCache.containsKey(name))
			return locationCache.get(name);
		
		int result = glGetUniformLocation(ID, name);
		if ( result == -1)
		{
			System.err.println("Could not find uniform variable '" + name + "'!");
			
		}else
		{
			locationCache.put(name, result);
		}
		return result;
	}
	
	public void setUniform1i(String name, int value)
	{
		if(!enabled)
			enable();
		glUniform1i(getUniform(name), value);
	}
	public void setUniform1f(String name, float value)
	{
		if(!enabled)
			enable();
		glUniform1f(getUniform(name), value);
	}
	public void setUniform2f(String name, float x, float y)
	{
		if(!enabled)
			enable();
		glUniform2f(getUniform(name), x, y);
	}
	public void setUniform3f(String name, Vector3f vector)
	{
		if(!enabled)
			enable();
		glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
	}
	public void setUniform4f(String name, Matrix4f matrix)
	{
		if(!enabled)
			enable();
		glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
	}
	
	
	public void enable()
	{
		glUseProgram(ID);
		enabled = true;
	}
	public void disable()
	{
		glUseProgram(0);
		enabled = false;
	}
	
}
