/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import gameEngine.GameObject;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 * Controlador del champiñon de vida extra
 * @author txaber gardeazabal
 */
public class LiveMushroom extends MushroomAI {
    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f normal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null) {
            contact.setEnabled(false);
            if (!hitPlayer) {
                EventSystem.notify(this.gameObject, new Event(EventType.oneUp));
                this.gameObject.destroy();
                hitPlayer = true;
            }
        }
        
        if (Math.abs(normal.y) < 0.1f) {
            goingRight = normal.x < 0;
        }
    }
}
