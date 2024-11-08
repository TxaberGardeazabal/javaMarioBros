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
    public void reachFlag() {
        if (!this.gameObject.getComponent(TransitionMachine.class).isPlaying()) {
            this.gameObject.getComponent(TransitionMachine.class).begin();
        }
    }
}
