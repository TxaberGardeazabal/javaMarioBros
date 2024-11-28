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
    
    private Direction direction = Direction.Left;
    private float distance = 2;
    private float duration = 1.5f;
    private boolean destroyOnEnd = false;
    private boolean loops = true;
    private boolean linear = false;
    
    private transient Vector2f target = new Vector2f();
    private transient Vector2f start;
    private float time = 0;
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
                if (linear) {
                    this.gameObject.transform.setPosition(new Vector2f(
                        JMath.lerp(start.x, target.x, time/duration),
                        JMath.lerp(start.y, target.y, time/duration)));
                } else {
                    if (time/duration <= 0.5f) {
                        this.gameObject.transform.setPosition(new Vector2f(
                            JMath.lerp(start.x, target.x, JMath.easeInOut((time/duration)/0.5f)),
                            JMath.lerp(start.y, target.y, JMath.easeInOut((time/duration)/0.5f))));
                    } else {
                        this.gameObject.transform.setPosition(new Vector2f(
                            JMath.lerp(start.x, target.x, JMath.easeInOut(JMath.flip(time/duration)/0.5f)),
                            JMath.lerp(start.y, target.y, JMath.easeInOut(JMath.flip(time/duration)/0.5f))));
                    }
                }
                time += dt;
            } else {
                if (loops) {
                    time = 0; 
                } else if (destroyOnEnd) {
                    this.gameObject.destroy();
                }
            }
        
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean isDestroyOnEnd() {
        return destroyOnEnd;
    }

    public void setDestroyOnEnd(boolean destroyOnEnd) {
        this.destroyOnEnd = destroyOnEnd;
    }

    public boolean isLoops() {
        return loops;
    }

    public void setLoops(boolean loops) {
        this.loops = loops;
    }

    public boolean isLinear() {
        return linear;
    }

    public void setLinear(boolean linear) {
        this.linear = linear;
    }
    
    @Override
    public void beginCollision(GameObject collidingObj,Contact cntct,Vector2f hitNormal) {
        if (collidingObj.getComponent(PhysicsController.class) != null && hitNormal.y > 0.8f) {
            this.gameObject.addChild(collidingObj);
            collidingPlayer = collidingObj;
        }
    }
    
    @Override
    public void endCollision(GameObject go, Contact cntc, Vector2f normal) {
        if (go.equals(collidingPlayer)) {
            this.gameObject.removeChild(collidingPlayer);
            collidingPlayer.setParent(null);
            collidingPlayer = null;
        }
    }
    
    @Override
    public void destroy() {
        this.gameObject.removeChild(collidingPlayer);
        if (collidingPlayer != null) {
            collidingPlayer.setParent(null);
        }
        collidingPlayer = null;
    }
}
