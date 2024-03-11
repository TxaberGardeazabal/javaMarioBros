/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers.events;

/**
 *
 * @author txaber gardeazabal
 */
public class Event {
    public EventType type;

    public Event(EventType type) {
        this.type = type;
    }
    
    public Event() {
        this.type = EventType.UserEvent;
    }
}
