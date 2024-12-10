/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.TransitionMachine;
import gameEngine.GameObject;
import gameEngine.Prefab;
import gameEngine.PrefabSave;
import gameEngine.Window;
import org.joml.Vector2f;
import util.AssetPool;
import util.JMath;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class BowserAI extends Enemy {
    
    private float timeToSwap = 4;
    private transient float swapTime = 0;
    private float timeToBreathe = 6;
    private transient float breathTime = 0;
    private float timeToJump = 5;
    private transient float jumpTime = 0;
    private float health = 10;
    private transient boolean jumpLastFrame = false;
    
    private boolean active = true;
    private Vector2f frontOffset = new Vector2f(-0.375f,0.125f);
    
    @Override
    public void start() {
        super.start();
        this.walkSpeed = 0.3f;
        this.sizeX = Settings.GRID_WIDTH * 2;
        this.sizeY = Settings.GRID_HEIGHT * 2;
        this.innerWidth = 0.25f * 1.7f;
        this.castVal = -0.27f;
        
        swapTime = timeToSwap;
        breathTime = timeToBreathe;
        jumpTime = timeToJump;
    }
    
    @Override
    public void update(float dt) {
        super.update(dt);
        if (!active || isDead) {
            return;
        }
        
        swapTime -= dt;
        breathTime -= dt;
        jumpTime -= dt;
        
        if (swapTime <= 0) {
            goingRight = !goingRight;
            swapTime = timeToSwap;
        }
        if (goingRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        }
        
        if (breathTime <= 0) {
            spawnJet();
            stateMachine.trigger("toWalk");
            breathTime = timeToBreathe;
        } else if (breathTime < 1) {
            stateMachine.trigger("toBreathe");
        }
         
        checkOnGround();
        if (jumpTime <= 0) {
            this.velocity.y = 2.8f;

            jumpTime = timeToJump;
            jumpLastFrame = true;
        } else if (!onGround) {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        } else {
            // check if a jump was done last frame
            if (!jumpLastFrame) {
                this.acceleration.y = 0;
                this.velocity.y = 0;
            } else {
                jumpLastFrame = false;
            }
            
        }
        applyForces(dt);
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
        PrefabSave firePre = new PrefabSave("assets/prefabs/particles/firejet.prefab");
        GameObject fireball = firePre.load();
        
        if (fireball != null) {
            float diff = JMath.getRandom(-0.45f, 0.45f);
            fireball.transform.setPosition(new Vector2f(this.gameObject.transform.position)
                    .add(frontOffset)
                    .add(0,diff));

            TransitionMachine tm = fireball.getComponent(TransitionMachine.class);
            if (tm != null) {
                tm.start();
                tm.begin();
            }
            AssetPool.getSound("assets/sounds/bowserfire.ogg").play();
            Window.getScene().addGameObjectToScene(fireball);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
