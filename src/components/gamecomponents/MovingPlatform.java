/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.PhysicsController;
import gameEngine.Direction;
import gameEngine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.JMath;

/**
 *
 * @author txaber gardeazabal
 */
public class MovingPlatform extends Component{
    
    private Direction direction = Direction.Up;
    private float distance;
    private float duration = 1.5f;
    
    private transient Vector2f target = new Vector2f();
    private transient Vector2f start;
    private transient float time = 0;
    private transient Vector2f oldPos = new Vector2f();
    private transient GameObject collidingPlayer = null;
    
    @Override
    public void start() {
        
        start = new Vector2f(this.gameObject.transform.position);
        switch(direction) {
            case Up:
                target = new Vector2f(this.gameObject.transform.position).add(new Vector2f(0,distance));
                break;
            case Down:
                target = new Vector2f(this.gameObject.transform.position).add(new Vector2f(0,-distance));
                break;
            case Left:
                target = new Vector2f(this.gameObject.transform.position).add(new Vector2f(-distance,0));
                break;
            case Right:
                target = new Vector2f(this.gameObject.transform.position).add(new Vector2f(distance,0));
                break;
        }
    }
    
    @Override
    public void update(float dt) {
        
        if (time < duration) {
            if (time/duration <= 0.5f) {
                this.gameObject.transform.setPosition(new Vector2f(
                    JMath.lerp(start.x, target.x, JMath.easeInOut((time/duration)/0.5f)),
                    JMath.lerp(start.y, target.y, JMath.easeInOut((time/duration)/0.5f))));
            } else {
                this.gameObject.transform.setPosition(new Vector2f(
                    JMath.lerp(start.x, target.x, JMath.easeInOut(JMath.flip(time/duration)/0.5f)),
                    JMath.lerp(start.y, target.y, JMath.easeInOut(JMath.flip(time/duration)/0.5f))));
            }
            time += dt;
            
        } else {
            time = 0;
        }
        Vector2f deltaMove = new Vector2f(this.gameObject.transform.position).sub(oldPos).mul(32,32);
        System.out.println(deltaMove);
        if (collidingPlayer != null) {
            collidingPlayer.getComponent(PhysicsController.class).addAcceleration(deltaMove);
        }
        oldPos = new Vector2f(this.gameObject.transform.position);
    }
    
    @Override
    public void beginCollision(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {
        if (collidingObj.getComponent(PlayerController.class) != null) {
            collidingPlayer = collidingObj;
        }
    }
    
    @Override
    public void endCollision(GameObject go, Contact cntc, Vector2f normal) {
        if (go.getComponent(PlayerController.class) != null) {
            collidingPlayer = null;
        }
    }
}
