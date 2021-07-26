package com.wonzii.flappy.math;

import static java.lang.Math.*;

import java.nio.FloatBuffer;

import com.wonzii.flappy.utils.BufferUtils;



public class Matrix4f {
	public static final int SIZE = 4 * 4; 
	public float[] elements = new float[SIZE];

	public Matrix4f()
	{
		
	}
	//identity matrix 단위행렬
	// static : because it returns new matrix like constructor
	public static Matrix4f identity()
	{
		Matrix4f result = new Matrix4f();
		
		for ( int i = 0; i < SIZE ; i++ )
		{
			result.elements[i] = 0.0f ;
		}
		
		result.elements[0 + 0*4] = 1.0f;
		result.elements[1 + 1*4] = 1.0f;
		result.elements[2 + 2*4] = 1.0f;
		result.elements[3 + 3*4] = 1.0f;
		
		return result;
	}
	
	public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {
		Matrix4f result = identity();
		
		// column major order 
		result.elements[ 0 + 0 *4 ] = 2.0f / (right - left);
		result.elements[ 1 + 1 *4 ] = 2.0f / (top - bottom);
		result.elements[ 2 + 2 *4 ] = 2.0f / (near - far);
		
		result.elements[ 0 + 3 *4 ] = (left + right) / (left - right);
		result.elements[ 1 + 3 *4 ] = (bottom + top) / (bottom - top);
		result.elements[ 2 + 3 *4 ] = (near + far) / ( far - near );
		
		return result;
	}
	
	
	// Opengl using column major ordering 
	// but when it comes to multiplication it doesn't really change much
	public Matrix4f multiply(Matrix4f matrix) {
		Matrix4f result = new Matrix4f();
		
		for ( int x = 0; x < 4 ; x++) {
			for( int y = 0; y < 4; y++) {
				float sum = 0.0f;
				
				for(int e = 0; e < 4; e++)
				{
					sum += this.elements[e + x * 4] * matrix.elements[e * 4 + y];
				}
				result.elements[4 * x + y] = sum;	
			}
		}
		return result;
	}
	//move the object 
	public static Matrix4f translate(Vector3f vector)
	{
		Matrix4f result = identity();
		
		result.elements[0 + 3 * 4] = vector.x;
		result.elements[1 + 3 * 4] = vector.y;
		result.elements[2 + 3 * 4] = vector.z;
	
				
		return result;
	}
	// Rotation around the Z-axis / https://learnopengl.com/Getting-started/Transformations
   	
	public static Matrix4f rotate(float angle)
	{
		Matrix4f result = identity();
		//cos and sin in Math lib only accepts radian not degree
		float r = (float) toRadians(angle);
		float cos = (float) cos(r);
		float sin = (float) sin(r);
		
		result.elements[0 + 0 * 4] = cos; 
		result.elements[1 + 0 * 4] = sin;
		
		result.elements[0 + 1 * 4] = -sin;
		result.elements[1 + 1 * 4] = cos;
		
		return result;
	}
	
	public FloatBuffer toFloatBuffer()
	{
		return BufferUtils.createFloatBuffer(elements);
	}
//		float tmp1, tmp2, tmp3, tmp4;
//		for(int i = 0; i < SIZE/4 ;i++)
//		{
//			for(int j = 0 ; j < SIZE/4 ; j++)
//			{
//				tmp1 = a.elements[0+i*4] * b.elements[0*4 + j];
//				tmp2 = a.elements[1+i*4] * b.elements[1*4 + j];
//				tmp3 = a.elements[2+i*4] * b.elements[2*4 + j];
//				tmp4 = a.elements[3+i*4] * b.elements[3*4 + j];
//				
//				result.elements[4*i + j] = tmp1 + tmp2 + tmp3 + tmp4;
//			}
			
//		}
	
	
	
			

}

