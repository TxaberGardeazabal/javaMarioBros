/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import gameEngine.GameObject;
import java.util.HashMap;
import java.util.Map;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
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
            playerController.powerUp();
            
            Map payload = new HashMap<>();
            payload.put("points", "1000");
            EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));

            this.gameObject.destroy();
        }
    }
    
}
