package com.wonzii.flappy.level;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.wonzii.flappy.graphics.Shader;
import com.wonzii.flappy.graphics.Texture;
import com.wonzii.flappy.graphics.VertexArray;
import com.wonzii.flappy.math.Matrix4f;
import com.wonzii.flappy.math.Vector3f;
import com.wonzii.flappy.math.Vector4f;
import com.wonzii.flappy.utils.GenericUtil;

public class TextItem {
    
	//여기선 static으로 잡으면 안되겠지
	// pipe는 여러개가 개체가 있더라도 결국 똑같은 개체를 찍어내는거지만
	// TextItem은 문자별로 다른 개체가 있어야 하므로.. Hud에서
	// 각 문자에 맞는 개체들을 만들어서 관리해 주는게 좋다
	private VertexArray mesh;
	
	private Texture texture;
	
	private static final float ZPOS = 0.5f;

    private static final int VERTICES_PER_QUAD = 4;

    private String text;
    
    private final int numCols;
    
    private final int numRows;
    
    private final int numChar;
    
    
    public TextItem(String text, String fontFileName, int numCols, int numRows) throws Exception 
    {
        this.text = text;
        this.numCols = numCols;
        this.numRows = numRows;
        this.numChar = text.length();
        texture = new Texture(fontFileName);
        mesh = buildMesh(texture, numCols, numRows);
    }

    private VertexArray buildMesh(Texture texture, int numCols, int numRows) {
        byte[] chars = text.getBytes(Charset.forName("ISO-8859-1"));
        int numChars = chars.length;

        List<Float> positions = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        List<Integer> indices   = new ArrayList<>();
        
        float tileWidth = (float)texture.getWidth() / (float)numCols;
        float tileHeight = (float)texture.getHeight() / (float)numRows;

        for(int i=0; i<numChars; i++) {
            byte currChar = chars[i];
            int col = currChar % numCols;
            int row = currChar / numCols;
            
            // Build a character tile composed by two triangles
            
            // Left Top vertex
            positions.add((float)i*tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(ZPOS); //z
            textCoords.add((float)col / (float)numCols );
            textCoords.add((float)row / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD);
                        
            // Left Bottom vertex
            positions.add((float)i*tileWidth); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float)col / (float)numCols );
            textCoords.add((float)(row + 1) / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add((float)i*tileWidth + tileWidth); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(col + 1)/ (float)numCols );
            textCoords.add((float)(row + 1) / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add((float)i*tileWidth + tileWidth); // x
            positions.add(tileHeight); //y
            positions.add(ZPOS); //z
            textCoords.add((float)(col + 1)/ (float)numCols );
            textCoords.add((float)row / (float)numRows );
            indices.add(i*VERTICES_PER_QUAD + 3);
            
            // Add indices por left top and bottom right vertices
            indices.add(i*VERTICES_PER_QUAD);
            indices.add(i*VERTICES_PER_QUAD + 2);
        }
   
 //		TODO : 최종 본에서는 지워야 할 부분       
//		Byte Array :         
//        byte[] indicesByteArray = new byte[indices.size()]; 
//       indices.stream().map(Integer::byteValue).forEach((x) -> { x.byteValue();});
//		indices.stream().map(Integer::byteValue).collect(Collectors);
       
        
        /*List type to primitive data types like byte or float type*/
        byte[] indicesByteArray = new byte[indices.size()];
        for(int i = 0 ; i < indices.size(); i++)
        {
        	indicesByteArray[i] = indices.get(i).byteValue();
        }
        
        float[] posArr = GenericUtil.listToArray(positions);
        float[] textCoordsArr = GenericUtil.listToArray(textCoords);
        
        VertexArray mesh = new VertexArray(posArr, indicesByteArray, textCoordsArr);
        return mesh;
    }
    
    public String getText() {
        return text;
    }
    
    public void renderText() {
    	Shader.TEXT.enable();
    	texture.bind();
    	mesh.bind();
    	
		Shader.TEXT.setUniform4f("color", new Vector4f(1.0f, 1.0f, 1.0f, 0f));	
		//TODO texture position should be calculated from the size of texture
		Shader.TEXT.setUniform4fv("vw_matrix", Matrix4f.translate(new Vector3f(-400.0f + (800f - (float)texture.getWidth()/(float)numCols*(float)numChar)/2.0f, 0f, 0f )).multiply(Matrix4f.scale(1f)));
		//multiply(Matrix4f.rotate(10f).multiply(Matrix4f.scale(0.f))
    	mesh.draw();
    	
		Shader.TEXT.disable();
		mesh.unbind();
		texture.unbind();	
    }
    
//    public void setText(String text) {
//        this.text = text;
//        Texture texture = this.getMesh().getMaterial().getTexture();
//        this.getMesh().deleteBuffers();
//        this.setMesh(buildMesh(texture, numCols, numRows));
//    }
}
