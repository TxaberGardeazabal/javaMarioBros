/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2f;
import org.joml.Vector4f;
import render.FontTest;



/**
 *
 * @author txaber
 */
public class FontRenderer extends Component {

    public transient FontTest font;
    public String text = "a";
    public int size = 1;
    public Vector4f color = new Vector4f(1f,1f,1f,1f);
    
    @Override
    public void start() {
        
    }    
    
    @Override
    public void update(float dt) {
        
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public FontTest getFont() {
        return font;
    }

    public void setFont(FontTest font) {
        this.font = font;
    }
    
    public Vector2f getTextPos() {
        return new Vector2f(gameObject.transform.position.x,gameObject.transform.position.x);
    }
}
