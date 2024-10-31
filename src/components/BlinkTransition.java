/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import org.joml.Vector4f;

/**
 *
 * @author txaber gardeazabal
 */
public class BlinkTransition extends TransitionState{
    private transient SpriteRenderer spr;
    private float duration;
    private transient int blinkS = 0;
    private transient boolean switchA = true;

    public BlinkTransition(float duration) {
        this.duration = duration;
    }

    @Override
    public void start() {
        spr = this.gameObject.getComponent(SpriteRenderer.class);
        timeTracker = 0;
        finished = false;
    }

    @Override
    public void step(float dt) {
        blinkS++;
        if (blinkS == 16) {
            if (switchA) {
                spr.setColor(new Vector4f(1f,1f,1f,0.7f));
                switchA = false;
            }else {
                spr.setColor(new Vector4f(1f,1f,1f,1f));
                switchA = true;
            }
            blinkS = 0;
        } 
        
        timeTracker += dt;
        if (timeTracker >= duration) {
            spr.setColor(new Vector4f(1f,1f,1f,1f));
            finished = true;
        }
    }
    
    
}
