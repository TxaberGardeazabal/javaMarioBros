/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import gameEngine.GameObject;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 *
 * @author txaber gardeazabal
 */
public class TrackSwapScript extends Component{
    
    @Override
    public void beginCollision(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {
        if (collidingObj.getComponent(PlayerController.class) != null) {
            EventSystem.notify(gameObject, new Event(EventType.SwapTheme));
        }
    }
}
