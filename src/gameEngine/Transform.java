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

    /*private Vector2f lastPosition = new Vector2f();
    private float lastRotation = 0.0f;
    private Vector2f lastScale = new Vector2f();
    private int lastZIndex = 0;
    */
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
    
    /*@Override
    public void update(float dt) {
        if (position != lastPosition || rotation != lastRotation || scale != lastScale || zIndex != lastZIndex) {
            spreadChanges();
        }
        
        this.lastPosition = position;
        this.lastRotation = rotation;
        this.lastScale = scale;
        this.lastZIndex = zIndex;
    }
    
    @Override
    public void editorUpdate(float dt) {
        if (position != lastPosition || rotation != lastRotation || scale != lastScale || zIndex != lastZIndex) {
            spreadChanges();
        }
        
        this.lastPosition = position;
        this.lastRotation = rotation;
        this.lastScale = scale;
        this.lastZIndex = zIndex;
    }
    
    private void spreadChanges() {
        if (position != lastPosition) {
            for (int i = 0; i < gameObject.getChildGOs().size(); i++) {
                gameObject.getChildGOs().get(i).transform.position.add(lastPosition.sub(position));
            }
        }
    }*/
    
    @Override
    public void imGui(){
        gameObject.name = OImGui.inputText("Name: ", gameObject.name);
        OImGui.drawVec2Control("Position", this.position);
        OImGui.drawVec2Control("Scale", this.scale, 32.0f);
        this.rotation = OImGui.dragFloat("Rotation", this.rotation);
        this.zIndex = OImGui.dragInt("Z-index", this.zIndex);
        
        if (gameObject.getParent() != null) {
            ImGui.text("Parent: "+gameObject.getParent().name);
        } else {
            ImGui.text("Parent: none");
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
            go.transform.translate(x, y);
        }
    }
}
