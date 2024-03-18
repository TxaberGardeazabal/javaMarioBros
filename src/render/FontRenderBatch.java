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

/**
 *
 * @author txaber gardeazabal
 */
public class FontRenderBatch implements Comparable<RenderBatch>{
    // Vertex
    // ======
    //
    // Pos             Color                      tex coord       tex id    entity
    // float float     float float float float    float float     float     float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;
    
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
    
    private FontRenderer[] numTexts;
    private int size;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};
    
    private ArrayList<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    
    private int zIndex;
    private Renderer renderer;

    public FontRenderBatch(int maxBatchSize, int zIndex, Renderer renderer) {
        this.numTexts = new FontRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;
        
        // 4 vertices quads
        this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        
        this.size = 0;
        this.hasRoom = true;
        this.textures = new ArrayList();
        this.zIndex = zIndex;
        this.renderer = renderer;
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
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        // enable the buffer atribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }
    
    /*public void addSprite(SpriteRenderer spr) {
        // get index and add renderObject
        // next index = lenght - 1 + 1
        this.sprites[this.numSprites] = spr;
        
        if (spr.getTexture() != null) {
            if (!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }
        
        // add properties to local vertices array
        loadVertexProperties(this.numSprites);
        
        this.numSprites++;
        if (this.numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }
    */
    public void addText(FontRenderer fr) {
        String text = fr.getText();
        float x = fr.getTextPos().x;
        float y = fr.getTextPos().y;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            CharInfo charInfo = fr.getFont().getCharacter(c);
            if (charInfo.width == 0) {
                System.out.println("WARNING: unknown character" + c);
                continue;
            }
            
            addCharacterProperties(x, y, fr, charInfo);
            x += charInfo.width * fr.getSize();
        }
    }
    
    public void addCharacterProperties(float x, float y, FontRenderer fr, CharInfo charInfo) {
        float scale = fr.getSize();
        
        
        // if we have no more room in the current batch, flush it and start with a fresh batch.
        if (vertices.length >= maxBatchSize - 1) {
            flushBatch();
        }
        
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
        vertices[index +2] = fr.getColor().x;
        vertices[index +3] = fr.getColor().y;
        vertices[index +4] = fr.getColor().z;
        vertices[index +5] = fr.getColor().w;

        // load texture coordinates
        vertices[index +6] = ux1;
        vertices[index +7] = uy0;

        // load texture id
        vertices[index +8] = fr.getFont().textureId;

        // load entity id
        vertices[index +9] = fr.gameObject.getUid() +1;
        
        // second vertice
        index += VERTEX_SIZE;
        // load position
        vertices[index] = x1;
        vertices[index +1] = y1;

        // load color
        vertices[index +2] = fr.getColor().x;
        vertices[index +3] = fr.getColor().y;
        vertices[index +4] = fr.getColor().z;
        vertices[index +5] = fr.getColor().w;

        // load texture coordinates
        vertices[index +6] = ux1;
        vertices[index +7] = uy1;

        // load texture id
        vertices[index +8] = fr.getFont().textureId;

        // load entity id
        vertices[index +9] = fr.gameObject.getUid() +1;
        
        // third vertice
        index += VERTEX_SIZE;
        // load position
        vertices[index] = x0;
        vertices[index +1] = y1;

        // load color
        vertices[index +2] = fr.getColor().x;
        vertices[index +3] = fr.getColor().y;
        vertices[index +4] = fr.getColor().z;
        vertices[index +5] = fr.getColor().w;

        // load texture coordinates
        vertices[index +6] = ux0;
        vertices[index +7] = uy1;

        // load texture id
        vertices[index +8] = fr.getFont().textureId;

        // load entity id
        vertices[index +9] = fr.gameObject.getUid() +1;
        
        // forth vertice
        index += VERTEX_SIZE;
        // load position
        vertices[index] = x0;
        vertices[index +1] = y0;

        // load color
        vertices[index +2] = fr.getColor().x;
        vertices[index +3] = fr.getColor().y;
        vertices[index +4] = fr.getColor().z;
        vertices[index +5] = fr.getColor().w;

        // load texture coordinates
        vertices[index +6] = ux0;
        vertices[index +7] = uy0;

        // load texture id
        vertices[index +8] = fr.getFont().textureId;

        // load entity id
        vertices[index +9] = fr.gameObject.getUid() +1;
        
        size += 4;
    }
    
    public void flushBatch() {
        // clear buffer on the GPU, upload the CPU contents, and the draw
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * maxBatchSize, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        
        // draw the buffer that we just uploaded
        Shader shader = Renderer.getBoundShader();
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_BUFFER, numTexts[0].getFont().textureId);
        shader.uploadIntArray("uTextures", texSlots);
        
        glBindVertexArray(vaoID);
        
        glDrawElements(GL_TRIANGLES, this.size * 6, GL_UNSIGNED_INT, 0);
        
        // reset the batch for use on next draw
        size = 0;
    }
    
    /*public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer spr = sprites[i];
            if (spr.isDirty()) {
                if (!hasTexture(spr.getTexture())) {
                    this.renderer.destroyGameObject(spr.gameObject);
                    this.renderer.add(spr.gameObject);
                } else {
                    loadVertexProperties(i);
                    spr.setClean();
                    rebufferData = true;
                }
            }
            
            // TODO: video 40
            if (spr.gameObject.transform.zIndex != this.zIndex) {
                destroyIfExists(spr.gameObject);
                renderer.add(spr.gameObject);
                i--;
            }
        }
        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }
        
        // use shader
        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);
        
        // bind the VAO
        glBindVertexArray(vaoID);
        
        // enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
        
        // unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
        shader.detach();
    }*/

    private int[] generateIndices() {
        // 6 indices per quad (3 * 2)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }
        
        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;
        
        // triangle 1
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex +1] = offset + 2;
        elements[offsetArrayIndex +2] = offset + 0;
        
        // triangle 2
        elements[offsetArrayIndex +3] = offset + 0;
        elements[offsetArrayIndex +4] = offset + 2;
        elements[offsetArrayIndex +5] = offset + 1;
    }
    
    /*public boolean destroyIfExists(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        for (int i = 0; i < numSprites; i++) {
            if (sprites[i] == spr) {
                // overwrite the object by moving array items forward
                for (int j = i; j < numSprites -1; j++) {
                    sprites[j] = sprites[j + 1];
                    sprites[j].setDirty();
                }
                numSprites --;
                return true;
            }
        }
        return false;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];
        
        // find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;
        
        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();
        
        int texId = 0;
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size() && texId == 0; i++) {
                if (textures.get(i).equals(sprite.getTexture())) {
                    texId = i + 1;
                }
            }
        }
        
        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(sprite.gameObject.transform.position.x,
                    sprite.gameObject.transform.position.y, 0f);
            transformMatrix.rotate((float)Math.toRadians(sprite.gameObject.transform.rotation),
                    0, 0, 1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x,
                    sprite.gameObject.transform.scale.y, 1);
        }
        
        // add vertice with the appropiate properties
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 1:
                    yAdd = -0.5f;
                    break;
                case 2:
                    xAdd = -0.5f;
                    break;
                case 3:
                    yAdd = 0.5f;
                    break;
                default:
                    break;
            }
            
            Vector4f currentPos = new Vector4f(sprite.gameObject.transform.position.x + 
                    (xAdd * sprite.gameObject.transform.scale.x),
                    sprite.gameObject.transform.position.y + 
                    (yAdd * sprite.gameObject.transform.scale.y),
                    0, 1);
            if (isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }
            
            // load position
            vertices[offset] = currentPos.x;
            vertices[offset +1] = currentPos.y;
            
            // load color
            vertices[offset +2] = color.x;
            vertices[offset +3] = color.y;
            vertices[offset +4] = color.z;
            vertices[offset +5] = color.w;
            
            // load texture coordinates
            vertices[offset +6] = texCoords[i].x;
            vertices[offset +7] = texCoords[i].y;
            
            // load texture id
            vertices[offset +8] = texId;
            
            // load entity id
            vertices[offset +9] = sprite.gameObject.getUid() +1;
            
            offset += VERTEX_SIZE;
        }
        
        
    }*/

    public boolean hasRoom() {
        return hasRoom;
    }
    
    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }
    
    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    public int getzIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(RenderBatch t) {
        return Integer.compare(zIndex, t.getzIndex());
    }
}
