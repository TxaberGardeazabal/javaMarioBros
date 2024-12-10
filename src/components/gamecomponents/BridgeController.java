/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.TransitionMachine;
import gameEngine.GameObject;
import gameEngine.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 *
 * @author txaber gardeazabal
 */
public class BridgeController extends Component {
    private transient boolean hit = false;
    
    @Override
    public void update(float dt) {
        
    }
    
    @Override
    public void beginCollision(GameObject collidingObj, Contact contact, Vector2f contactN) {
        if (collidingObj.getComponent(PlayerController.class) != null && !hit) {
            GameObject collidingPlayer = collidingObj;
            hit = true;
            
            Window.getScene().getGameObjectByName("bridge").destroy();
            //Window.getScene().getGameObjectByName("bowser").getComponent(BowserAI.class).setActive(false);
            collidingPlayer.getComponent(PlayerController.class).playWinAnimation2();
            this.gameObject.destroy();
        }
    }
}
