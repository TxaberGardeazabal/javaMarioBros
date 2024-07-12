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
public class LevelSelectButtonBehavior implements ButtonBehavior {

    
    @Override
    public void onClick() {
        System.out.println("select!");
    }

    @Override
    public void onHold() {
    }

    @Override
    public void onHover() {
    }
    
}
