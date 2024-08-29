/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import org.jbox2d.dynamics.contacts.Contact;
import gameEngine.GameObject;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
import util.AssetPool;

/**
 * Monedas recogibles por mario
 * @author txaber gardeazabal
 */
public class Coin extends Component{
    
    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f normal) {
        PlayerController player = go.getComponent(PlayerController.class);
        if (player != null && !player.isDead()) {
            contact.setEnabled(false);
            AssetPool.getSound("assets/sounds/coin.ogg").play();
            
            EventSystem.notify(this.gameObject, new Event(EventType.CoinGet));
            this.gameObject.destroy();
        }
    }
}
