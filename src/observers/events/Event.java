/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers.events;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author txaber gardeazabal
 */
public class Event {
    public EventType type;
    private Map<String, String> payload = new HashMap<>();

    public Event(EventType type) {
        this.type = type;
    }
    
    public Event(EventType type, Map<String, String> payload) {
        this.type = type;
        if (payload != null) {
            this.payload = payload;
        }
    }
    
    public Event() {
        this.type = EventType.UserEvent;
    }
    
    public String getPayload(String name) {
        return this.payload.getOrDefault(name, null);
    }
}
