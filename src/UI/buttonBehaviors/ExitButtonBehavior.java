/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI.buttonBehaviors;

import UI.ButtonBehavior;

/**
 *
 * @author txaber gardeazabal
 */
public class ExitButtonBehavior implements ButtonBehavior {

    
    @Override
    public void onClick() {
        System.out.println("click!");
    }

    @Override
    public void onHold() {
    }

    @Override
    public void onHover() {
    }
    
}
