/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import components.Component;
import components.StateMachine;
import editor.ConsoleWindow;

/**
 * El digito individual
 * @author txaber gardeazabal
 */
public class Digit extends Component{
    private StateMachine sm;
    
    @Override
    public void start() {
        this.sm = this.gameObject.getComponent(StateMachine.class);
        if (sm == null) {
            ConsoleWindow.addLog("couldn't find statemachine in digit "+this.gameObject.name,
                    ConsoleWindow.LogCategory.warning);
        }
    }
    
    /**
     * Asigna un valor numerico al digito.
     * @param digit el valor a asignar, en formato string.
     */
    public void setDigit(String digit) {
        sm.trigger(digit);
    }
}
