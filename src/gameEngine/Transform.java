/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import components.Component;
import editor.OImGui;
import imgui.internal.ImGui;
import org.joml.Vector2f;

/**
 * A transform holds information of the position and scale of an object inside the world
 * @author txaber
 */
public class Transform extends Component{
    
    public Vector2f position;
    public float rotation = 0.0f;
    public Vector2f scale;
    public int zIndex = 0;

    public Transform() {
        this.position = new Vector2f();
        this.scale = new Vector2f();
    }

    public Transform(Vector2f position) {
        this.position = position;
        //this.lastPosition = position;
        this.scale = new Vector2f();
    }
    
    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        //this.lastPosition = position;
        this.scale = scale;
        //this.lastScale = scale;
    }
    
    public void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }
    
    @Override
    public void imGui() {
        Vector2f pos = OImGui.drawVec2Control("Position",new Vector2f(this.position));
        float rot = OImGui.dragFloat("Rotation", this.rotation);
        Vector2f sca = OImGui.drawVec2Control("Scale",new Vector2f(this.scale), 32.0f);
        int zInt = OImGui.dragInt("Z-index", this.zIndex);
        
        if (!this.position.equals(pos)) {
            Vector2f tmp = new Vector2f(this.position).sub(pos);
            this.translate(-tmp.x, -tmp.y);
        }
        if (rot != this.rotation) {
            this.rotate(this.rotation - rot);
        }
        if (!this.scale.equals(sca)) {
            Vector2f tmp = new Vector2f(this.scale).sub(sca);
            this.scale(-tmp.x, -tmp.y);
        }
        if (zInt != this.zIndex) {
            this.setzIndex(zInt);
        }
        
    }
    
    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));       
    }
    
    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (!(o instanceof Transform)) {
            return false;
        }
        Transform t = (Transform)o;
        return t.position.equals(this.position) && t.scale.equals(this.scale) &&
                t.rotation == this.rotation && t.zIndex == this.zIndex;
    }
    
    public void translate(float x,float y) {
        Vector2f translate = new Vector2f(x,y);
        position.add(translate);
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.translate(translate);
        }
    }
    
    public void translate(Vector2f translate) {
        position.add(translate);
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.translate(translate);
        }
    }
    
    public void rotate(float z) {
        rotation += z;
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.rotate(z);
        }
    }
    
    public void scale(float x,float y) {
        Vector2f scalew = new Vector2f(x,y);
        this.scale.add(scalew);
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.scale(x, y);
        }
    }
    
    public void scale(Vector2f scalew) {
        this.scale.add(scalew);
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.scale(scalew);
        }
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.setzIndex(zIndex);
        }
    }
}
