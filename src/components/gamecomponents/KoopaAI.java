/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.propertieComponents.Ground;
import gameEngine.GameObject;
import gameEngine.Window;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2D.RaycastInfo;
import render.DebugDraw;
import util.AssetPool;
import util.Settings;

/**
 * Controllador del enemigo koopa verde
 * @author txaber gardeazabal
 */
public class KoopaAI extends Enemy {
    public transient boolean isShelled = false;
    public transient boolean isShellMoving = false;
    private float movingDebounce = 0.32f;
    private float stompDebounce = 0.32f;
    
    private boolean fallsOnEdges = true;
    private transient float fallDebounce = 0;
    private transient int hitCombo = 0;
    
    @Override
    public void update(float dt) {
        super.update(dt);
        if (isDead || !inCamera) {
            return;
        }

        movingDebounce -= dt;
        stompDebounce -= dt;
        if (fallDebounce > 0) {
            fallDebounce -= dt;
        }
        
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
                fallsOnEdges = true;
                walkSpeed *= 3.0f;
                this.stateMachine.trigger("squash");
                stopAllForces();
            } else {
                if (isShellMoving) {
                    isShellMoving = false;
                    stopAllForces();
                    AssetPool.getSound("assets/sounds/kick.ogg").play();
                    hitCombo = 0;
                } else {
                    isShellMoving = true;
                    AssetPool.getSound("assets/sounds/kick.ogg").play();
                }
            }
        }
    }
    
    @Override
    public void die(boolean hitRight) {
        Map payload = new HashMap<>();
        payload.put("points", "200");
        EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));
        super.die(hitRight);
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
                            playerController.stompIncrement();
                            
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
                                playerController.stompIncrement();
                                stomp();
                                goingRight = normal.x < 0;
                            } else if (!isShellMoving) {
                                stomp();
                                goingRight = normal.x < 0;
                                movingDebounce = 0.32f;
                                
                                Map payload = new HashMap<>();
                                payload.put("points", "400");
                                EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));
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
                        hitIncrement();
                    }
                    
                } else {
                    Enemy other = (Enemy)component;
                    if (isShelled && isShellMoving) {
                        other.die(normal.x > 0);
                        contact.setEnabled(false);
                        AssetPool.getSound("assets/sounds/kick.ogg").play();
                        hitIncrement();
                    }
                }
                
                
            }
        }
    }
    
    @Override
    public boolean checkOnGround() {
        Vector2f raycastBegin = new Vector2f(this.gameObject.transform.position);
        raycastBegin.sub(innerWidth / 2.0f, 0.0f);
        Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, castVal);
        
        RaycastInfo info = Window.getPhysics().raycast(this.gameObject, raycastBegin, raycastEnd);
        
        Vector2f raycast2Begin = new Vector2f(raycastBegin).add(innerWidth, 0.0f);
        Vector2f raycast2End = new Vector2f(raycastEnd).add(innerWidth, 0.0f);
        
        RaycastInfo info2 = Window.getPhysics().raycast(this.gameObject, raycast2Begin, raycast2End);
        
        if (Settings.mGroundRaycast) {
            DebugDraw.addLine2D(raycastBegin, raycastEnd, new Vector3f(1,0,0));
         DebugDraw.addLine2D(raycast2Begin, raycast2End, new Vector3f(1,0,0));
        }
        
        if (!fallsOnEdges) {
            // XOR 
            if (((info.hit && info.hitObj != null && info.hitObj.getComponent(Ground.class) != null) ^
                (info2.hit && info2.hitObj != null && info2.hitObj.getComponent(Ground.class) != null))
                    && fallDebounce <= 0) {
                goingRight = !goingRight;
                fallDebounce = 0.4f;
            }
        }
        
        onGround = (info.hit && info.hitObj != null && info.hitObj.getComponent(Ground.class) != null)
                || (info2.hit && info2.hitObj != null && info2.hitObj.getComponent(Ground.class) != null);
        
        return onGround;
    }

    public void setFallsOnEdges(boolean fallsOnEdges) {
        this.fallsOnEdges = fallsOnEdges;
    }
    
    private void hitIncrement() {
        Map payload = new HashMap<>();
        
        switch(hitCombo) {
            case 0:
                payload.put("points", "500");
                break;
            case 1:
                payload.put("points", "800");
                break;
            case 2:
                payload.put("points", "1000");
                break;
            case 3:
                payload.put("points", "2000");
                break;
            case 4:
                payload.put("points", "4000");
                break;
            case 5:
                payload.put("points", "5000");
                break;
            case 6:
                payload.put("points", "8000");
                break;
            case 7:
                payload.put("points", "0");
                EventSystem.notify(this.gameObject, new Event(EventType.OneUp));
                hitCombo--;
                break;
            default:
                payload.put("points", "0");
                break;
        }
        hitCombo++;
        EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));
    }
}
