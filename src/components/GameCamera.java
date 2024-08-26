/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import components.gamecomponents.PlayerController;
import gameEngine.Camera;
import gameEngine.GameObject;
import gameEngine.Window;
import org.joml.Vector4f;

/**
 * Camara en la vista del juego.
 * La camara sigue a mario a traves del nivel hasta su fin,
 * la posicion Y es fija pero cambia cuando mario entra en una tuberia
 * la camara nunca se movera a la izquierda, pero igual cambia en el futuro
 * @author txaber gardeazabal
 */
public class GameCamera extends Component{
    private transient GameObject player;
    private transient Camera camera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float underGroundYLevel = 0.0f;
    private transient float cameraOffset = 2.25f;
    private transient float playerOffset = 0.75f;
    
    private transient float Dx = 0.0f;
    private transient float Dy = 0.0f;
    
    // TODO this should be stored in the level itself, not here
    private Vector4f skyColor = new Vector4f(92.0f/255.0f, 148.0f/255.0f, 252.0f/255.0f, 1.0f);
    private Vector4f underGroundColor = new Vector4f(0.0f,0.0f,0.0f,0.0f);
    
    public GameCamera(Camera camera) {
        this.camera = camera;
    }
    
    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWith(PlayerController.class);
        this.camera.clearColor.set(skyColor);
        this.underGroundYLevel = this.camera.position.y - 
                this.camera.getProjectionSize().y - this.cameraOffset;
    }
    
    @Override
    public void update(float dt) {
        if (player == null) {
            
            this.camera.clearColor.set(skyColor);
        } else {
            if (player.getComponent(PlayerController.class) != null) {
                if (!player.getComponent(PlayerController.class).hasWon()) {
                    // mario solo se mueve a la derecha de la pantalla
                    Dx = -(camera.position.x - Math.max(player.transform.position.x -2.5f, highestX));
                    camera.position.x = Math.max(player.transform.position.x -2.5f, highestX);
                    highestX = Math.max(highestX, camera.position.x);

                    if (player.transform.position.y < -playerOffset) {
                        Dy = -(this.camera.position.y - underGroundYLevel);
                        this.camera.position.y = underGroundYLevel;
                        this.camera.clearColor.set(underGroundColor);
                    } else if (player.transform.position.y >= 0.0f) {
                        Dy = -(this.camera.position.y - 0.0f);
                        this.camera.position.y = 0.0f;
                        this.camera.clearColor.set(skyColor);
                    }
                }
            }
        }
        
    }

    public float getDeltaX() {
        return Dx;
    }

    public float getDeltaY() {
        return Dy;
    }
    
    
}
