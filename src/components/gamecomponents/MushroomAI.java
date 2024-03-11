/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import gameEngine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.components.Rigidbody2D;
import util.AssetPool;

/**
 *
 * @author txaber gardeazabal
 */
public class MushroomAI extends Component{
    private transient boolean goingRight = true;
    private transient Rigidbody2D rb;
    private transient Vector2f speed = new Vector2f(1.0f, 0.0f);
    private transient float maxSpeed = 0.8f;
    private transient boolean hitPlayer = false;
    
    @Override
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
    }
    
    @Override
    public void update(float dt) {
        if (Math.abs(rb.getVelocity().x) < maxSpeed) {
            if (goingRight) {
                rb.addVelocity(speed);
            } else {
                rb.addVelocity(new Vector2f(-speed.x, speed.y));
            }
        }
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
