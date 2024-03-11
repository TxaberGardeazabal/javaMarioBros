/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers;

import gameEngine.GameObject;
import java.util.ArrayList;
import java.util.List;
import observers.events.Event;

/**
 *
 * @author txaber gardeazabal
 */
public class EventSystem {
    private static List<Observer> observers = new ArrayList<>();
    
    public static void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    public static void notify(GameObject go, Event event){
        for (Observer observer : observers) {
            observer.onNotify(go, event);
        }
    }
}
