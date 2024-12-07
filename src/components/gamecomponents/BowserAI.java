/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.TransitionMachine;
import gameEngine.GameObject;
import gameEngine.Prefab;
import gameEngine.Window;
import org.joml.Vector2f;
import util.AssetPool;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class BowserAI extends Enemy {
    
    private float timeToSwap = 4;
    private float swapTime = 0;
    private float timeToBreathe = 6;
    private float breathTime = 0;
    private float health = 10;
    
    @Override
    public void start() {
        super.start();
        this.walkSpeed = 0.3f;
        this.sizeX = Settings.GRID_WIDTH * 2;
        this.sizeY = Settings.GRID_HEIGHT * 2;
        
        swapTime = timeToSwap;
        breathTime = timeToBreathe;
    }
    
    @Override
    public void update(float dt) {
        super.update(dt);
        swapTime -= dt;
        breathTime -= dt;
      
        
        if (swapTime < 0) {
            goingRight = !goingRight;
            swapTime = timeToSwap;
        }
        
        if (goingRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        }
        
        if (breathTime < 0) {
            spawnJet();
            stateMachine.trigger("toWalk");
            breathTime = timeToBreathe;
        } else if (breathTime < 1) {
            stateMachine.trigger("toBreathe");
        }
    }
    
    @Override
    public void fireballHit(Vector2f direction) {
        health--;
        if (health <= 0) {
            AssetPool.getSound("assets/sounds/bowserfalls.ogg").play();
            die();
        }
    }
    
    private void spawnJet() {
        GameObject fireball = Prefab.generateFireJet();
        fireball.transform.setPosition(gameObject.transform.position);
        TransitionMachine tm = fireball.getComponent(TransitionMachine.class);
        if (tm != null) {
            tm.start();
            tm.begin();
        }
        AssetPool.getSound("assets/sounds/bowserfire.ogg").play();
        Window.getScene().addGameObjectToScene(fireball);
    }
}
