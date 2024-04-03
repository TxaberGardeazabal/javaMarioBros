/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.gamecomponents.PlayerController;
import components.StateMachine;
import gameEngine.Camera;
import gameEngine.GameObject;
import gameEngine.Window;
import java.util.List;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.Physics2D;
import physics2D.components.CircleCollider;
import physics2D.components.Rigidbody2D;
import util.AssetPool;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class KoopaAI extends Enemy {
    private transient boolean isShelled = false;
    private transient boolean isShellMoving = false;
    private float movingDebounce = 0.32f;
    
    @Override
    public void update(float dt) {
        super.update(dt);
        if (isDead) {
            return;
        }

        movingDebounce -= dt;
        
        if (!isShelled || isShellMoving) {
            if (goingRight) {
                gameObject.transform.scale.x = -0.25f;
                velocity.x = walkSpeed;
            } else {
                gameObject.transform.scale.x = 0.25f;
                velocity.x = -walkSpeed;
            }
        } else {
            velocity.x = 0;
        }
        
        checkOnGround();
        if (onGround) {
            this.acceleration.y = 0;
            this.velocity.y = 0;
        } else {
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        }
        
        applyForces(dt);
        
        // removes when falling of the level
        if (this.gameObject.transform.position.x < Window.getScene().camera().position.x - 0.5f 
                || this.gameObject.transform.position.y < 0.0f) {
            this.gameObject.destroy();
        }
    }
     
    @Override
    public void stomp() {
        this.isShelled = true;
        this.isShellMoving = false;
        stopAllForces();
        this.stateMachine.trigger("squash");
        AssetPool.getSound("assets/sounds/kick.ogg").play();
    }
    
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f normal) {
        if (isDead) {
            return;
        }
        
        List<Component> comps = go.getAllComponents();
        for (Component component : comps) {
            if (component instanceof PlayerController) {
                PlayerController playerController = (PlayerController)component;
                if (!playerController.isDead() && !playerController.isHurtInvincible()) {
                    if (!isShelled && normal.y > 0.58f) {
                        playerController.enemyBounce();
                        stomp();
                        walkSpeed *= 3.0f;
                    } else if (movingDebounce < 0 && (isShellMoving ||!isShelled) && normal.y < 0.58f) {
                        playerController.hurt();
                    } else {
                        if (isShelled && normal.y > 0.58f) {
                            playerController.enemyBounce();
                            AssetPool.getSound("assets/sounds/kick.ogg").play();
                            isShellMoving = !isShellMoving;
                            goingRight = normal.x < 0;
                        } else if (isShelled && !isShellMoving) {
                            AssetPool.getSound("assets/sounds/kick.ogg").play();
                            isShellMoving = true;
                            goingRight = normal.x < 0;
                            movingDebounce = 0.32f;
                        }
                    }
                }
            } else if (component instanceof Fireball) {
                Fireball f = (Fireball) component;
                f.delete();
                die(normal.x < 0);
            }
        }
            
        if (Math.abs(normal.y) < 0.1f) {
            goingRight = normal.x < 0;
            if (isShellMoving && isShelled) {
                AssetPool.getSound("assets/sounds/bump.ogg").play();
            }
        }
    }
    
    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f normal) {
        List<Component> comps = go.getAllComponents();
        for (Component component : comps) {
            if (component instanceof Enemy) {
                if (component instanceof KoopaAI) {
                    KoopaAI e = (KoopaAI)component;
                    if (isShelled && isShellMoving) {
                        e.die(normal.x > 0);
                        contact.setEnabled(false);
                        AssetPool.getSound("assets/sounds/kick.ogg").play();
                        if (e.isShelled && e.isShellMoving) {
                            die(normal.x < 0);
                        }
                    }
                } else {
                    Enemy e = (Enemy)component;
                    if (isShelled && isShellMoving) {
                        e.die(normal.x > 0);
                        contact.setEnabled(false);
                        AssetPool.getSound("assets/sounds/kick.ogg").play();
                    }
                }
            }
        }
    }
}
