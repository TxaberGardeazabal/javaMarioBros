/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.PhysicsController;
import gameEngine.GameObject;
import gameEngine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.Settings;

/**
 * Bola de fuego que lanza mario en su forma de mario fuego, las bolas botan por la pantalla
 * @author txaber gardeazabal
 */
public class Fireball extends PhysicsController {
    // cuantas bolas pueden existir al mismo de tiempo
    private static final int LIMIT = 4;
    private static int fireballCount;
    private transient float fireballSpeed = 1.7f;
    private transient float lifetime = 1.7f;
    private transient float bounceForce = 1.5f;
    public transient boolean goingRight = false;
    
    /**
     * Puede haber mas bolas en el nivel
     * @return Bolas en el nivel < bolas maximas.
     */
    public static boolean canSpawn()  {
        return fireballCount < LIMIT;
    }
    
    @Override
    public void start() {
        super.start();
        fireballCount++;
    }
    
    @Override
    public void update(float dt) {
        lifetime -= dt;
        if (lifetime <= 0) {
            delete();
            return;
        }
        
        rb.setAngularVelocity(rb.getAngularVelocity() - dt * 5);
        
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
    
    /**
     * Elimina la bola del nivel y actualiza la cantidad de estos
     */
    public void delete() {
        fireballCount--;
        this.gameObject.destroy();
    }
    
    /**
     * Resetea la cantidad de bolas a 0
     */
    public static void reset() {
        fireballCount = 0;
    }
}
