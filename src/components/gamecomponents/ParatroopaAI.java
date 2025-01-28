/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import gameEngine.GameObject;
import gameEngine.PrefabSave;
import gameEngine.Window;
import java.util.HashMap;
import java.util.Map;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
import physics2D.enums.BodyType;
import util.AssetPool;

/**
 *
 * @author txaber gardeazabal
 */
public class ParatroopaAI extends Enemy {
    
    @Override
    public void stomp() {
        AssetPool.getSound("assets/sounds/kick.ogg").play();
        PrefabSave pre = new PrefabSave("assets/prefabs/entities/koopaRed.prefab");
        GameObject turtle = pre.load();
        if (turtle != null) {
            turtle.transform.setPosition(this.gameObject.transform.position);
            Window.getScene().addGameObjectToScene(turtle);
        }
        this.gameObject.destroy();
    }
    
    @Override
    public void fireballHit(Vector2f direction) {
        MovingPlatform mp = this.gameObject.getComponent(MovingPlatform.class);
        if (mp != null) {
            mp.setLoops(false);
            mp.setTime(mp.getDuration());
        }
        
        this.rb.setBodyType(BodyType.Dynamic);
        die(direction.x < 0);
    }
    
    @Override
    public void die(boolean hitRight) {
        MovingPlatform mp = this.gameObject.getComponent(MovingPlatform.class);
        if (mp != null) {
            mp.setLoops(false);
            mp.setTime(mp.getDuration());
        }
        
        Map payload = new HashMap<>();
        payload.put("points", "200");
        EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));
        super.die(hitRight);
    }
}
