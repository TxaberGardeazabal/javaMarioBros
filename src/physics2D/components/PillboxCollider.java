/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package physics2D.components;

import components.Component;
import gameEngine.Window;
import org.joml.Vector2f;

/**
 * Objeto de colision de dos dimensiones con forma de capsula.
 * la forma de calpsula se consigue con 2 circlecollider y un boxcollider
 * @author txaber gardeazabal
 */
public class PillboxCollider extends Collider {
    private transient CircleCollider topCircle = new CircleCollider();
    private transient CircleCollider bottomCircle = new CircleCollider();
    private transient Box2DCollider box = new Box2DCollider();
    private transient boolean resetFixtureNextFrame = false;
    public boolean showBoundaries = false;
    
    public float width = 1.0f;
    public float height = 1.0f;
    
    private Vector2f offset = new Vector2f();
    
    public Vector2f getOffset() {
        return this.offset;
    }
    
    @Override
    public void start() {
        this.topCircle.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.box.gameObject = this.gameObject;
        recalculateColliders();
    }
    
    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }
    
    @Override
    public void editorUpdate(float dt) {
        if (showBoundaries) {
            topCircle.showBoundaries = true;
            bottomCircle.showBoundaries = true;
            box.showBoundaries = true;
        } else {
            box.showBoundaries = false;
            topCircle.showBoundaries = false;
            bottomCircle.showBoundaries = false;
        }
        
        topCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);
        box.editorUpdate(dt);
        
        if (resetFixtureNextFrame) {
            resetFixture();
        }
        
    }

    public void setWidth(float width) {
        this.width = width;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(float height) {
        this.height = height;
        recalculateColliders();
        resetFixture();
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
                Window.getPhysics().resetPillboxCollider(rb, this);
            }
        }
    }
    
    public void recalculateColliders() {
        float circleRadius = width / 4.0f;
        float boxHeight = height - 2 * circleRadius;
        topCircle.setRadius(circleRadius);
        bottomCircle.setRadius(circleRadius);
        topCircle.setOffset(new Vector2f(offset).add(0,boxHeight / 4.0f));
        topCircle.setOffset(new Vector2f(offset).sub(0,boxHeight / 4.0f));
        box.setHalfSize(new Vector2f(width / 2.0f, boxHeight / 2.0f));
        box.setOffset(offset);
    }

    public CircleCollider getTopCircle() {
        return topCircle;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBox() {
        return box;
    }
}
