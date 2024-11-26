/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import components.gamecomponents.PlayerController;
import org.joml.Vector2f;
import util.JMath;

/**
 *
 * @author txaber gardeazabal
 */
public class TranslateTransition extends TransitionState {
    
    private transient Vector2f pointA;
    private transient Vector2f pointB;
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
        if (this.gameObject.getComponent(PlayerController.class) != null) {
            this.gameObject.getComponent(PlayerController.class).setPosition(new Vector2f(
                    JMath.lerp(pointA.x, pointB.x, factor),
                    JMath.lerp(pointA.y, pointB.y, factor)));
        } else {
            this.gameObject.transform.setPosition(new Vector2f(
                    JMath.lerp(pointA.x, pointB.x, factor),
                    JMath.lerp(pointA.y, pointB.y, factor)
            ));
        }
        //System.out.println("time "+timeTracker);
        //System.out.println(factor);
        timeTracker += dt;
        if (timeTracker >= duration) {
            finished = true;
            // set to be safe
            this.gameObject.transform.setPosition(pointB);
        }
    }
    
    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }
}
