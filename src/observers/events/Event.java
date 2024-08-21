/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers.events;

import java.util.HashMap;
import java.util.Map;

/**
 * Un evento sirve para mandar informacion a objetos en diferentes niveles de abstraccion de manera sencilla.
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
    
    /**
     * Accede a la informacion contenida dentro del evento.
     * @param name label de la informacion a recibir
     * @return un string con la informacion solicitada, si no existe o no se pudo encontrar devolvera null
     */
    public String getPayload(String name) {
        return this.payload.getOrDefault(name, null);
    }
}
