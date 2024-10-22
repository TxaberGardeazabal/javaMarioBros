/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

/**
 *
 * @author txaber gardeazabal
 */
public class BlinkTransition extends TransitionState{
    private transient SpriteRenderer spr;
    private float duration;

    public BlinkTransition(float duration) {
        this.duration = duration;
    }

    @Override
    public void start() {
        spr = this.gameObject.getComponent(SpriteRenderer.class);
        
    }

    @Override
    public void step(float dt) {
        
        
        timeTracker += dt;
        if (timeTracker >= duration) {
            finished = true;
            //this.gameObject.transform.position = pointB;
        }
    }
    
    
}
