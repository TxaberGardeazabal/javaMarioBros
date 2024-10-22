/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.PhysicsController;
import components.TransitionMachine;
import gameEngine.GameObject;
import gameEngine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.components.Rigidbody2D;
import util.Settings;

/**
 * Controllador del powerup estrella
 * @author txaber gardeazabal
 */
public class StarAI extends PhysicsController {
    
    private transient float speed = 1.7f;
    private transient float bounceForce = 1.5f;
    public transient boolean goingRight = true;
    private boolean active = true;
    
    @Override
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        this.acceleration.y = Window.getPhysics().getGravity().y * Settings.worldGravityMul;
    }
    
    @Override
    public void update(float dt) {
        if (active) {
            if (goingRight) {
                velocity.x = speed;
            } else {
                velocity.x = -speed;
            } 

            checkOnGround();
            if (onGround) {
                this.acceleration.y = bounceForce;
                this.velocity.y = 2f;
            } else {
                this.acceleration.y = Window.getPhysics().getGravity().y * Settings.worldGravityMul;
            }
            applyForces(dt);
        } else {
            TransitionMachine tm = this.gameObject.getComponent(TransitionMachine.class);
            if (tm != null && !tm.isPlaying()) {
                active = true;
            }
        }
    }
    
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f normal) {
        if (Math.abs(normal.x) > 0.8f) {
            this.goingRight = normal.x < 0; 
        }
        
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null) {
            playerController.invinciblePowerUp();
            gameObject.destroy();
        }
        
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    
}
