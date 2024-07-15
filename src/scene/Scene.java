/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scene;

import UI.ButtonBehavior;
import components.ComponentDeserializer;
import components.Component;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.ButtonBehaviorDeserializer;
import components.propertieComponents.ShadowObj;
import gameEngine.Camera;
import gameEngine.GameObject;
import gameEngine.GameObjectDeserializer;
import gameEngine.Transform;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import render.Renderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import observers.EventSystem;
import observers.Observer;
import org.joml.Vector2f;
import physics2D.Physics2D;
import util.Settings;

/**
 * The scene class allows the creation for different levels and scenes
 * @author txaber
 */
public class Scene {

    private Renderer renderer;
    private Camera camera;
    private boolean isRunning;
    private ArrayList<GameObject> gameObjects;
    private ArrayList<GameObject> pendingObjects;
    private Physics2D physics;
    
    private SceneInitializer sceneInitializer;
    private String levelFilepath;
    
    public Scene(SceneInitializer sceneInitializer, String filepath) {
        this.sceneInitializer = sceneInitializer; 
        this.physics = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.pendingObjects = new ArrayList<>();
        this.isRunning = false;
        this.levelFilepath = filepath;
        
        EventSystem.addObserver(sceneInitializer);
    }
    
    public Physics2D getPhysics() {
        return this.physics;
    }
    
    // function is called before the first frame
    public void start() {
        
        for (int i=0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            if (go.getComponent(ShadowObj.class) != null) {
                go.destroy();
                //continue;
            }
            go.start();
            if (go.isEnabled()) {
                this.renderer.add(go);
                this.physics.add(go);
            }
        }
        isRunning=true;
        
    }
    
    public void editorUpdate(float dt) {
        this.camera.adjustProjection();
        
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            if (go.isEnabled()) {
                // add them back from the disabled state
                if (go.isDirty()) {
                    this.renderer.add(go);
                    this.physics.add(go);
                }
                go.editorUpdate(dt);
            } else {
                // remove them if disabled
                if (go.isDirty()) {
                    this.renderer.destroyGameObject(go);
                    this.physics.destroyGameObject(go);
                    // reset flag
                    go.setIsDirty(false);
                }
            }
            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics.destroyGameObject(go);
                i--;
            }
        }
        
        for (GameObject go : pendingObjects) {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
            this.physics.add(go);
        }
        pendingObjects.clear();
    }
    
    // function is called every frame
    public void update(float dt) {
        this.camera.adjustProjection();
        this.physics.update(dt);
        
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            if (go.isEnabled()) {
                // add them back from the disabled state
                if (go.isDirty()) {
                    this.renderer.add(go);
                    this.physics.add(go);
                }
                go.update(dt);
            } else {
                // remove them if disabled
                if (go.isDirty()) {
                    this.renderer.destroyGameObject(go);
                    this.physics.destroyGameObject(go);
                    // reset flag
                    go.setIsDirty(false);
                }
            }
            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics.destroyGameObject(go);
                i--;
            }
        }
        
        for (GameObject go : pendingObjects) {
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
            this.physics.add(go);
        }
        pendingObjects.clear();
        
        // ???? this causes bugs
        //DebugDraw.addBox2D(new Vector2f(50,120), new Vector2f(64,32), 0, new Vector3f(1,0,1), 1);
        //DebugDraw.addCircle2D(new Vector2f(50,400), 64, new Vector3f(0,1,0), 1);
        
    }
    
    // function is called every frame after the update function
    public void lateUpdate(float dt) {
        
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            if (go.isEnabled()) {
                // add them back from the disabled state
                if (go.isDirty()) {
                    this.renderer.add(go);
                    this.physics.add(go);
                }
                go.lateUpdate(dt);
            } else {
                // remove them if disabled
                if (go.isDirty()) {
                    this.renderer.destroyGameObject(go);
                    this.physics.destroyGameObject(go);
                    // reset flag
                    go.setIsDirty(false);
                }
            }
            if (go.isDead()) {
                gameObjects.remove(i);
                this.renderer.destroyGameObject(go);
                this.physics.destroyGameObject(go);
                i--;
            }
        }
    }
    
    public void render() {
        this.renderer.render();
    }
    
    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            pendingObjects.add(go);
        }
        for(GameObject child : go.getChildGOs()) {
            addGameObjectToScene(child);
        }
    }
    
    public void Destroy() {
        // remove the observer from the observer list
        EventSystem.removeObserver(this.sceneInitializer);
        
        for (GameObject go : gameObjects) {
            go.destroy();
        }
    }
    
    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }
    
    public GameObject getGameObject(int goId) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUid() == goId).findFirst();
        return result.orElse(null);
    }
    
    public GameObject getGameObjectByName(String name) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.name.equals(name)).findFirst();
        return result.orElse(null);
    }
    
    public <T extends Component> GameObject getGameObjectWith(Class<T> CClass) {
        for (GameObject go : gameObjects) {
            if (go.getComponent(CClass) != null) {
                return go;
            }
        }
        return null;
    }
    
    public String getLevelFilepath() {
        return this.levelFilepath;
    }
    
    public String getRelativeLevelFilepath() {
        return Settings.getRelativePath(this.levelFilepath);
    }
    
    public void changeLevelFilepath(String newFilepath) {
        this.levelFilepath = newFilepath;
    }
    
    public void init() {
        this.camera = new Camera(new Vector2f(0,0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }
    
    public Camera camera() {
        return this.camera;
    }

    public void imGui(){
        this.sceneInitializer.imGui();
    }
    
    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }
    
    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(ButtonBehavior.class, new ButtonBehaviorDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        
        try {
            String[] temp = levelFilepath.split("/");
            String levelName = temp[temp.length-1];
            
            FileWriter fe = new FileWriter(levelName);
            ArrayList<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj : this.gameObjects) {
                if (obj.getParent() == null) {
                    if (obj.doSerialization()) {
                        objsToSerialize.add(obj);
                    }
                }

            }
            fe.write(gson.toJson(objsToSerialize));
            fe.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private transient int maxGoId = -1;
    private transient int maxCompId = -1;
    public void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(ButtonBehavior.class, new ButtonBehaviorDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        
        String inFile = "";
        try {
            String[] temp = levelFilepath.split("/");
            String levelName = temp[temp.length-1];
            
            inFile = new String(Files.readAllBytes(Paths.get(levelName)));
        } catch (IOException ex ) {
            // what to do?
            /*System.out.println("got here?");
            File file = new File("assets/levels/" + levelName);
            try {
                if (!file.exists()) {

                    file.createNewFile();
                    
                }
                inFile = new String(Files.readAllBytes(Paths.get(levelName)));
            } catch (IOException ex1) {
                assert false: "ERROR while loading level: couldnt create file "+file.getAbsolutePath();
            }*/
            
            //ex.printStackTrace();
        }
        
        if (!inFile.equals("")) {
            maxGoId = -1;
            maxCompId = -1;
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (GameObject obj: objs) {
                addGameObjectToScene(obj);
                loadObj(obj);
            }
            
            maxGoId++;
            GameObject.init(maxGoId);
            maxCompId++;
            Component.init(maxCompId);
            System.out.println("max object id: "+maxGoId+" max component id: "+maxCompId);
        }
        
    }
    
    private void loadObj(GameObject obj) {
        for (Component c: obj.getAllComponents()) {
            if (c.getUid() > maxCompId) {
                maxCompId = c.getUid();
            }
        }
        if (obj.getUid() > maxGoId) {
            maxGoId = obj.getUid();
        }
        
        if (!obj.getChildGOs().isEmpty()) {
            for (GameObject child : obj.getChildGOs()) {
                loadObj(child);
                
                child.setParent(obj);
            }
        }
    }

    public Observer getInitializer() {
        return this.sceneInitializer;
    }
}
