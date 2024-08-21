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

/**
 * Powerup flor de fuego
 * @author txaber gardeazabal
 */
public class Flower extends Component{
    private transient Rigidbody2D rb;
    
    @Override
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        this.rb.setIsSensor(true);
    }
    
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f normal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null) {
            if (playerController.isSmall()) {
                playerController.powerUp();
            }
            playerController.powerUp();
            this.gameObject.destroy();
        }
    }
    
}
