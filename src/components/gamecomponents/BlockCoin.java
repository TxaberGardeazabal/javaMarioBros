/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.TransitionMachine;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;

/**
 * Monedas en los bloques de interrogacion
 * @author txaber gardeazabal
 */
public class BlockCoin extends Component{
    private transient TransitionMachine tm;
    
    @Override
    public void start() {
        EventSystem.notify(this.gameObject, new Event(EventType.CoinGet));
        
        tm = this.gameObject.getComponent(TransitionMachine.class);
        if (tm != null) {
            tm.begin();
        }
    }
    
    @Override
    public void update(float dt) {
        if (tm != null && !tm.isPlaying()) {
            gameObject.destroy();
        }
    }
}
