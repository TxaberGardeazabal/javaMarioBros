/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import gameEngine.GameObjectDeserializer;
import gameEngine.Transform;
import imgui.ImGui;
import java.util.ArrayList;
import java.util.List;
import util.AssetPool;

/**
 * A gameobject is something that can be placed inside the game world and host components that give it functionality
 * @author txaber
 */
public class GameObject {
    private static int ID_COUNTER = 0;
    private transient int uid = -1;
    
    public String name;
    private ArrayList<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;
    private boolean isDead = false;
    
    private List<GameObject> childGOs = new ArrayList();
    private transient GameObject parent = null;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList();
        this.uid = ID_COUNTER++;
    }
    
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c: components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                }
                catch (ClassCastException e)
                {
                    e.printStackTrace();
                    assert false : "ERROR: casting component";
                }                
            }
        }
        return null;
    }
    
    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                
                components.remove(i);
                return;
                               
            }
        }
    }
    
    public void addComponent(Component c) {
        c.generateId();
        this.components.add(c);
        c.gameObject = this;
    }
    
    public void update(float dt) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }
    
    public void editorUpdate(float dt) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).editorUpdate(dt);
        }
    }
    
    public void start() {
        // the reason why this is a regular for instead of a list for
        // is because some components like the gizmo system add more components
        // to the list at start an this causes an exception in list fors
        
        for (int i = 0;i < components.size();i++) {
            components.get(i).start();
        }
    }

    public void imGui() {
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName())) {
                c.imGui();
            }
        }
    }
    
    public static void init(int maxId) {
        ID_COUNTER = maxId;
    } 

    public void destroy() {
        this.isDead = true;
        
        for (int i = childGOs.size(); i > 0; i--) {
            childGOs.get(i-1).destroy();
        }
        
        if (parent != null) {
            parent.getChildGOs().remove(this);
        }
        
        for (int i = components.size(); i > 0; i--) {
            components.get(i-1).destroy();
        }
    }
    
    public boolean isDead() {
        return this.isDead;
    }
    
    public int getUid() {
        return uid;
    }

    public ArrayList<Component> getAllComponents() {
        return components;
    }
    
    public void setNoSerialize() {
        this.doSerialization = false;
    }
    
    public boolean doSerialization() {
        return this.doSerialization;
    }

    public GameObject copy() {
        Gson gson = new GsonBuilder()
                 .registerTypeAdapter(Component.class, new ComponentDeserializer())
                 .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
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
    
    private void copyChild(GameObject copy) {
        for (GameObject obj : copy.getChildGOs()) {
            obj.generateUid();

            for (Component c : obj.getAllComponents()) {
                c.generateId();
            }

            SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
            //System.out.println(sprite.getTexture().getFilepath());
            if (sprite != null && sprite.getTexture() != null) {
                sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
            }
            
            obj.setParent(copy);
            copyChild(obj);
        }
    }
   
    public void generateUid() {
       this.uid = ID_COUNTER++;
   }
   
    public GameObject addChild(GameObject go) {
       this.childGOs.add(go);
       go.setParent(this);
       return go;
    }
    
    public void removeChild(GameObject go) {
        this.childGOs.remove(go);
    }

    public List<GameObject> getChildGOs() {
        return childGOs;
    }

    public GameObject getParent() {
        return parent;
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }
}
