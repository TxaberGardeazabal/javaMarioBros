/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.PhysicsController;
import components.TransitionMachine;
import gameEngine.GameObject;
import java.util.HashMap;
import java.util.Map;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.enums.BodyType;

/**
 * Controllador del powerup champiñon
 * @author txaber gardeazabal
 */
public class MushroomAI extends PhysicsController {
    protected transient boolean goingRight = true;
    private transient float speed = 0.8f;
    protected transient boolean hitPlayer = false;
    private boolean active = true;
    
    @Override
    public void start() {
        super.start();
        hasGravity = false;
    }
    
    @Override
    public void update(float dt) {
        if (active) {
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
        } else {
            TransitionMachine tm = this.gameObject.getComponent(TransitionMachine.class);
            if (tm != null && !tm.isPlaying()) {
                active = true;
                rb.setBodyType(BodyType.Dynamic);
                rb.reset();
            }
        }
    }
    
    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f normal) {
        if (!active) {
            this.stopAllForces();
            return;
        }
        
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null) {
            contact.setEnabled(false);
            if (!hitPlayer) {
                playerController.powerUp();
                
                Map payload = new HashMap<>();
                payload.put("points", "1000");
                EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));
        
                this.gameObject.destroy();
                hitPlayer = true;
            }
        }
        
        if (Math.abs(normal.y) < 0.1f) {
            goingRight = normal.x < 0;
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
