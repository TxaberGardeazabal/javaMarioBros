/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import gameEngine.GameObject;
import gameEngine.Window;
import java.util.HashMap;
import java.util.Map;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 * El poste de bandera al final del nivel
 * @author txaber gardeazabal
 */
public class FlagPole extends Component{
    private boolean isTop = false;
    private int poleHeightNumber;

    public FlagPole(boolean isTop, int poleHeightNumber) {
        this.isTop = isTop;
        this.poleHeightNumber = poleHeightNumber;
    }
    
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f normal) {
        PlayerController pb = obj.getComponent(PlayerController.class);
        if (pb != null && !pb.hasWon()) {
            pb.playWinAnimation(this.gameObject);
            
            if (isTop) {
                EventSystem.notify(this.gameObject, new Event(EventType.OneUp));
            } else {
                Map payload = new HashMap<>();
                switch (poleHeightNumber) {
                    case 1:
                        payload.put("points", "100");
                        break;
                    case 2:
                        payload.put("points", "100");
                        break;
                    case 3:
                        payload.put("points", "400");
                        break;
                    case 4:
                        payload.put("points", "400");
                        break;
                    case 5:
                        payload.put("points", "800");
                        break;
                    case 6:
                        payload.put("points", "800");
                        break;
                    case 7:
                        payload.put("points", "2000");
                        break;
                    case 8:
                        payload.put("points", "2000");
                        break;
                    case 9:
                        payload.put("points", "5000");
                        break;
                    default:
                        payload.put("points", "0");
                        break;
                }
                EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));
            }
            
            Window.getScene().getGameObjectByName("flag").getComponent(Flag.class).reachFlag();
        }
    }
    
}
