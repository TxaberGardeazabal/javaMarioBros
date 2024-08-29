/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;

/**
 * Monedas en los bloques de interrogacion
 * @author txaber gardeazabal
 */
public class BlockCoin extends Component{
    private Vector2f topY;
    private float coinSpeed = 1.8f;
    private float timeToKill = 0.15f;
    private boolean isTop = false;
    
    @Override
    public void start() {
        topY = new Vector2f(gameObject.transform.position.y).add(0,0.5f);
        EventSystem.notify(this.gameObject, new Event(EventType.CoinGet));
    }
    
    @Override
    public void update(float dt) {
        if  (this.gameObject.transform.position.y < topY.y && isTop == false) {
            this.gameObject.transform.position.y += dt * coinSpeed;
            if (this.gameObject.transform.position.y > topY.y) {
                this.isTop = true;
            }
        } else {
            if (timeToKill > 0) {
                this.gameObject.transform.position.y -= dt * coinSpeed;
                timeToKill -= dt; 
            } else {
                gameObject.destroy();
            }
        }
    }
}
