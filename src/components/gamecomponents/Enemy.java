/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.PhysicsController;
import components.StateMachine;
import editor.ConsoleWindow;
import gameEngine.Camera;
import gameEngine.GameObject;
import gameEngine.Window;
import java.util.List;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.enums.BodyType;
import util.AssetPool;
import util.Settings;

/**
 * Funcionalidades basicas que comparten todos los enemigos del juego
 * @author txaber gardeazabal
 */
public class Enemy extends PhysicsController{
    protected transient boolean goingRight = false;
    protected transient float walkSpeed = 0.6f;
    protected transient boolean isDead = false;
    protected transient boolean inCamera = false;
    protected transient StateMachine stateMachine;
    
    protected float sizeX = Settings.GRID_WIDTH;
    protected float sizeY = Settings.GRID_HEIGHT;
    
    @Override
    public void start() {
        super.start();
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine == null) {
            ConsoleWindow.addLog("couldn't find statemachine in enemy "+gameObject.name,
                    ConsoleWindow.LogCategory.warning);
        }
    }
    
    @Override
    public void update(float dt) {
        if (isDead) {
            addGravity();
            applyForces(dt);
        }
        Camera camera = Window.getScene().camera();
        
        inCamera = !(this.gameObject.transform.position.x - (sizeX / 2) > 
                camera.position.x + camera.getProjectionSize().x * camera.getZoom() ||
                this.gameObject.transform.position.x  + (sizeX / 2) < 
                camera.position.x * camera.getZoom() ||
                this.gameObject.transform.position.y - (sizeY / 2) > 
                camera.position.y + camera.getProjectionSize().y * camera.getZoom() ||
                this.gameObject.transform.position.y + (sizeY / 2) <
                camera.position.y * camera.getZoom());
    }
    
    /**
     * Funcion que se llama cuando mario pisa al enemigo por encima.
     */
    public void stomp() {}
    
    /**
     * Funcion que se llama cuando es golpeado por una bola de fuego lanzado por mario.
     * por defecto el enemigo muere
     * @param direction normal por donde ha sido golpeado
     */
    public void fireballHit(Vector2f direction) {
        die(direction.x < 0);
    }
    
    /**
     * Mata al enemigo, llamado cuando deberia morir.
     * por defecto inverte su sprite y añade velocidad hacia la izquierda de la pantalla
     */
    public void die() {
        die(true);
    }
    
    /**
     * Mata al enemigo, llamado cuando deberia morir.
     * por defecto inverte su sprite y añade velocidad hacia un lado de la pantalla
     * @param hitRight si fue golpeado por la derecha, sera lanzado para la izquierda, lo contrario si es false
     */
    public void die(boolean hitRight) {
        AssetPool.getSound("assets/sounds/kick.ogg").play();
        isDead = true;
        gameObject.transform.scale.y = -gameObject.transform.scale.y;
        rb.setIsSensor(true);
        rb.setBodyType(BodyType.Dynamic);
        rb.reset();
        
        if (hitRight) {
            acceleration.set(new Vector2f(0, 2f));
            velocity.set(new Vector2f(1,1.6f));
        } else {
            acceleration.set(new Vector2f(0, 2f));
            velocity.set(new Vector2f(-1,1.6f));
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
                    if (!playerController.isStarInvincible() && normal.y > 0.58f) {
                        playerController.enemyBounce();
                        playerController.stompIncrement();
                        stomp();
                    } else if (!playerController.isInvincible()){
                        playerController.hurt();
                    } else if (playerController.isStarInvincible()) {
                        die(normal.x < 0);
                    }
                    contact.setEnabled(false);
                }
                
            } else if (component instanceof Fireball) {
                Fireball f = (Fireball) component;
                f.delete();
                fireballHit(normal);
            }
        }
        
    }
    
    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f normal) {
        if (isDead) {
            return;
        }
        
        // cambiar direccion
        if (Math.abs(normal.y) < 0.1f) {
            goingRight = normal.x < 0;
        }
    }
}
