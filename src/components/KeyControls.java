/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;
import gameEngine.KeyListener;
import gameEngine.Window;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class KeyControls extends Component {
    private float debounceTime = 0.1f;
    private float debounce = 0.0f;
    private MouseControls mc;
    
    public KeyControls(MouseControls mouseControls) {
        this.mc = mouseControls;
    }
    
    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        
        GameObject activeGameObject = mc.getActiveGameObject();
        List<GameObject> activeGameObjects = mc.getActiveGameObjects();
        float multiplier = KeyListener.isKeyPressed(Settings.EDITOR_MOVE_INCREASE) ? 1.0f : 0.1f;
        
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null) {
            GameObject newGo = activeGameObject.copy();
            newGo.transform.translate(0.25f, 0.0f);
            Window.getScene().addGameObjectToScene(newGo);
            //newGo.transform.position.add(Settings.GRID_WIDTH,0.0f);
            mc.setActiveGameObject(newGo);
            if (newGo.getComponent(StateMachine.class) != null) {
                newGo.getComponent(StateMachine.class).refreshTextures();
            }
                
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) && !activeGameObjects.isEmpty()) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            mc.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                mc.addActiveGameObject(copy);
                if (copy.getComponent(StateMachine.class) != null) {
                    copy.getComponent(StateMachine.class).refreshTextures();
                }
            }
        } else if (KeyListener.isKeyPressed(Settings.EDITOR_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            mc.clearSelected();
        } else if (KeyListener.isKeyPressed(Settings.EDITOR_MOVE_BACK) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex --;
            }
        } else if (KeyListener.isKeyPressed(Settings.EDITOR_BRING_FORWARD) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex ++;
            }
        } else if (KeyListener.isKeyPressed(Settings.EDITOR_MOVE_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        } else if (KeyListener.isKeyPressed(Settings.EDITOR_MOVE_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        } else if (KeyListener.isKeyPressed(Settings.EDITOR_MOVE_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x -= Settings.GRID_WIDTH * multiplier;
            }
        } else if (KeyListener.isKeyPressed(Settings.EDITOR_MOVE_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x += Settings.GRID_WIDTH * multiplier;
            }
        }
    }
}
