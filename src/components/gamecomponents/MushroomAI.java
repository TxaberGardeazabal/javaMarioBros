/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.PhysicsController;
import gameEngine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 * Controllador del powerup champiñon
 * @author txaber gardeazabal
 */
public class MushroomAI extends PhysicsController {
    private transient boolean goingRight = true;
    private transient float speed = 0.8f;
    private transient boolean hitPlayer = false;
    
    @Override
    public void start() {
        super.start();
        hasGravity = false;
    }
    
    @Override
    public void update(float dt) {
        
        if (goingRight) {
            velocity.x = speed;
        } else {
            velocity.x = -speed;
        }
        
        if (checkOnGround()) {
            velocity.y = 0;
        } else {
            addGravity();
        }
        applyForces(dt);
    }
    
    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f normal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null) {
            contact.setEnabled(false);
            if (!hitPlayer) {
                playerController.powerUp();
                this.gameObject.destroy();
                hitPlayer = true;
            }
        }
        
        if (Math.abs(normal.y) < 0.1f) {
            goingRight = normal.x < 0;
        }
    }
}
