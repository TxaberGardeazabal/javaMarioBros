/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI.buttonBehaviors;

import UI.ButtonBehavior;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

/**
 *
 * @author txaber gardeazabal
 */
public class ExitButtonBehavior implements ButtonBehavior {

    
    @Override
    public void onClick() {
        EventSystem.notify(null, new Event(EventType.PlayLevel));
    }

    @Override
    public void onHold() {
    }

    @Override
    public void onHover() {
    }
    
}
