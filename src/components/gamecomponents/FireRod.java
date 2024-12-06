/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.PhysicsController;
import components.propertieComponents.StageHazard;
import gameEngine.GameObject;
import java.util.List;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.JMath;

/**
 *
 * @author txaber gardeazabal
 */
public class FireRod extends Component{
    
    private boolean clockWise = false;
    private float duration = 5;
    private float time = 0;
    
    @Override
    public void update(float dt) {
        if (time < duration) {
            if (clockWise) {
                this.gameObject.transform.setRotationNoSpread( -JMath.lerp(0, 360, time/duration), gameObject.transform.position);
            } else {
                this.gameObject.transform.setRotationNoSpread(JMath.lerp(0, 360, time/duration), gameObject.transform.position);
            }
            
            //this.gameObject.transform.rotate(dt, gameObject.transform.position);
            List<GameObject> children = this.gameObject.getChildGOs();
            for (GameObject go : children) {
                go.transform.rotate(-5);
            }
            time += dt;
        } else {
            time = 0;
        }
    }
    
    @Override
    public void preSolve(GameObject collidingObj, Contact contact, Vector2f contactN) {
        if (collidingObj.getComponent(PhysicsController.class) != null) {
            contact.setEnabled(false);
        }
    }
}
