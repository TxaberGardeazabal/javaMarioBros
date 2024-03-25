/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package render;

import org.joml.Vector2f;

/**
 *
 * @author txaber gardeazabal
 */
public class CharInfo {
    public int sourceX;
    public int sourceY;
    public int height;
    public int width;
    
    public Vector2f[] textureCoordinates = new Vector2f[4];

    public CharInfo(int sourceX, int sourceY, int height, int width) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.height = height;
        this.width = width;
    }

    public void calculateTextCoordinates(int fontWidth, int fontHeight) {
        float x0 = (float)sourceX / (float)fontWidth;
        float x1 = (float)(sourceX + width) / (float)fontWidth;
        float y0 = (float)(sourceY - height) / (float)fontHeight;
        float y1 = (float)sourceY / (float)fontHeight;
        
        textureCoordinates[0] = new Vector2f(x0, y1);
        textureCoordinates[1] = new Vector2f(x1, y0);
    }
    
    
}
