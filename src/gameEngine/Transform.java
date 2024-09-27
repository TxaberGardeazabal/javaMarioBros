/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import components.Component;
import editor.OImGui;
import org.joml.Vector2f;

/**
 * La clase transform contiene la informacion de la posicion, rotacion y escala de un objeto dentro del mundo de juego
 * @author txaber
 */
public class Transform extends Component{
    
    public Vector2f position;
    public float rotation;
    public Vector2f scale;
    public int zIndex = 0;

    public Transform() {
        this.position = new Vector2f();
        this.rotation = 0;
        this.scale = new Vector2f();
    }

    public Transform(Vector2f position) {
        this.position = position;
        this.rotation = 0;
        this.scale = new Vector2f();
    }
    
    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        this.rotation = 0;
        this.scale = scale;
    }
    
    public Transform(Vector2f position, float rotation, Vector2f scale) {
        this.position = position;
        this.rotation = rotation;
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
    
    /**
     * Copia este objeto
     * @return un objeto nuevo con los mismos parametros de este objeto
     */
    public Transform copy() {
        Transform ret = new Transform(new Vector2f(this.position), this.rotation, new Vector2f(this.scale));
        ret.zIndex = this.zIndex;
        return ret;
    }
    
    /**
     * Copia los valores de este transform
     * @param to un transform donde copiar este objeto
     */
    public void copy(Transform to) {
        to.position.set(this.position);
        to.rotation = this.rotation;
        to.scale.set(this.scale);
        to.zIndex = this.zIndex;
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
    
    /**
     * Mueve este transform y todos sus hijos
     * @param x movimiento en el eje x
     * @param y movimiento en el eje y
     */
    public void translate(float x,float y) {
        Vector2f translate = new Vector2f(x,y);
        position.add(translate);
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.translate(translate);
        }
    }
    
    /**
     * Mueve este transform y todos sus hijos
     * @param translate vector de movimiento
     */
    public void translate(Vector2f translate) {
        position.add(translate);
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.translate(translate);
        }
    }
    
    /**
     * Rota este transform y todos sus hijos, solo en el eje z
     * @param z movimiento de rotacion en grados
     */
    public void rotate(float z) {
        rotation += z;
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.rotate(z);
        }
    }
    
    /**
     * Escala este transform y todos sus hijos
     * @param x escalado en el eje x
     * @param y escalado en el eje y
     */
    public void scale(float x,float y) {
        Vector2f scalew = new Vector2f(x,y);
        this.scale.add(scalew);
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.scale(x, y);
        }
    }
    
    /**
     * Escala este transform y todos sus hijos
     * @param scalew vector en el que escalar
     */
    public void scale(Vector2f scalew) {
        this.scale.add(scalew);
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.scale(scalew);
        }
    }

    /**
     * Asigna el zindex y el de todos sus hijos
     * @param zIndex nuevo valor del zindex
     */
    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.setzIndex(zIndex);
        }
    }
    
    /**
     * Mueve este transform a una posicion, pasando el movimiento a sus hijos
     * @param newPos un posicion dentro del mundo donde mover el objeto
     */
    public void setPosition(Vector2f newPos) {
        Vector2f delta = new Vector2f(newPos).sub(this.position);
        this.position = newPos;
        
        for (GameObject go : gameObject.getChildGOs()) {
            go.transform.translate(delta);
        }
    }
}
