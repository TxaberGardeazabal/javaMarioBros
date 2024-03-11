/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package physics2D.components;

import components.Component;
import gameEngine.Window;
import org.joml.Vector2f;
import render.DebugDraw;

/**
 *
 * @author txaber gardeazabal
 */
public class Box2DCollider extends Collider{
    private Vector2f halfSize = new Vector2f(1);
    private Vector2f origin = new Vector2f();
    public boolean showBoundaries = false;
    private transient boolean resetFixtureNextFrame = false;
    
    private Vector2f offset = new Vector2f();

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
        resetFixture();
    }

    public Vector2f getOrigin() {
        return origin;
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
            DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
        }
    }
    
    @Override
    public void update(float dt) {
        if (showBoundaries) {
            Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
            DebugDraw.addBox2D(center, this.halfSize, this.gameObject.transform.rotation);
        }
        
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }
    
    public void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;
        
        if (gameObject != null) {
            Rigidbody2D rb = gameObject.getComponent(Rigidbody2D.class);
            if (rb != null) {
                Window.getPhysics().resetBox2DCollider(rb, this);
            }
        }
    }
}
