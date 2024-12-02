/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import gameEngine.GameObject;
import gameEngine.PrefabSave;
import gameEngine.Window;
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
        PrefabSave pre = new PrefabSave("assets/prefabs/entities/RedKoopa.prefab");
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
}
