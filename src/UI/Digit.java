/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import components.Component;
import components.StateMachine;

/**
 *
 * @author txaber gardeazabal
 */
public class Digit extends Component{
    private StateMachine sm;
    
    @Override
    public void start() {
        this.sm = this.gameObject.getComponent(StateMachine.class);
        if (sm == null) {
            System.out.println("warning: couldn't find statemachine in digit "+this.gameObject.name);
        }
    }
    
    public void setDigit(String digit) {
        sm.trigger(digit);
    }
}
