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
import gameEngine.PrefabSave;
import gameEngine.Window;
import imgui.ImGui;
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
 * Componente que controla las acciones que hacen los inputs del teclado.
 * Esta clase se usa solo en el editor y todas sus funcionalidades no llegan fuera de este.
 * 
 * @see la implementacion de esta clase y keyControlls no es perfecta, me gustaria haberlo hecho de otra forma
 * como tener esta clase en la propia escena en vez de añadirlo al nivel como componente,
 * pero he llegado lejos como esta y no me interesa cambiarlo.
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
        
        /*if (ImGui.beginPopupContextWindow("Prefab save")) {
            if (ImGui.menuItem("create prefab")) {
                //saveAsPrefab();
            }
            ImGui.endPopup();
        }*/
    }
    
    /*
        agarrar y soltar objetos (drag and drop) del editor
    */
    
    /**
     * "agarra" un gameobject (o un GO agrupado) que pasas. un gameobject "agarrado" sigue la posicion del raton y se 
     * muestra en un color diferente para contrastar con otros gameobjects no agarrados, esto se mantiene
     * hasta que el gameobject sea "soltado", mientras el gameobject puede ser movido libremente en la pantalla de juego.
     * Si ya existia un gameobject "agarrado" este se borra.
     * 
     * @param go la referencia del gameobject
     */
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
    
    /**
     * "suelta" el gameobject (o GO agrupado)" pegado al cursor. Esto se realiza haciendo una copia del objeto
     * en el lugar del cursor con lo cual no sera el original, asi se pueden "colocar" multiples objetos parecidos en el nivel.
     * Para retirar el gameobject "agarrado" usa la funcion destroyHoldingObject()
     */
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
    
    /**
     * Borra el gameobject que estaba pegado al cursor
     */
    public void destroyHoldingObject() {
        if (holdingObject != null) {
            holdingObject.destroy();
            holdingObject = null;
        }
    }
    
    /**
     * Funcion "helper" para refrescar las texturas de un gameobject y todos sus hijos.
     * @param go el gameobject en cuestion
     */
    private void propagateRefresh(GameObject go) {
        if (go.getComponent(StateMachine.class) != null) {
            go.getComponent(StateMachine.class).refreshTextures();
        }
        
        for (GameObject child : go.getChildGOs()) {
            propagateRefresh(child);
        }
    }
    
    /*
        Seleciconar objetos activos para su edicion en grupo
    */
    
    /**
     * Busca las referencias de los objetos que estan dentro de una caja cogiendo datos desde el shader
     * @param screenStart la posicion superior izquierda de la caja
     * @param screenEnd la posicion inferior derecha de la caja
     * @param pickingTexture imagen grafica con la informacion a buscar
     * @return la lista con ids unicas de todos los objetos dentro de la caja
     */
    public Set<Integer> findObjects(Vector2i screenStart, Vector2i screenEnd, PickingTexture pickingTexture) {
        float[] gameObjectsIds = pickingTexture.readPixels(screenStart,screenEnd);
        // remove possible duplicates
        Set<Integer> uniqueGameObjectIds = new HashSet<>();
        for (float objId : gameObjectsIds) {
            uniqueGameObjectIds.add((int)objId);
        }
        return uniqueGameObjectIds;
    }
    
    /**
     * Busca si existe un gameobject entre la posicion definida y los bordes de un celda del grid
     * @param x pos X
     * @param y pos Y
     * @return true si encuentra un gameobject, false de lo contrario
     */
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
    
    /**
     * Devuelve el objeto que esta seleccionado, pero solo si es uno
     * @return el gameobject seleccionado, null si es indefinido o si hay multiples GOs seleccionados
     */
    public GameObject getActiveGameObject() {
        return this.activeGameObjects.size() == 1 ? this.activeGameObjects.get(0)
                : null;
    }
    
    /**
     * Libera la seleccion de objetos y los restaura a su estado original
     */
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
    
    /**
     * Devuelve los objetos ahora mismo seleccionados
     * @return la lista con los objetos seleccionados
     */
    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    /**
     * asigna un gameobject como seleccionado
     * @param go el gameobject a seleccionar
     */
    public void setActiveGameObject(GameObject go) {
        if (go != null) {
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }
    
    /**
     * Añade un gameobject a la lista de seleccionados, se puede usar para asignar uno individual o añadir al grupo
     * @param go el gameobject a añadir
     */
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
    
    /*private void saveAsPrefab() {
        PrefabSave x = new PrefabSave("assets/prefabs/test.txt");
        x.setPrefab(this.holdingObject);
        x.save();
    }*/
}
