/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers;

import gameEngine.GameObject;
import observers.events.Event;

/**
 *
 * @author txaber gardeazabal
 */
public interface Observer {
    void onNotify(GameObject go, Event event);
}
