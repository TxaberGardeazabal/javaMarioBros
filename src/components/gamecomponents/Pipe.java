/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.TransitionMachine;
import components.TranslateTransition;
import gameEngine.Direction;
import gameEngine.GameObject;
import gameEngine.KeyListener;
import gameEngine.Prefab;
import gameEngine.PrefabSave;
import gameEngine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.DebugDraw;
import util.AssetPool;
import util.Settings;

/**
 * Tuberias pasadizas que trasportan a mario a otras zonas
 * @author txaber gardeazabal
 */
public class Pipe extends Component {
    public Direction direction;
    private String connectingPipeName = "";
    private boolean isTraversable = false;
    private boolean hasPiranhaPlant = false;
    
    private transient GameObject connectingPipe = null;
    private transient float entranceVectorTolerance = 0.7f;
    private transient PlayerController collidingPlayer = null;
    private transient boolean playerEntering = false;
    private transient boolean playerExiting = false;
    private transient boolean spawnFlag = false;

    public Pipe(Direction direction) {
        this.direction = direction;
    }
    
    @Override
    public void start () {
        connectingPipe = Window.getScene().getGameObjectByName(connectingPipeName);
    }
    
    @Override
    public void update(float dt) {
        if (!spawnFlag && hasPiranhaPlant) {
            Vector2f entranceOffset = new Vector2f();
            int rotation = 0;
            Vector2f translateV = new Vector2f();
            
            switch (direction) {
                case Up:
                    entranceOffset = new Vector2f(0,0.4f);
                    translateV = new Vector2f(this.gameObject.transform.position);
                    break;
                case Down:
                    entranceOffset = new Vector2f(0,-0.4f);
                    rotation = 180;
                    translateV = new Vector2f(this.gameObject.transform.position);
                    break;
                case Left:
                    entranceOffset = new Vector2f(-0.4f,0);
                    rotation = 90;
                    translateV = new Vector2f(this.gameObject.transform.position);
                    break;
                case Right:
                    entranceOffset = new Vector2f(0.4f,0);
                    rotation = -90;
                    translateV = new Vector2f(this.gameObject.transform.position);
                    break;
            }

            //GameObject plant = Prefab.generatePiranhaPlant();
            PrefabSave plantPre = new PrefabSave("assets/prefabs/entities/piranhaPlantOverworld.prefab");
            GameObject plant = plantPre.load();
            if (plant != null) {
                plant.transform.setPosition(new Vector2f(gameObject.transform.position).add(entranceOffset));
                plant.transform.rotate(rotation);

                TranslateTransition move1 = new TranslateTransition(new Vector2f(entranceOffset).negate(), 1);
                TranslateTransition move2 = new TranslateTransition(entranceOffset, 1);
                TransitionMachine planttm = new TransitionMachine(false,false);
                planttm.addTransition(move1);
                planttm.addTransition(move2);
                plant.addComponent(planttm);

                Window.getScene().addGameObjectToScene(plant);
            }
            spawnFlag = true;
        }
        
        if (connectingPipe == null) {
            return;
        }
        
        if (playerEntering != false) {
            if (!collidingPlayer.getTransitionMachine().isPlaying()) {
                playerEntering = false;
                playerExiting = true;
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                if (connectingPipe.getComponent(Pipe.class) != null) {
                    collidingPlayer.setPosition(getPlayerPosition(connectingPipe));
                    collidingPlayer.playPipeExitAnimation(connectingPipe.getComponent(Pipe.class).direction);
                } else {
                    collidingPlayer.playPipeExitAnimation(direction);
                }
                
            }
        } else if (playerExiting != false) {
            if (!collidingPlayer.getTransitionMachine().isPlaying()) {
                playerExiting = false;
                collidingPlayer.setDisableForces(false);
                collidingPlayer = null;
                // transition end
            }
        } else if (isTraversable && collidingPlayer != null) {
            playerEntering = false;
            
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
                collidingPlayer.setDisableForces(true);
                collidingPlayer.playPipeEnterAnimation(direction);
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
        if (playerController != null && !(playerEntering || playerExiting)) {
            collidingPlayer = null;
        }
    }

    /**
     * Devuelve la posicion donde mario debe aparecer en la tuberia destino.
     * @param pipe el gameobject con el componente de la tuberia destino
     * @return la posicion encima de la boca de la tuberia, si no se encuentra el componente devuelve un vector vacio.
     */
    private Vector2f getPlayerPosition(GameObject pipe) {
        return new Vector2f(pipe.transform.position);
    }
    
    /**
     * Comprueba si mario esta sobre la entrada de la tuberia
     * @return true si esta sobre la entrada de la tuberia, da igual la direccion, false de lo contrario
     */
    public boolean playerAtEntrance() {
        if (collidingPlayer == null) {
            return false;
        }
        
        /*
            find the boundaries of the pipe and the player to find if the player is in the pipe entrance,
            we add a tiny offset to the pipe so it doesn't require perfect positioning, it's still a bit weird but I don't care
        */
        Vector2f min = new Vector2f(gameObject.transform.position).sub(new Vector2f(gameObject.transform.scale).mul(0.5f)).add(0.025f,0.025f);
        Vector2f max = new Vector2f(gameObject.transform.position).add(new Vector2f(gameObject.transform.scale).mul(0.5f)).sub(0.025f,0.025f);
        Vector2f playerMin = new Vector2f(collidingPlayer.gameObject.transform.position).sub(new Vector2f(collidingPlayer.gameObject.transform.scale).mul(0.5f));
        Vector2f playerMax = new Vector2f(collidingPlayer.gameObject.transform.position).add(new Vector2f(collidingPlayer.gameObject.transform.scale).mul(0.5f));
        
        if (Settings.mPipeBorders) {
            Vector2f dimen = new Vector2f(max).sub(min);
            DebugDraw.addBox2D(new Vector2f(gameObject.transform.position), dimen, 0, new Vector3f(0,0.5f,1), 50);
            Vector2f dimen2 = new Vector2f(playerMax).sub(playerMin);
            DebugDraw.addBox2D(new Vector2f(collidingPlayer.gameObject.transform.position), dimen2, 0, new Vector3f(0,0.5f,1), 50);
        }
        
        switch(direction) {
            case Up:
                return playerMin.y >= max.y &&
                        playerMin.x >= min.x &&
                        playerMax.x <= max.x;
            case Left:
                boolean a = playerMax.x <= min.x &&
                        playerMin.y >= min.y &&
                        playerMax.y <= max.y;
                return a;
            case Down:
                return playerMax.y <= min.y &&
                        playerMin.x >= min.x &&
                        playerMax.x <= max.x;
            case Right:
                return playerMin.x >= max.x &&
                        playerMin.y >= min.y &&
                        playerMax.y <= max.y;
        }
        return false;
    }
}
