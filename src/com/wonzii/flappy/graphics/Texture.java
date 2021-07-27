package com.wonzii.flappy.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.system.MemoryStack;

import com.wonzii.flappy.utils.BufferUtils;

public class Texture {
	private int width, height;
	private int texture;
	
	public Texture(String path)
	{
			texture = load2(path);

	}
	
	private int load(String fileName)  
	{

		ByteBuffer buf;
		
		try(MemoryStack stack = MemoryStack.stackPush();){
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			// 4 
			buf = stbi_load(fileName, w, h, channels, 4);
		
			
			width = w.get();
			height = h.get();
		}
		
		int textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1 );
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		glGenerateMipmap(GL_TEXTURE_2D);
		stbi_image_free(buf);
		glBindTexture(GL_TEXTURE_2D, 0);
		return textureID;
	}
	
	
	private int load2(String path) {
		int[] pixels = null;
		
		BufferedImage image;
		try {
			image = ImageIO.read(new FileInputStream(path));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width*height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		int[] data = new int[width*height];
		for (int i = 0 ; i < width * height; i++)
		{
			int a  = (pixels[i]&0xff000000) >> 24;
			int r  = (pixels[i]&0xff0000)>>16;
			int g  = (pixels[i]&0xff00)>>8;
			int b  = (pixels[i]&0xff);
			// change the order of bytes - argb -> abgr
			data[i] = a<<24 | b<<16 | g << 8 | r;
		}
		
		
		int result = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, result);
		//the resolution should be sharp not blurry( GL_linear )
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		//
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
		glBindTexture(GL_TEXTURE_2D, 0);
		
		return result;
	}
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texture);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	 
};
