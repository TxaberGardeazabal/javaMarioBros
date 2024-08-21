/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import render.FontTest;



/**
 * Componente para mostrar texto en pantalla partiendo de fuentes de texto
 * @deprecated 
 * @author txaber
 */
public class FontRenderer extends Component {

    private transient FontTest font;
    private String text = "a";
    private int size = 1;
    private Vector4f color = new Vector4f(1f,1f,1f,1f);

    public FontRenderer() {
        this.font = new FontTest("assets/fonts/super-mario-bros-nes.ttf", 1);
    }
    
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
        return new Vector2f(gameObject.transform.position.x,gameObject.transform.position.y);
    }
}
