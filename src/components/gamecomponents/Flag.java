/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.TransitionMachine;

/**
 * La bandera arriba del poste al final del nivel
 * @author txaber gardeazabal
 */
public class Flag extends Component{
    private boolean flagReached = false;
    private float distanceToTravel = 1.9f;
    
    @Override
    public void update(float dt) {
        
        /*if (flagReached) {
            if (distanceToTravel > 0) {
                distanceToTravel -= dt;
                gameObject.transform.position.y -= dt;
            }
        }*/
    }
    
    public void reachFlag() {
        if (!this.gameObject.getComponent(TransitionMachine.class).isPlaying()) {
            this.gameObject.getComponent(TransitionMachine.class).begin();
        }
    }
}
