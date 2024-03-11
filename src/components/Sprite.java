/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import org.joml.Vector2f;
import render.Texture;

/**
 *
 * @author txaber
 */
public class Sprite {
    
    private float Width,height;
    private Texture texture = null;
    private Vector2f[] texCoords = new Vector2f[] {
          new Vector2f(1,1),
          new Vector2f(1,0),  
          new Vector2f(0,0),  
          new Vector2f(0,1)
        };
    
    public Sprite(){}
    
    public Sprite(Texture texture) {
        this.texture = texture;
        this.height = texture.getHeight();
        this.Width = texture.getWidth();
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }
    
    public float getWidth() {
        return Width;
    }

    public void setWidth(float Width) {
        this.Width = Width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
    
    public int getTexId() {
        return texture == null ? -1 : texture.getTexID();
    }
}
