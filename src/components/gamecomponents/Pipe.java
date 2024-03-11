/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import gameEngine.Direction;
import gameEngine.GameObject;
import gameEngine.KeyListener;
import gameEngine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import util.AssetPool;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class Pipe extends Component {
    private Direction direction;
    private String connectingPipeName ="";
    private boolean isTraversable = false;
    private transient GameObject connectingPipe = null;
    private transient float entranceVectorTolerance = 0.6f;
    private transient PlayerController collidingPlayer = null;

    public Pipe(Direction direction) {
        this.direction = direction;
    }
    
    @Override
    public void start () {
        connectingPipe = Window.getScene().getGameObjectByName(connectingPipeName);
    }
    
    @Override
    public void update(float dt) {
        if (connectingPipe == null) {
            return;
        }
        
        if (isTraversable && collidingPlayer != null) {
            boolean playerEntering = false;
            
            switch(direction) {
                case Up:
                    if (KeyListener.isKeyPressed(Settings.ENTERPIPEDOWN)
                            && isTraversable
                            && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case Left:
                    if (KeyListener.isKeyPressed(Settings.ENTERPIPERIGHT)
                            && isTraversable
                            && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case Down:
                    if (KeyListener.isKeyPressed(Settings.ENTERPIPEUP)
                            && isTraversable
                            && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case Right:
                    if (KeyListener.isKeyPressed(Settings.ENTERPIPELEFT)
                            && isTraversable
                            && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
            }
            if (playerEntering) {
                collidingPlayer.setPosition(getPlayerPosition(connectingPipe));
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                    
            }
        }
    }
    
    @Override
    public void beginCollision(GameObject go, Contact cntc, Vector2f normal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null) {
            collidingPlayer = playerController;
        }
    }
    
    @Override
    public void endCollision(GameObject go, Contact cntc, Vector2f normal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (playerController != null) {
            collidingPlayer = null;
        }
    }

    
    private Vector2f getPlayerPosition(GameObject pipe) {
        Pipe pipeComponent = pipe.getComponent(Pipe.class);
        if (pipeComponent != null) {
            switch(pipeComponent.direction) {
                case Up:
                    return new Vector2f(pipe.transform.position).add(0.0f, 0.5f);
                case Left:
                    return new Vector2f(pipe.transform.position).add(-0.5f, 0.0f);
                case Down:
                    return new Vector2f(pipe.transform.position).add(0.5f, 0.0f);
                case Right:
                    return new Vector2f(pipe.transform.position).add(0.0f, -0.5f);
            }
        }
        return new Vector2f();
        
    }
    
    public boolean playerAtEntrance() {
        if (collidingPlayer == null) {
            return false;
        }
        
        Vector2f min = new Vector2f(gameObject.transform.position).sub(new Vector2f(gameObject.transform.scale).mul(0.5f));
        Vector2f max = new Vector2f(gameObject.transform.position).add(new Vector2f(gameObject.transform.scale).mul(0.5f));
        Vector2f playerMin = new Vector2f(collidingPlayer.gameObject.transform.position).sub(new Vector2f(collidingPlayer.gameObject.transform.scale).mul(0.5f));
        Vector2f playerMax = new Vector2f(collidingPlayer.gameObject.transform.position).add(new Vector2f(collidingPlayer.gameObject.transform.scale).mul(0.5f));
        
        
        switch(direction) {
            case Up:
                return playerMin.y >= max.y &&
                        playerMax.x >= min.x &&
                        playerMax.x <= max.x;
            case Left:
                return playerMin.x >= max.x &&
                        playerMax.y >= min.y &&
                        playerMin.y <= max.y;
            case Down:
                return playerMax.y <= min.y &&
                        playerMax.x >= min.x &&
                        playerMax.x <= max.x;
            case Right:
                return playerMax.x <= min.x &&
                        playerMax.y >= min.y &&
                        playerMin.y <= max.y;
        }
        return false;
    }
}
