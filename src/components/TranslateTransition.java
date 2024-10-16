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
public class TranslateTransition extends TransitionState {
    
    private Vector2f pointA;
    private Vector2f pointB;
    private Vector2f offset;
    private float duration;

    public TranslateTransition(Vector2f offset, float duration) {
        this.offset = offset;
        this.duration = duration;
    }

    @Override
    public void start() {
        pointA = new Vector2f(this.gameObject.transform.position);
        pointB = new Vector2f(this.gameObject.transform.position);
        pointB.add(offset);
        timeTracker = 0;
        finished = false;
    }
    
    @Override
    public void step(float dt) {
        float factor = JMath.normalize(timeTracker,duration,0.0f,1.0f,0.0f);
        this.gameObject.transform.position.x = JMath.lerp(pointA.x, pointB.x, factor);
        this.gameObject.transform.position.y = JMath.lerp(pointA.y, pointB.y, factor);
        //System.out.println("time "+timeTracker);
        //System.out.println(factor);
        timeTracker += dt;
        if (timeTracker >= duration) {
            finished = true;
            // set to be safe
            this.gameObject.transform.position = pointB;
        }
    }
    
}
