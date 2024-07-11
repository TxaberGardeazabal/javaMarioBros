/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import components.propertieComponents.NonPickable;
import components.propertieComponents.ShadowObj;
import gameEngine.GameObject;
import gameEngine.KeyListener;
import gameEngine.MouseListener;
import gameEngine.Window;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import render.DebugDraw;
import render.PickingTexture;
import scene.Scene;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class MouseControls extends Component{
    // object picking
    private boolean boxSelectSet = false;
    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd = new Vector2f();
    private PickingTexture pickingTexture;
    
    // object holding
    private List<GameObject> activeGameObjects = null;
    private List<Vector4f> activeGameObjectOgColor;
    GameObject holdingObject = null;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;
    
    private boolean debugDrop = false;
    
    public void pickUpObject(GameObject go) {
        // reset functionalities
        if (this.holdingObject != null) {
            this.holdingObject.destroy();
            this.activeGameObjectOgColor.clear();
        }
        if (!this.activeGameObjects.isEmpty() || this.activeGameObjectOgColor.isEmpty()) {
            clearSelected();
        }
        
        this.holdingObject = go;
        if (this.holdingObject.getComponent(SpriteRenderer.class) != null) {
            this.activeGameObjectOgColor.add(this.holdingObject.getComponent(SpriteRenderer.class).getColor());
            this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f,0.8f,0.8f,0.5f));
        }
        if (this.holdingObject.getComponent(ComplexPrefabWrapper.class) != null) {
            ComplexPrefabWrapper cpw = this.holdingObject.getComponent(ComplexPrefabWrapper.class);
            for (GameObject childGo : cpw.getGameObjects()) {
                childGo.addComponent(new NonPickable());
                childGo.addComponent(new ShadowObj());
                if (childGo.getComponent(SpriteRenderer.class) != null) {
                    this.activeGameObjectOgColor.add(childGo.getComponent(SpriteRenderer.class).getColor());
                    childGo.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f,0.8f,0.8f,0.5f));
                }

                Window.getScene().addGameObjectToScene(childGo);
            }
        }
        this.holdingObject.addComponent(new NonPickable());
        this.holdingObject.addComponent(new ShadowObj());
        Window.getScene().addGameObjectToScene(go);
    }
    
    public void placeObject() {
        if (this.holdingObject != null) {
            if (holdingObject.getComponent(ComplexPrefabWrapper.class) == null) {
                
                if (debugDrop) {
                    
                    if (holdingObject.getComponent(StateMachine.class) != null) {
                        holdingObject.getComponent(StateMachine.class).refreshTextures();
                    }
                    if (holdingObject.getComponent(SpriteRenderer.class) != null) {
                        holdingObject.getComponent(SpriteRenderer.class).setColor(this.activeGameObjectOgColor.get(0));
                    }

                    holdingObject.removeComponent(NonPickable.class);
                    holdingObject.removeComponent(ShadowObj.class);
                    Window.getScene().addGameObjectToScene(holdingObject);
                    holdingObject = null;
                } else {
                    
                    GameObject newObj = this.holdingObject.copy();
                    if (newObj.getComponent(StateMachine.class) != null) {
                        newObj.getComponent(StateMachine.class).refreshTextures();
                    }
                    if (newObj.getComponent(SpriteRenderer.class) != null) {
                        newObj.getComponent(SpriteRenderer.class).setColor(this.activeGameObjectOgColor.get(0));
                    }

                    newObj.removeComponent(NonPickable.class);
                    newObj.removeComponent(ShadowObj.class);
                    Window.getScene().addGameObjectToScene(newObj);
                }
            } else {
                if (debugDrop) {
                    ComplexPrefabWrapper cpw = this.holdingObject.getComponent(ComplexPrefabWrapper.class);
                    for (int i = 0; i < cpw.getGameObjects().size(); i++) {
                        GameObject newObj = cpw.getGameObjects().get(i);
                        if (newObj.getComponent(StateMachine.class) != null) {
                            newObj.getComponent(StateMachine.class).refreshTextures();
                        }
                        if (newObj.getComponent(SpriteRenderer.class) != null) {
                            newObj.getComponent(SpriteRenderer.class).setColor(this.activeGameObjectOgColor.get(i));
                        }
                        
                        for (GameObject child : newObj.getChildGOs()) {
                            propagateRefresh(child);
                        }

                        newObj.removeComponent(NonPickable.class);
                        newObj.removeComponent(ShadowObj.class);
                        Window.getScene().addGameObjectToScene(newObj);
                        holdingObject = null;
                    }
                } else {
                    ComplexPrefabWrapper cpw = this.holdingObject.getComponent(ComplexPrefabWrapper.class);
                    for (int i = 0; i < cpw.getGameObjects().size(); i++) {
                        GameObject newObj = cpw.getGameObjects().get(i).copy();
                        if (newObj.getComponent(StateMachine.class) != null) {
                            newObj.getComponent(StateMachine.class).refreshTextures();
                        }
                        if (newObj.getComponent(SpriteRenderer.class) != null) {
                            newObj.getComponent(SpriteRenderer.class).setColor(this.activeGameObjectOgColor.get(i));
                        }
                        
                        for (GameObject child : newObj.getChildGOs()) {
                            propagateRefresh(child);
                        }

                        newObj.removeComponent(NonPickable.class);
                        newObj.removeComponent(ShadowObj.class);
                        Window.getScene().addGameObjectToScene(newObj);
                    }
                }
            }
            
        }
    }
    
    public void propagateRefresh(GameObject go) {
        if (go.getComponent(StateMachine.class) != null) {
            go.getComponent(StateMachine.class).refreshTextures();
        }
        
        for (GameObject child : go.getChildGOs()) {
            propagateRefresh(child);
        }
    }
    
    public MouseControls(PickingTexture pickingTexture) {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectOgColor = new ArrayList<>();
    }
    
    @Override
    public void editorUpdate(float dt) {
        
        debounce -= dt;
        //pickingTexture = Window.getImGuiLayer().getPropertiesWindow().getPickingTexture();
        Scene currentScene = Window.getScene(); 
        
        if (holdingObject != null) {
            float posX = ((int) Math.floor(MouseListener.getWorldX()/ Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            float posY = ((int) Math.floor(MouseListener.getWorldY()/ Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;
            float moveX = posX - holdingObject.transform.position.x;
            float moveY = posY - holdingObject.transform.position.y;
            if (holdingObject.getComponent(ComplexPrefabWrapper.class) == null) {
                holdingObject.transform.translate(moveX, moveY);

                if (MouseListener.MouseButtonDown(Settings.EDITOR_PLACE_BLOCK)) {
                    float halfWidth = Settings.GRID_WIDTH / 2.0f;
                    float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                    if (MouseListener.isMouseDraging() &&
                            !blockInSquare(holdingObject.transform.position.x - halfWidth, holdingObject.transform.position.y - halfHeight)) {

                        
                        placeObject();
                    } else if (!MouseListener.isMouseDraging() && debounce < 0) {

                        placeObject();
                        debounce = debounceTime;
                    }
                }
                
                if (KeyListener.isKeyPressed(Settings.EDITOR_DROP_BLOCK)) {
                    holdingObject.destroy();
                    holdingObject = null;
                }
            } else {
                holdingObject.transform.translate(moveX, moveY);
                ComplexPrefabWrapper cpw = this.holdingObject.getComponent(ComplexPrefabWrapper.class);
                for (int i = 0; i < cpw.getGameObjects().size(); i++ ) {
                    cpw.getGameObjects().get(i).transform.translate(moveX, moveY);
                }
                
                if (MouseListener.MouseButtonDown(Settings.EDITOR_PLACE_BLOCK)) {
                    placeObject();
                    for (int i = 0; i < cpw.getGameObjects().size(); i++ ) {
                        cpw.getGameObjects().get(i).destroy();
                    }
                    holdingObject.destroy();
                    holdingObject = null;
                }
                
                if (KeyListener.isKeyPressed(Settings.EDITOR_DROP_BLOCK)) {
                    for (int i = 0; i < cpw.getGameObjects().size(); i++ ) {
                        cpw.getGameObjects().get(i).destroy();
                    }
                    holdingObject.destroy();
                    holdingObject = null;
                }
            }
            
              
        } else if (!MouseListener.isMouseDraging() && MouseListener.MouseButtonDown(Settings.EDITOR_PLACE_BLOCK) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int goId = pickingTexture.readPixel(x, y);
            GameObject pickedObj = currentScene.getGameObject(goId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isMouseDraging()) {
                clearSelected();
            }
            debounce = 0.2f;
        } else if (MouseListener.isMouseDraging() && MouseListener.MouseButtonDown(Settings.MOUSE_BOX_SELECT)) {
            if (!boxSelectSet) {
                clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D((new Vector2f(boxSelectStartWorld)).add(halfSize), 
                   new Vector2f(halfSize).mul(2.0f),
                    0.0f);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX = (int)boxSelectEnd.x;
            int screenEndY = (int)boxSelectEnd.y;
            
            boxSelectStart.zero();
            boxSelectEnd.zero();
            
            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }
            
            Set<Integer> uniqueGameObjectIds = findObjects(
                    new Vector2i(screenStartX,screenStartY),
                    new Vector2i(screenEndX,screenEndY),
                    pickingTexture);
            
            for (Integer objId : uniqueGameObjectIds) {
                GameObject pickedObj = Window.getScene().getGameObject(objId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                    addActiveGameObject(pickedObj);
                }
            }
            // check if only one object was valid and selected, if so dont store its color like in 
            // multiple selections, the color may change while editing
            if (this.activeGameObjects.size() == 1) {
                this.activeGameObjects.get(0)
                        .getComponent(SpriteRenderer.class)
                        .setColor(this.activeGameObjectOgColor.get(0));
                this.activeGameObjectOgColor.clear();
            }
            
        }
    }
    
    public Set<Integer> findObjects(Vector2i screenStart, Vector2i screenEnd, PickingTexture pickingTexture) {
        float[] gameObjectsIds = pickingTexture.readPixels(screenStart,screenEnd);
        // remove possible duplicates
        Set<Integer> uniqueGameObjectIds = new HashSet<>();
        for (float objId : gameObjectsIds) {
            uniqueGameObjectIds.add((int)objId);
        }
        return uniqueGameObjectIds;
    }
    
    private boolean blockInSquare(float x, float y) {
        Vector2f start = new Vector2f(x,y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf = MouseListener.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int)startScreenf.x +2,(int)startScreenf.y +2);
        Vector2i endScreen = new Vector2i((int)endScreenf.x -2,(int)endScreenf.y -2);
        Set<Integer> gameObjectIds = findObjects(startScreen, endScreen, pickingTexture);
        
        for (Integer objId : gameObjectIds) {
            
            GameObject pickedObj = Window.getScene().getGameObject(objId);
            if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                return true;
            }
            
        }
        return false;
    }
    
    public GameObject getActiveGameObject() {
        return this.activeGameObjects.size() == 1 ? this.activeGameObjects.get(0)
                : null;
    }
    
    public void clearSelected() {
        if (!this.activeGameObjectOgColor.isEmpty()) {
            int i = 0;
            for (GameObject go : activeGameObjects) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null) {
                    spr.setColor(activeGameObjectOgColor.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear();
        this.activeGameObjectOgColor.clear();
    }
    
    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    public void setActiveGameObject(GameObject go) {
        if (go != null) {
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }
    
    public void addActiveGameObject(GameObject go) {
        if (go != null) {
            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if (spr != null) {
                this.activeGameObjectOgColor.add(new Vector4f(spr.getColor()));
                spr.setColor(new Vector4f(0.8f,0.8f,0.0f,0.8f));
            } else {
                this.activeGameObjectOgColor.add(new Vector4f());
            }
            this.activeGameObjects.add(go);
        }
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }
}
