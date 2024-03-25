/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package render;

import components.FontRenderer;
import components.SpriteRenderer;
import gameEngine.GameObject;
import gameEngine.Window;
import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import util.AssetPool;

/**
 *
 * @author txaber gardeazabal
 */
public class FontRenderBatch{
    
    /*private float[] vertices = {
            // x, y,        r, g, b              ux, uy
            0.5f, 0.5f,     1.0f, 0.2f, 0.11f,   1.0f, 0.0f,
            0.5f, -0.5f,    1.0f, 0.2f, 0.11f,   1.0f, 1.0f,
            -0.5f, -0.5f,   1.0f, 0.2f, 0.11f,   0.0f, 1.0f,
            -0.5f, 0.5f,    1.0f, 0.2f, 0.11f,   0.0f, 0.0f
    };*/

    private int[] indices = {
            0, 1, 3,
            1, 2, 3
    };

    private int BATCH_SIZE = 100;
    
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 3;
    private final int TEX_COORDS_SIZE = 2;
    
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 7;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    
    private float[] vertices;
    private int size = 0;
    
    private int vaoID, vboID;
    private Shader shader;
    private int textureId;

    public FontRenderBatch() {
        this.vertices = new float[BATCH_SIZE * VERTEX_SIZE];
        shader = AssetPool.getShader("assets/shaders/fontShader.glsl");
    }
    
    public void start() {
        // generate and bind VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        
        // allocate space on the GPU for the vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
        
        // create and upload indices buffer
        int eboID = glGenBuffers();
        //int[] indices = generateIndices();
        int [] elementBuffer = new int[BATCH_SIZE * 3];
        for (int i = 0; i < elementBuffer.length; i++) {
            elementBuffer[i] = indices[(i % 6)] + ((i / 6) * 4);
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
        
        // enable the buffer atribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);
        /*glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);*/
    }
    
    public void addText(String text, int x, int y, float scale, int rgb, FontTest font) {
        System.out.println("adding text " + text);

        this.textureId = font.textureId;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            CharInfo charInfo = font.getCharacter(c);
            if (charInfo.width == 0) {
                System.out.println("WARNING: unknown character" + c);
                continue;
            }
            
            addCharacterProperties(x, y, scale, charInfo, rgb);
            x += charInfo.width * scale;
        }
    }
    
    public void addCharacterProperties(float x, float y, float scale, CharInfo charInfo, int rgb) {
        
        // if we have no more room in the current batch, flush it and start with a fresh batch.
        if (size >= BATCH_SIZE - 4) {
            System.out.println("batch full, flushing batch");
            
            flushBatch();
        }
        
        float r = (float)((rgb >> 16) & 0xFF) / 255.0f;
        float g = (float)((rgb >> 8) & 0xFF) / 255.0f;
        float b = (float)((rgb >> 0) & 0xFF) / 255.0f;
        
        float x0 = x;
        float y0 = y;
        float x1 = x + scale * charInfo.width;
        float y1 = y + scale * charInfo.height;
        
        float ux0 = charInfo.textureCoordinates[0].x;
        float uy0 = charInfo.textureCoordinates[0].y;
        float ux1 = charInfo.textureCoordinates[1].x;
        float uy1 = charInfo.textureCoordinates[1].y;
        
        int index = size * VERTEX_SIZE;
        // TODO: refractor into a for loop
        // load position
        vertices[index] = x1;
        vertices[index +1] = y0;

        // load color
        vertices[index +2] = r;
        vertices[index +3] = g;
        vertices[index +4] = b;

        // load texture coordinates
        vertices[index +5] = ux1;
        vertices[index +6] = uy0;
        
        // second vertice
        index += VERTEX_SIZE;
        // load position
        vertices[index] = x1;
        vertices[index +1] = y1;

        // load color
        vertices[index +2] = r;
        vertices[index +3] = g;
        vertices[index +4] = b;

        // load texture coordinates
        vertices[index +6] = ux1;
        vertices[index +7] = uy1;
        
        // third vertice
        index += VERTEX_SIZE;
        // load position
        vertices[index] = x0;
        vertices[index +1] = y1;

        // load color
        vertices[index +2] = r;
        vertices[index +3] = g;
        vertices[index +4] = b;

        // load texture coordinates
        vertices[index +6] = ux0;
        vertices[index +7] = uy1;
        
        // forth vertice
        index += VERTEX_SIZE;
        // load position
        vertices[index] = x0;
        vertices[index +1] = y0;

        // load color
        vertices[index +2] = r;
        vertices[index +3] = g;
        vertices[index +4] = b;

        // load texture coordinates
        vertices[index +6] = ux0;
        vertices[index +7] = uy0;
        
        size += 4;
    }
    
    public void flushBatch() {
        System.out.println("batch contents = " + Arrays.toString(vertices));
        // clear buffer on the GPU, upload the CPU contents, and the draw
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * vertices.length, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        
        // draw the buffer that we just uploaded
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_BUFFER, textureId);
        shader.uploadTexture("uFontTexture", 0);
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.detach();
        
        glBindVertexArray(vaoID);
        
        glDrawElements(GL_TRIANGLES, this.size * 6, GL_UNSIGNED_INT, 0);
        
        // reset the batch for use on next draw
        size = 0;
    }
    
    
}
