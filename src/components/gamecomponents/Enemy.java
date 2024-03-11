/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.PhysicsController;
import components.StateMachine;
import gameEngine.Camera;
import gameEngine.GameObject;
import gameEngine.Window;
import java.util.List;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.components.Rigidbody2D;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class Enemy extends PhysicsController{
    protected transient boolean goingRight = false;
    protected transient float walkSpeed = 0.6f;
    protected transient boolean isDead = false;
    protected transient StateMachine stateMachine;
    
    @Override
    public void start() {
        super.start();
        this.stateMachine = gameObject.getComponent(StateMachine.class);
    }
    
    @Override
    public void update(float dt) {
        // stop updating if the enemy exits from the left of the screen
        // not sure if the camera will be able to move left in the future
        Camera camera = Window.getScene().camera();
        if (this.gameObject.transform.position.x > 
                camera.position.x + camera.getProjectionSize().x * camera.getZoom()) {
            gameObject.destroy();
            return;
        }
        
        if (isDead) {
            addGravity();
            applyForces(dt);
        }
        
    }
    
    public void stomp() {}
    
    public void die() {
        die(true);
    }
    
    public void die(boolean hitRight) {
        isDead = true;
        gameObject.transform.scale.y = -gameObject.transform.scale.y;
        rb.setIsSensor(true);
        if (hitRight) {
            acceleration.set(new Vector2f(0, 2f));
            velocity.set(new Vector2f(1,1.6f));
        } else {
            acceleration.set(new Vector2f(0, 2f));
            velocity.set(new Vector2f(-1,1.6f));
        }
    }
    
    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f normal) {
        if (isDead) {
            return;
        }
        
        List<Component> comps = go.getAllComponents();
        for (Component component : comps) {
            if (component instanceof PlayerController) {
                PlayerController playerController = (PlayerController)component;
                    if (!playerController.isDead() && !playerController.isHurtInvincible() && normal.y > 0.58f) {
                    playerController.enemyBounce();
                    stomp();
                } else if (!playerController.isDead() && !playerController.isInvincible()){
                    playerController.hurt();
                    contact.setEnabled(false);
                }
            }
        }
        
    }
    
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f normal) {
        if (isDead) {
            return;
        }
        
        if (Math.abs(normal.y) < 0.1f) {
            goingRight = normal.x < 0;
        }
    }
}
