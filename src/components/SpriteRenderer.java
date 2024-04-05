/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;


import editor.OImGui;
import gameEngine.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import render.Texture;

/**
 *
 * @author txaber
 */
public class SpriteRenderer extends Component{
    
    private Vector4f color = new Vector4f(1,1,1,1);
    private Sprite sprite = new Sprite();
    private transient Transform lastTransform;
    // dirty flag
    private transient boolean isDirty = true;

    public SpriteRenderer() {
    }

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.sprite = new Sprite(null);
    }
    
    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
    }
    
    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }
    
    @Override
    public void update(float dt) {
        if(!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }
    
    @Override
    public void editorUpdate(float dt) {
        if(!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color = color;
            isDirty = true;
        }
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }
    
    public void setClean() {
        this.isDirty = false;
    }
    
    @Override
    public void imGui() {
        if (OImGui.colorPicker4("Color Picker", this.color)) {
            this.isDirty = true;
        }
    }
    
    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
    }
    
    public void setDirty() {
        this.isDirty = true;
    }
}
