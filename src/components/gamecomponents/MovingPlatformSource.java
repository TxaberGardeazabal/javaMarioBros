/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import editor.ConsoleWindow;
import gameEngine.Direction;
import gameEngine.GameObject;
import gameEngine.PrefabSave;
import gameEngine.Window;

/**
 *
 * @author txaber gardeazabal
 */
public class MovingPlatformSource extends Component{
    
    private String platformPrefab;
    private Direction direction = Direction.Left;
    private float distance = 2;
    private float duration = 2.5f;
    private float spawnFrecuency = 3;
    
    private transient float spawnTimer = spawnFrecuency;
    private transient GameObject platform;

    public MovingPlatformSource(String platformPrefab) {
        this.platformPrefab = platformPrefab;
    }
    
    @Override
    public void start() {
        if (platformPrefab != null || !platformPrefab.equals("")) {
            PrefabSave pre = new PrefabSave(platformPrefab);
            platform = pre.load();
        } else {
            ConsoleWindow.addLog("El archivo con la plataforma esta vacio o no existe", ConsoleWindow.LogCategory.warning);
        }
    }
    
    @Override
    public void update(float dt) {
        if (platform == null) {
            return;
        }
        
        spawnTimer -= dt;
        if (spawnTimer <= 0) {
            GameObject platformCopy = platform.copy();
            platformCopy.transform.setPosition(this.gameObject.transform.position);
            MovingPlatform controller = platformCopy.getComponent(MovingPlatform.class);
            
            controller.setDirection(direction);
            controller.setDistance(distance);
            controller.setDuration(duration);
            controller.setLoops(false);
            controller.setDestroyOnEnd(true);
            controller.setLinear(true);
            Window.getScene().addGameObjectToScene(platformCopy);
            spawnTimer = spawnFrecuency;
        }
    }

    public String getPlatformPrefab() {
        return platformPrefab;
    }

    public void setPlatformPrefab(String platformPrefab) {
        this.platformPrefab = platformPrefab;
    }
}
