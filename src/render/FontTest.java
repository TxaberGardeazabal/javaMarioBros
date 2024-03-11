/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package render;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

/**
 *
 * @author txaber gardeazabal
 */
public class FontTest {
    private String filepath;
    private int fontSize;
    private int width, height, lineHeight;
    private Map<Integer, CharInfo> characterMap;
    
    public int textureId;

    public FontTest(String filepath, int fontSize) {
        this.filepath = filepath;
        this.fontSize = fontSize;
        this.characterMap = new HashMap<>();
        generateBitmap();
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
        g2d.setColor(Color.white);
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
        uploadTexture(img);
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
        
        int[] pixels = new int[image.getHeight()- image.getWidth()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getHeight(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                
            }
        }
        
        
    }
    
    public CharInfo getCharacter(int codePoint) {
        return characterMap.getOrDefault(codePoint, new CharInfo(0,0,0,0));
    }
}
