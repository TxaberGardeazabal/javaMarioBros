/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.gamecomponents.PlayerController;
import components.StateMachine;
import gameEngine.Camera;
import gameEngine.GameObject;
import gameEngine.Window;
import java.util.List;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.Physics2D;
import physics2D.components.CircleCollider;
import physics2D.components.Rigidbody2D;
import util.AssetPool;

/**
 *
 * @author txaber gardeazabal
 */
public class GoombaAI extends Enemy {    
    public transient boolean isStomped = false;
    private transient float timeToKill = 0.8f;
    
    @Override
    public void update(float dt) {
        super.update(dt);
        if (isDead) {
            return;
        }
        
        
        if (isStomped) {
            timeToKill -= dt;
            if (timeToKill <= 0) {
                this.gameObject.destroy();
            }
            this.rb.setVelocity(new Vector2f());
            return;
        }
        
        if (goingRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        } 
        
        checkOnGround();
        if (onGround) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }

        applyForces(dt);
    }
    
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f normal) {
        if (isStomped) {
            return;
        }
        super.beginCollision(go,contact,normal);
    }
    
    @Override
    public void stomp() {
        stomp(true);
    }

    public void stomp(boolean playSound) {
        this.isStomped = true;
        stopAllForces();
        this.rb.setIsSensor(true);
        this.stateMachine.trigger("squash");
        if (playSound) {
            AssetPool.getSound("assets/sounds/stomp.ogg").play();
        }
    }
}
