/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.PhysicsController;
import gameEngine.Camera;
import gameEngine.GameObject;
import gameEngine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.Physics2D;
import physics2D.components.Rigidbody2D;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class Fireball extends PhysicsController{
    private static final int LIMIT = 4;
    private static int fireballCount;
    private transient float fireballSpeed = 1.7f;
    private transient float lifetime = 1.7f;
    private transient float bounceForce = 1.5f;
    public transient boolean goingRight = false;
    
    
    public static boolean canSpawn()  {
        return fireballCount < LIMIT;
    }
    
    @Override
    public void start() {
        fireballCount++;
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * Settings.worldGravityMul;
    }
    
    @Override
    public void update(float dt) {
        lifetime -= dt;
        if (lifetime <= 0) {
            delete();
            return;
        }
        
        /*CircleCollider cc = gameObject.getComponent(CircleCollider.class);
        if (!cc.showBoundaries) {
            cc.showBoundaries = true;
        }*/
        
        rb.setAngularVelocity(rb.getAngularVelocity() - dt * 5);
        //System.out.println(gameObject.transform.rotation);
        
        if (goingRight) {
            velocity.x = fireballSpeed;
        } else {
            velocity.x = -fireballSpeed;
        } 
        
        checkOnGround();
        if (onGround) {
            this.acceleration.y = bounceForce;
            this.velocity.y = 2f;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * Settings.worldGravityMul;
        }
        
        applyForces(dt);
    }
    
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f normal) {
        if (Math.abs(normal.x) > 0.8f) {
            this.goingRight = normal.x < 0; 
        }
        
    }
    
    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f normal) {
        if (go.getComponent(PlayerController.class) != null 
                || go.getComponent(Fireball.class) != null) {
            contact.setEnabled(false);
        }
    }
    
    public void delete() {
        fireballCount--;
        this.gameObject.destroy();
    }
    
    public static void reset() {
        fireballCount = 0;
    }
}
