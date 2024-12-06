/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package physics2D.components;

import gameEngine.Window;
import org.joml.Vector2f;
import render.DebugDraw;
import util.JMath;

/**
 * Objeto de colision de dos dimensiones con forma de circulo 
 * @author txaber gardeazabal
 */
public class CircleCollider extends Collider{
    private float radius = 1f;
    public boolean showBoundaries = false;
    private transient boolean resetFixtureNextFrame = false;
    
    private Vector2f offset = new Vector2f();
    

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        resetFixture();
    }
    
    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset.set(offset);
        resetFixture();
    }
    
    @Override
    public void editorUpdate(float dt) {
        if (showBoundaries) {
            Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
            if (this.gameObject.transform.rotation != 0) {
                JMath.rotate(center, this.gameObject.transform.rotation, this.gameObject.transform.position);
            }
            DebugDraw.addCircle2D(center, radius);
        }
        
        /*if (resetFixtureNextFrame) {
            resetFixture();
        }*/
    }
    
    @Override
    public void update(float dt) {
        if (showBoundaries) {
            Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
            if (this.gameObject.transform.rotation != 0) {
                JMath.rotate(center, this.gameObject.transform.rotation, this.gameObject.transform.position);
            }
            DebugDraw.addCircle2D(center, radius);
        }
        
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }
    
    /**
     * Llama a la clase physics2D para restear el colider de la simulacion
     */
    public void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;
        
        if (gameObject != null) {
            Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
            if (rb != null) {
                Window.getPhysics().resetCircleCollider(rb, this);
            }
        }
    }
}
