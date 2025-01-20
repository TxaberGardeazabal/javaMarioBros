/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI.buttonBehaviors;

import UI.ButtonBehavior;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

/**
 *
 * @author txaber gardeazabal
 */
public class StartButtonBehavior implements ButtonBehavior{

    @Override
    public void onClick() {
        Map firstLevel = new HashMap<>();
        File file = new File("assets/levels/game/1-1.txt");
        firstLevel.put("filepath", file.getAbsolutePath());
        EventSystem.notify(null, new Event(EventType.PlayLevel, firstLevel));
    }

    @Override
    public void onHold() {
    }

    @Override
    public void onHover() {
    }
    
}
