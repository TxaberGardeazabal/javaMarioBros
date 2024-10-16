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
import components.GameCamera;
import components.TransitionState;
import components.TransitionStateDeserializer;
import components.gamecomponents.HoleLogic;
import components.gamecomponents.PlayerController;
import components.propertieComponents.ShadowObj;
import editor.ConsoleWindow;
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
 * Clase con el que se crean y manejan diferentes niveles.
 * Esta clase engloba todas las partes principales del juego, como la camara, renderizado, todos los objetos del mundo,
 * fisicas y cargado/guardado de niveles. Se puede añadir nuevas funcionalidades con un sceneinitializer
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
    
    /**
     * Funcion que se ejecuta antes del primer frame
     */
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
    
    /**
     * Funcion que se ejecuta cada frame
     * @param dt tiempo en segundos desde el anterior frame
     */
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
    }
    
    /**
     * Funcion que se ejecuta cada frame despues de la funcion update
     * @param dt tiempo en segundos desde el anterior frame
     */
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
    
    /**
     * Funcion que se ejecuta cada frame solo en el editor
     * @param dt tiempo en segundos desde el anterior frame
     */
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
    
    /**
     * renderiza todos los gameobjects en pantalla
     */
    public void render() {
        this.renderer.render();
    }
    
    /**
     * Añade el objeto creado a la escena.
     * si la escena estaba en marcha al llamar esta funcion el gameobject se añade cuando el motor de fisicas lo permita.
     * @param go un gameobject nuevo
     */
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
    
    /**
     * Elmina esta escena y libera el espacio usado.
     * Si se estaba editando la escena y se desea guardar los cambios llama la funcion save antes de este
     */
    public void Destroy() {
        // remove the observer from the observer list
        EventSystem.removeObserver(this.sceneInitializer);
        
        for (GameObject go : gameObjects) {
            go.destroy();
        }
    }
    
    /**
     * 
     * @return todos los gameobjects que estan en la escena, en el orden donde fueron añadidos
     */
    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }
    
    /**
     * Busca un gameobject con un id unico.
     * @param goId la id del objeto a buscar
     * @return la primera instancia de gameobject con el id, null si no se encuentra ninguno
     */
    public GameObject getGameObject(int goId) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUid() == goId).findFirst();
        return result.orElse(null);
    }
    
    /**
     * Busca un gameobject con un nombre.
     * @param name el nombre del objeto a buscar
     * @return la primera instancia de gameobject con el nombre que encuentra, null si no se encuentra ninguno
     */
    public GameObject getGameObjectByName(String name) {
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.name.equals(name)).findFirst();
        return result.orElse(null);
    }
    
    /**
     * Busca un gameobject con un componente especifico.
     * @param <T> cualquier Component
     * @param CClass referencia a la clase del componente
     * @return la primera y solo la primera instancia de gameobject con el componente que encuentra, null si no se encuentra ninguno
     */
    public <T extends Component> GameObject getGameObjectWith(Class<T> CClass) {
        for (GameObject go : gameObjects) {
            if (go.getComponent(CClass) != null) {
                return go;
            }
        }
        return null;
    }
    
    /**
     * 
     * @return la ruta absoluta del archivo de nivel
     */
    public String getLevelFilepath() {
        return this.levelFilepath;
    }
    
    /**
     * 
     * @return la ruta relativa del archivo de nivel
     */
    public String getRelativeLevelFilepath() {
        return Settings.getRelativePath(this.levelFilepath);
    }
    
    /**
     * Asigna un archivo de nivel nuevo a esta escena 
     * @param newFilepath ruta absoluta del nuevo archivo
     */
    public void changeLevelFilepath(String newFilepath) {
        this.levelFilepath = newFilepath;
    }
    
    /**
     * Inicializa esta escena y carga el nivel
     */
    public void init() {
        this.camera = new Camera(new Vector2f(0,0));
        this.sceneInitializer.loadResources(this);
        this.sceneInitializer.init(this);
    }
    
    public Camera camera() {
        return this.camera;
    }

    /**
     * Ejecuta el codigo imgui del initializer en esta escena
     */
    public void imGui(){
        this.sceneInitializer.imGui();
    }
    
    /**
     * Crea un nuevo GameObject
     * @param name nombre del nuevo objeto
     * @return un gameobject inicializado
     */
    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }
    
    /**
     * Guarda todo el nivel en el estado actual en el archivo
     */
    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(ButtonBehavior.class, new ButtonBehaviorDeserializer())
                .registerTypeAdapter(TransitionState.class, new TransitionStateDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        
        try {
            
            FileWriter fe = new FileWriter(levelFilepath);
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
            ConsoleWindow.addLog("Error al guardar el archivo de nivel: no se pudo guardar el archivo "+levelFilepath, 
                    ConsoleWindow.LogCategory.error);
        }
    }
    
    private transient int maxGoId = -1;
    private transient int maxCompId = -1;
    /**
     * Carga y reemplaza el nivel actual con el contenido del archivo de nivel
     */
    public void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(ButtonBehavior.class, new ButtonBehaviorDeserializer())
                .registerTypeAdapter(TransitionState.class, new TransitionStateDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        
        String inFile = "";
        try {
            
            inFile = new String(Files.readAllBytes(Paths.get(levelFilepath)));
        } catch (IOException ex ) {
            ConsoleWindow.addLog("Error al cargar: No se pudo acceder al archivo: "+levelFilepath+" ,comprueba que el archivo exista y esta aplicacion tenga permisos de edicion",
                    ConsoleWindow.LogCategory.error);
        }
        
        if (!inFile.equals("")) {
            // reset stored ids
            GameObject.resetIds();
            Component.resetIds();
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
            ConsoleWindow.addLog("max object id: "+maxGoId+" max component id: "+maxCompId, ConsoleWindow.LogCategory.info);
        }
        
    }
    
    /**
     * Funcio de carga recursiva
     * @param obj el objeto a crear
     */
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
