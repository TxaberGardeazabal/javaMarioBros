/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import gameEngine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 *
 * @author txaber gardeazabal
 */
public class PlayerSensor extends Component{
    
    private transient GameObject collidingObject = null;
    private transient boolean objectPresent = false;
    
    @Override
    public void beginCollision(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {
        if (collidingObj.getComponent(PlayerController.class) != null) {
            collidingObject = collidingObj;
            objectPresent = true;
        }
    }
    
    @Override
    public void endCollision(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {
        if (collidingObj.getComponent(PlayerController.class) != null) {
            collidingObject = null;
            objectPresent = false;
        }
    }

    public GameObject getCollidingObject() {
        return collidingObject;
    }

    public boolean isObjectPresent() {
        return objectPresent;
    }
}
