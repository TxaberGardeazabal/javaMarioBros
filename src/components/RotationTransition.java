/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import org.joml.Vector2f;
import util.JMath;

/**
 *
 * @author txaber gardeazabal
 */
public class RotationTransition extends TransitionState {

    private transient Vector2f origin;
    private transient float pointA;
    private transient float pointB;
    private float offset;
    private float duration;
    
    public RotationTransition(float offset, float duration) {
        this.offset = offset;
        this.duration = duration;
    }
    
    @Override
    public void start() {
        pointA = this.gameObject.transform.rotation;
        pointB = pointA + offset;
        origin = this.gameObject.transform.position;
        timeTracker = 0;
        finished = false;
    }

    @Override
    public void step(float dt) {
        float factor = JMath.normalize(timeTracker,duration,0.0f,1.0f,0.0f);
        
        this.gameObject.transform.setRotation(JMath.lerp(pointA, pointB, factor), origin);
        
        //System.out.println("time "+timeTracker);
        //System.out.println(factor);
        timeTracker += dt;
        if (timeTracker >= duration) {
            finished = true;
            // set to be safe
            this.gameObject.transform.setRotation(pointB, origin);
        }
    }
    
}
