/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package render;

import components.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;
import util.AssetPool;

/**
 * Renderiza texto en la pantalla, no ha funcionado.
 * @deprecated 
 * @author txaber gardeazabal
 */
public class FontTest extends Component{
    private String filepath;
    private int fontSize;
    private int width, height, lineHeight;
    private Map<Integer, CharInfo> characterMap;
    
    private float[] vertices = {
            // x, y,        r, g, b              ux, uy
            0.5f, 0.5f,     1.0f, 0.2f, 0.11f,   1.0f, 0.0f,
            0.5f, -0.5f,    1.0f, 0.2f, 0.11f,   1.0f, 1.0f,
            -0.5f, -0.5f,   1.0f, 0.2f, 0.11f,   0.0f, 1.0f,
            -0.5f, 0.5f,    1.0f, 0.2f, 0.11f,   0.0f, 0.0f
    };

    private int[] indices = {
            0, 1, 3,
            1, 2, 3
    };
    int vao;
    
    public int textureId;
    
    private FontRenderBatch batch = new FontRenderBatch();

    public FontTest(String filepath, int fontSize) {
        this.filepath = filepath;
        this.fontSize = fontSize;
        this.characterMap = new HashMap<>();
        generateBitmap();
    }
    
    @Override
    public void start() {
        Vector2f[] texCoords = getCharacter('k').textureCoordinates;
        vertices[5] = texCoords[1].x; vertices[6] = texCoords[1].y;
        vertices[12] = texCoords[1].x; vertices[13] = texCoords[0].y;
        vertices[19] = texCoords[0].x; vertices[20] = texCoords[0].y;
        //uploadSquare();
        
        //System.out.println("start");
        //batch.start();
    }
    
    
    @Override
    public void editorUpdate(float dt) {
        // font render test
        //System.out.println("update");
        //batch.addText("A", 3, 0, 0.01f, 0xFF00AB, this);
        //batch.showTextTexture(0, 0, 0.01f, 0xFF00AB, this);
        //batch.flushBatch();
    }
    @Override
    public void update(float dt) {
        // font render test
        //batch.addText("hola mundo!", 200, 200, 1f, 0xFF00AB, this);
        //batch.flushBatch();
        //batch.detachShader();
    }
    
    public void generateBitmap() {
        BufferedImage img = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        
        // create fake image to calculate font information
        Graphics2D g2d = img.createGraphics();
        Font font = registerFont(filepath);
        font = new Font(font.getName(),Font.PLAIN, fontSize);
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        
        int estimatedWidth = (int) Math.sqrt(font.getNumGlyphs()) * font.getSize()+1;
        width = 0;
        height = fontMetrics.getHeight();
        lineHeight = fontMetrics.getHeight();
        int x = 0;
        int y = (int)(fontMetrics.getHeight() * 1.4f);
        
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                // get the sizes for each codepoint glyph,
                // and update the actual image widht and height
                CharInfo charInfo = new CharInfo(x, y, fontMetrics.getHeight(), fontMetrics.charWidth(i));
                characterMap.put(i, charInfo);
                width = Math.max(x + fontMetrics.charWidth(i), width);
                x += charInfo.width;
                if (x > estimatedWidth) {
                    x = 0;
                    y += fontMetrics.getHeight() * 1.4f;
                    height += fontMetrics.getHeight() * 1.4f;
                }
                
            }
        }
        height += fontMetrics.getHeight() * 1.4f;
        g2d.dispose();
        
        // create the real texture
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                CharInfo info = characterMap.get(i);
                info.calculateTextCoordinates(width,height);
                g2d.drawString("" + (char)i,info.sourceX, info.sourceY);
            }
        }
        g2d.dispose();
        
        /*try {
            File file = new File("tmp.png");
            ImageIO.write(img, "png", file);
        } catch (IOException ex) {
            Logger.getLogger(FontTest.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        //uploadTexture(img);
        
        Texture fontTexture = new Texture();
        this.textureId = fontTexture.init(img);
    }
    
    public Font registerFont(String fontFile) {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontFile));
            ge.registerFont(font);
            return font;
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadTexture(BufferedImage image) {
        
        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                byte alphaComponnet = (byte)((pixel >> 24) & 0xFF);
                buffer.put(alphaComponnet);
                buffer.put(alphaComponnet);
                buffer.put(alphaComponnet);
                buffer.put(alphaComponnet);
            }
        }
        buffer.flip();
        
        textureId = glGenTextures();
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        //stbi_set_flip_vertically_on_load(true);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        buffer.clear();
    }
    
    public CharInfo getCharacter(int codePoint) {
        return characterMap.getOrDefault(codePoint, new CharInfo(0,0,0,0));
    }
    
    public void render() {
        Shader fontShader = AssetPool.getShader("assets/shaders/fontShader.glsl");
        fontShader.use();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_BUFFER, textureId);
        fontShader.uploadTexture("uFontTexture", 0);
        
        glBindVertexArray(vao);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        fontShader.detach();
    }
    
    private void uploadSquare() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        int stride = 7 * Float.BYTES;
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 5 * Float.BYTES);
        glEnableVertexAttribArray(2);
    }
    
}
