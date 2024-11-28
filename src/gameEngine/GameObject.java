/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import UI.ButtonBehavior;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.ButtonBehaviorDeserializer;
import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import components.StateMachine;
import components.TransitionState;
import components.TransitionStateDeserializer;
import editor.ConsoleWindow;
import editor.OImGui;
import imgui.ImGui;
import imgui.type.ImBoolean;
import java.util.ArrayList;
import java.util.List;
import util.AssetPool;

/**
 * Los gameobject son los objetos en el mundo, puede poseer diferentes componentes para tener funcionalidades varias como graficos,
 * se organizan en jerarquias descendentes y se pueden añadir, editar y destruir tanto en runtime como en editor.
 * Todos los gameobjects contienen un transform para definir su posicion en el mundo.
 * Al guardar datos de niveles la informacion detallada de todos los gameobject del mundo es la mas importante.
 * @author txaber
 */
public class GameObject {
    private static int ID_COUNTER = 0;
    private transient int uid = -1;
    
    public String name;
    private boolean enabled;
    private ArrayList<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;
    private boolean isDead = false;
    // dirty flag, for gos
    private transient boolean isDirty;
    
    // hierarchy
    private List<GameObject> childGOs = new ArrayList();
    private transient GameObject parent = null;

    public GameObject(String name) {
        this.name = name;
        this.enabled = true;
        this.components = new ArrayList();
        this.uid = ID_COUNTER++;
        
    }
    
    public GameObject(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
        this.components = new ArrayList();
        this.uid = ID_COUNTER++;
        
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (!this.enabled && enabled) {
            isDirty = true;
        } else if (this.enabled && !enabled) {
            isDirty = true;
        }
        this.enabled = enabled;
        
        for (GameObject go : this.childGOs){
            go.setEnabled(enabled);
        }
    }
    
    public boolean isDirty() {
        return isDirty;
    }
    
    public void setIsDirty(boolean dirty) {
        this.isDirty = dirty;
    }
    
    /**
     * Busca al componente con la clase T dentro del gameobject
     * @param <T> clase que hereda de la clase componente
     * @param componentClass clase de componente a buscar
     * @return el primer componente de la clase solicitada si existe dentro del gameobject, null si no existe 
     */
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c: components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                }
                catch (ClassCastException e)
                {
                    ConsoleWindow.addLog("ERROR: casting component"+c.toString()+" to class "+componentClass.getSimpleName(),
                            ConsoleWindow.LogCategory.error);
                }                
            }
        }
        return null;
    }
    
    /**
     * Elimina el componente con la clase T dentro del gameobject.
     * nota: esta funcion solo elimina la primera instancia encontrada de objeto encontrada
     * @param <T> clase que hereda de la clase componente
     * @param componentClass clase de componente a eliminar
     */
    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
                               
            }
        }
    }
    
    /**
     * Elimina el componente que tenga el componentID ID dentro del gameobject.
     * @param <T> clase que hereda de la clase componente
     * @param ID el ComponentID a eliminar
     */
    public <T extends Component> void removeComponentById(int ID) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getUid() == ID) {
                components.remove(i);
                return;
                               
            }
        }
    }
    
    /**
     * Añade un nuevo componente al gameobject
     * @param c el nuevo componente
     */
    public void addComponent(Component c) {
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }
    
    /**
     * Funcion que se ejecuta antes del primer frame
     */
    public void start() {
        // the reason why this is a regular for instead of a list for
        // is because some components like the gizmo system add more components
        // to the list at start an this causes an exception in list fors
        
        for (int i = 0;i < components.size();i++) {
            components.get(i).start();
        }
    }
    
    /**
     * Funcion que se ejecuta cada frame
     * @param dt tiempo en segundos desde el anterior frame
     */
    public void update(float dt) {
        if (isDirty) {
            isDirty = false;
        }
        
        if (enabled) {
            for (int i = 0; i < components.size(); i++) {
                components.get(i).update(dt);
            }
            
        }
        
    }
    
    /**
     * Funcion que se ejecuta cada frame despues de la funcion update
     * @param dt tiempo en segundos desde el anterior frame
     */
    public void lateUpdate(float dt) {
        if (isDirty) {
            isDirty = false;
        }
        
        if (enabled) {
            for (int i = 0; i < components.size(); i++) {
                components.get(i).lateUpdate(dt);
            }
        }
    }
    
    /**
     * Funcion que se ejecuta cada frame solo en el editor
     * @param dt tiempo en segundos desde el anterior frame
     */
    public void editorUpdate(float dt) {
        if (isDirty) {
            isDirty = false;
        }
        
        if (enabled) {
            for (int i = 0; i < components.size(); i++) {
                components.get(i).editorUpdate(dt);
            }
        }
    }

    /**
     * Muestra todas las variables de este objeto y de todos sus componentes en la ventana de propiedades
     */
    public void imGui() {
        name = OImGui.inputText("Name: ", name);
        
        boolean enabled2 = OImGui.inputBoolean("Enabled: ", enabled);
        if (enabled2 != enabled) {
            setEnabled(enabled2);
        }
        
        if (parent != null) {
            imgui.internal.ImGui.text("Parent: "+parent.name);
        } else {
            imgui.internal.ImGui.text("Parent: none");
        }
        
        for (int i = 0; i < components.size(); i++) {
            ImBoolean test = new ImBoolean(true);
            if (ImGui.collapsingHeader(components.get(i).getClass().getSimpleName(), test)) {
                components.get(i).imGui();
                
                
            }
            if (!test.get()) {
                this.components.remove(i);
                System.out.println(components.size());
                i--;
            }
        }
    }
    
    /**
     * Inicializa la ID global al maximo ID existente
     * @param maxId 
     */
    public static void init(int maxId) {
        ID_COUNTER = maxId;
    } 

    /**
     * Destruye el gameobject, sus componentes y todos los hijos
     */
    public void destroy() {
        this.isDead = true;
        
        for (int i = components.size(); i > 0; i--) {
            components.get(i-1).destroy();
        }
        
        for (int i = childGOs.size(); i > 0; i--) {
            childGOs.get(i-1).destroy();
        }
        
        if (parent != null) {
            parent.getChildGOs().remove(this);
        }
    }
    
    public boolean isDead() {
        return this.isDead;
    }
    
    public int getUid() {
        return uid;
    }

    /**
     * Devuelve todos los componentes del gameobject
     * @return el arraylist con todos los componentes ordenados por orden de añadido
     */
    public ArrayList<Component> getAllComponents() {
        return components;
    }
    
    public void setNoSerialize() {
        this.doSerialization = false;
    }
    
    public boolean doSerialization() {
        return this.doSerialization;
    }

    /**
     * Crea una copia casi exacta de este gameobject usando Gson
     * @return un gameobject nuevo con las mismas variables que este
     */
    public GameObject copy() {
        Gson gson = new GsonBuilder()
                 .registerTypeAdapter(Component.class, new ComponentDeserializer())
                 .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                 .registerTypeAdapter(ButtonBehavior.class, new ButtonBehaviorDeserializer())
                 .registerTypeAdapter(TransitionState.class, new TransitionStateDeserializer())
                 .enableComplexMapKeySerialization()
                 .create();
        String objToGson = gson.toJson(this);
        GameObject obj = gson.fromJson(objToGson, GameObject.class);
        
        obj.generateUid();

        for (Component c : obj.getAllComponents()) {
            c.generateId();
        }
        
        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if (sprite != null && sprite.getTexture() != null) {
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
        }
        
        if (parent != null) {
            obj.setParent(parent);
            parent.addChild(obj);
        }
        
        copyChild(obj);
        
        return obj;
    }
    
    /**
     * similar a la funcion copy, recursivamente crea copias de todos los hijos a los que hace referencia un gameobject
     * @param copy objeto con las referencias a copiar
     */
    private void copyChild(GameObject copy) {
        for (GameObject obj : copy.getChildGOs()) {
            obj.generateUid();

            for (Component c : obj.getAllComponents()) {
                c.generateId();
            }

            SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);

            if (sprite != null && sprite.getTexture() != null) {
                sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
            }
            
            obj.setParent(copy);
            copyChild(obj);
        }
    }
   
    /**
     * Crea una ID unica para este objecto
     */
    public void generateUid() {
       this.uid = ID_COUNTER++;
   }
    
    /**
     * Devuelve los hijos de este objeto.
     * @return una lista con todos los hijos de este objeto
     */
    public List<GameObject> getChildGOs() {
        return childGOs;
    }
    
    /**
     * devuelve el objeto padre de este objeto
     * @return el objeto padre
     */
    public GameObject getParent() {
        return parent;
    }
   
    /**
     * Añade un hijo en la jerarquia de este objeto
     * @param go un gameobject para ser hijo
     * @return el gameobject pasado
     */
    public GameObject addChild(GameObject go) {
        if (!this.childGOs.contains(go)) {
            this.childGOs.add(go);
            go.setParent(this);
        }
       return go;
    }
    
    /**
     * Elimina un gameobject de la lista de hijos de este objeto
     * @param go el gameobject a eliminar
     */
    public void removeChild(GameObject go) {
        this.childGOs.remove(go);
    }
    
    /**
     * Asigna un padre a este objeto.
     * Nota que esta funcion no añade el objeto en la lista de hijos del padre, ni lo elimina de la lista del anterior padre si tenia,
     * para un buen uso llamar primero a padre.removeChild() y nuevoPadre.addChild()
     * @param parent el nuevo objeto padre
     */
    public void setParent(GameObject parent) {
        if (parent == null) {
            this.parent = null;
        } else if (!parent.equals(this.parent) && !parent.equals(this)) {
            this.parent = parent;
        }
    }
    
    /**
     * Busca y devuelve un hijo por un nombre.
     * @param name Nombre del go a buscar
     * @return la primera instancia de gameobject que tenga el nombre dentro de los hijos de este gameobject, si no hay un gameobject con ese nombre dvuelve null
     */
    public GameObject getChildByName(String name) {
        for (GameObject child : childGOs) {
            if (child.name.equals(name)) {
                return child;
            }
        }
        return null;
    }

    /**
     * pone la siguiente id del siguiente componente a cero, esta funcion se usa para el 
     * cambio entre escenas y su uso pueder llevar a bugs como ids duplicadas
     */
    public static void resetIds() {
        ID_COUNTER = 0;
    }
    
    /**
     * Recursivamente refresca las texturas de los componentes graficos de los objetos.
     * esta funcion esta pensada para cuando se necesite cargar un asset de juego despues del
     * cargado inicial, el propio programa busca la textura correspondiente o lo añade si no estaba ya.
     */
    public void refreshTextures() {
        if (getComponent(SpriteRenderer.class) != null) {
            SpriteRenderer spr = getComponent(SpriteRenderer.class);
            if (spr.getTexture() != null) {
                spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
            }
        }

        if (getComponent(StateMachine.class) != null) {
            StateMachine stateMachine = getComponent(StateMachine.class);
            stateMachine.refreshTextures();
        }
        
        for (GameObject go2 : getChildGOs()) {
            go2.refreshTextures();
        }
    }
}
