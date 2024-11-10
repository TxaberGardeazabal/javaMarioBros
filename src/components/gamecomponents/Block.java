/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.TransitionMachine;
import gameEngine.GameObject;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;

/**
 * Funcionalidad general de los bloques interactuables por mario en el nivel
 * @author txaber gardeazabal
 */
public abstract class Block extends Component{
    protected transient TransitionMachine tm;
    protected transient boolean active = true;
    
    @Override
    public void start() {
        this.tm = gameObject.getComponent(TransitionMachine.class);
    }

    @Override
    public void beginCollision(GameObject collidingObj, Contact contact, Vector2f contactN) {
        PlayerController playerController = collidingObj.getComponent(PlayerController.class);
        if (active && playerController != null && contactN.y < -0.8f) {
            if (tm != null) {
                tm.begin();
            }
            AssetPool.getSound("assets/sounds/bump.ogg").play();
            hit(!playerController.isSmall());
        }
        
        if (active && collidingObj.getComponent(KoopaAI.class) != null) {
            
            KoopaAI koopa = collidingObj.getComponent(KoopaAI.class);
            if (koopa.isShelled && koopa.isShellMoving && contactN.y < 0.8f) { 
                if (tm != null) {
                    tm.begin();
                }
                hit(true);
            }
        }
    }
    
    /**
     * Pone el bloque en su estado 'inactivo'
     */
    public void setInactive() {
        this.active = false;
    }
    
    /**
     * Funcion a ejecutar cuando el jugador u otra entidad golpea el bloque
     * @param shouldOpen si el objeto debe soltar sus contenidos al golpear
     */
    protected abstract void hit(boolean shouldOpen);
    
}
