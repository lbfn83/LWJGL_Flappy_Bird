package com.wonzii.flappy.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
// Helper class : not instaiated
public class BufferUtils {
	private BufferUtils() {
		
	}
	
	public static ByteBuffer createByteBuffer(byte[] array)
	{
		ByteBuffer result = ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder());
		//flip: get ready for read . position go to 0
		result.put(array).flip();
		return result;
	}
	
	public static FloatBuffer createFloatBuffer(float[] array)
	{
		//float is 4 bytes
		FloatBuffer result = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
		//flip: get ready for read . position go to 0
		result.put(array).flip();
		return result;
	}
	
	public static IntBuffer createIntBuffer(int[] array)
	{
		//float is 4 bytes 
		IntBuffer result = ByteBuffer.allocateDirect(array.length << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
		//flip: get ready for read . position go to 0
		result.put(array).flip();
		return result;
	}
}
