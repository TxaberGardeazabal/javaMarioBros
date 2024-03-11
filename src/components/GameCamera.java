/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import components.gamecomponents.PlayerController;
import components.Component;
import gameEngine.Camera;
import gameEngine.GameObject;
import gameEngine.Window;
import org.joml.Vector4f;

/**
 *
 * @author txaber gardeazabal
 */
public class GameCamera extends Component{
    private transient GameObject player;
    private transient Camera camera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float underGroundYLevel = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;
    
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
                this.camera.getProjectionSize().y - this.cameraBuffer;
    }
    
    @Override
    public void update(float dt) {
        if (player == null) {
            
            this.camera.clearColor.set(skyColor);
        } else {
            if (player.getComponent(PlayerController.class) != null) {
                if (!player.getComponent(PlayerController.class).hasWon()) {
                    //System.out.println("should begin the camera");
                    camera.position.x = Math.max(player.transform.position.x -2.5f, highestX);
                    highestX = Math.max(highestX, camera.position.x);

                    if (player.transform.position.y < -playerBuffer) {
                        this.camera.position.y = underGroundYLevel;
                        this.camera.clearColor.set(underGroundColor);
                    } else if (player.transform.position.y >= 0.0f) {
                        this.camera.position.y = 0.0f;
                        this.camera.clearColor.set(skyColor);
                    }
                }
            }
        }
        
    }
}
