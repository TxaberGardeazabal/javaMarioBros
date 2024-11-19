/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.TransitionMachine;
import gameEngine.GameObject;
import java.util.List;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;


/**
 * Controlador del enemigo planta pirana
 * @author txaber gardeazabal
 */
public class PiranhaPlantAi extends Enemy {

    private transient TransitionMachine tm;
    private transient boolean goingIn = false;
    
    private float timeBeforeGoingIn = 3;
    private float timeBeforeGoingOut = 4;
    private float inOffset = 1f;
    private transient float timeTracker = 0;
    
    @Override
    public void start() {
        super.start();
        tm = gameObject.getComponent(TransitionMachine.class);
        timeTracker = inOffset;
    }
    
    @Override
    public void update(float dt) {
        super.update(dt);
        if (isDead) {
            if (tm != null) {
                tm.stop();
            }
            return;
        }
        
        if (tm != null) {
            timeTracker -= dt;
            if (timeTracker <=0) {
                if (goingIn) {
                    timeTracker = timeBeforeGoingIn;
                    tm.startTransition(1);
                    goingIn = false;
                } else {
                    timeTracker = timeBeforeGoingOut;
                    tm.startTransition(0);
                    goingIn = true;
                }
            }
            
        }
    }
    
    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f normal) {
        if (isDead) {
            return;
        }
        
        List<Component> comps = go.getAllComponents();
        for (Component component : comps) {
            if (component instanceof PlayerController) {
                PlayerController playerController = (PlayerController)component;
                if (!playerController.isDead()) {
                    if (playerController.isStarInvincible()) {
                        die(normal.x < 0);
                    } else if (!playerController.isInvincible()) {
                        playerController.hurt();
                    }
                    contact.setEnabled(false);
                }
                
            } else if (component instanceof Fireball) {
                Fireball f = (Fireball) component;
                f.delete();
                fireballHit(normal);
            }
        }
    }
    
}
