/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import gameEngine.GameObject;
import gameEngine.Window;
import java.util.List;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;

/**
 * Controllador del enemigo koopa verde
 * @author txaber gardeazabal
 */
public class KoopaAI extends Enemy {
    public transient boolean isShelled = false;
    public transient boolean isShellMoving = false;
    private float movingDebounce = 0.32f;
    private float stompDebounce = 0.32f;
    
    @Override
    public void update(float dt) {
        super.update(dt);
        if (isDead || !inCamera) {
            return;
        }

        movingDebounce -= dt;
        stompDebounce -= dt;
        
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
    }
     
    @Override
    public void stomp() {
        if (stompDebounce < 0) {
            stompDebounce = 0.32f;
            if (!isShelled) {
                isShelled = true;
                walkSpeed *= 3.0f;
                this.stateMachine.trigger("squash");
                stopAllForces();
            } else {
                if (isShellMoving) {
                    isShellMoving = false;
                    stopAllForces();
                    AssetPool.getSound("assets/sounds/kick.ogg").play();
                } else {
                    isShellMoving = true;
                    AssetPool.getSound("assets/sounds/kick.ogg").play();
                }
            }
        }
    }
    
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f normal) {
        if (isDead) {
            return;
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
        if (isDead) {
            return;
        }
        List<Component> comps = go.getAllComponents();
        for (Component component : comps) {
            if (component instanceof PlayerController) {
                PlayerController playerController = (PlayerController)component;
                if (!playerController.isDead()) {
                    if (!isShelled) {
                        // player to koopa interactions
                        if (!playerController.isStarInvincible() && normal.y > 0.58f) {
                            playerController.enemyBounce();
                            stomp();
                        } else if (!playerController.isInvincible()){
                            playerController.hurt();
                        }
                    } else {
                        // shell to player interactions
                        if (movingDebounce < 0 && isShellMoving && normal.y < 0.58f && !playerController.isInvincible()) {
                            playerController.hurt();
                        } else {
                            if (normal.y > 0.58f) {
                                playerController.enemyBounce();
                                stomp();
                                goingRight = normal.x < 0;
                            } else if (!isShellMoving) {
                                stomp();
                                goingRight = normal.x < 0;
                                movingDebounce = 0.32f;
                            }
                        }
                    }
                    if (playerController.isStarInvincible()) {
                        die(normal.x < 0);
                    }
                    contact.setEnabled(false);
                }
            } else if (component instanceof Fireball) {
                Fireball f = (Fireball) component;
                f.delete();
                die(normal.x < 0);
            } else if (component instanceof Enemy) {
                if (component instanceof KoopaAI) {
                    KoopaAI otherKoopa = (KoopaAI)component;
                    if (isShelled && isShellMoving) {
                        otherKoopa.die(normal.x > 0);
                        contact.setEnabled(false);
                        AssetPool.getSound("assets/sounds/kick.ogg").play();
                        if (otherKoopa.isShelled && otherKoopa.isShellMoving) {
                            die(normal.x < 0);
                        }
                    }
                    
                } else {
                    Enemy other = (Enemy)component;
                    if (isShelled && isShellMoving) {
                        other.die(normal.x > 0);
                        contact.setEnabled(false);
                        AssetPool.getSound("assets/sounds/kick.ogg").play();
                    }
                }
            }
        }
    }
}
