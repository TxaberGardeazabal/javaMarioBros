/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;

/**
 *
 * @author txaber gardeazabal
 */
public abstract class TransitionState {
    
    protected transient boolean finished = false;
    protected transient float timeTracker = 0.0f;
    protected transient GameObject gameObject;
    
    public abstract void start();
    public abstract void step(float dt);
    
    public boolean hasEnded() {
        return finished;
    }
}
